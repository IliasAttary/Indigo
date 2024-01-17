package service.ai

import entity.*
import service.AbstractRefreshingService
import service.RootService
import kotlin.math.ln
import kotlin.math.sqrt

class AIServices (private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Generates all positions on the game board and checks each position using the `checkPlacement` function
     * from the `PlayerService` class. If the current position is valid, it is added to the list of valid positions.
     *
     * @return A mutable list of valid Axial positions on the game board.
     */
    fun findAllValidPositions(): MutableList<AxialPos> {

        val game = rootService.currentGame

        checkNotNull(game) { "No game started yet" }


        val allPositions = mutableListOf<AxialPos>()

        val minQ = -4
        val maxQ = 4
        var startIndex = 0
        var target = 3

        // First loop
        (minQ..0).forEach { index ->
            (startIndex..4).forEach { secondIndex ->
                // add only the valid positions to the result list
                if (rootService.playerService.checkPlacement(AxialPos(index, secondIndex))) {
                    allPositions.add(AxialPos(index, secondIndex))
                }

            }
            startIndex -= 1
        }

        // Second loop
        (1..maxQ).forEach { index ->
            (minQ..target).forEach { secondIndex ->

                if (rootService.playerService.checkPlacement(AxialPos(index, secondIndex))) {

                    allPositions.add(AxialPos(index, secondIndex))
                }

            }
            target -= 1
        }

        return allPositions
    }


    /**
     * Updates the current state of the game based on the provided `gameState`.
     * Assumes that a game is currently in progress.
     *
     * @param gameState The GameState containing the updated game state information.
     *                  It should include the board, draw stack, players, and gems.
     *                  If no game is currently running, an IllegalStateException is thrown.
     */

    fun setCurrentState(gameState: GameState) {
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        game.currentBoard = gameState.board
        game.currentDrawStack = gameState.drawStack
        game.currentPlayers = gameState.players
        game.currentGems = gameState.gems
    }

    /**
     * Retrieves the current state of the game by creating a new instance of the `GameState` class
     * with the current board, draw stack, players, and gems from the provided game.
     *
     * @return The current state of the game encapsulated in a `GameState` object.
     */

    fun getCurrentState(): GameState {

        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        val currentBoard: MutableMap<AxialPos, Tile> = mutableMapOf()
        for (element in game.currentBoard) {
            currentBoard[element.key] = element.value
        }

        val currentDrawStack: MutableList<RouteTile> = mutableListOf()
        for (element in game.currentDrawStack) {
            currentDrawStack.add(element)
        }

        val currentGems: MutableList<Gem> = mutableListOf()
        for (element in game.currentGems) {
            currentGems.add(element)
        }
        return GameState(currentBoard, currentDrawStack, game.currentPlayers, currentGems)
    }

    /**
     * Function to retrieve all possible rotations of a single tile.
     *
     * @param tile The tile for which rotations are to be obtained.
     * @return A mutable list containing all possible rotations of the given tile.
     */

    fun getAllTilePossibleRotations(tile: Tile): MutableList<Tile> {
        // set the tile rotation as initial state to 0
        val resultList = mutableListOf<Tile>()
        for (i in 0..5) {
            tile.rotation = i
            // add the tile with it corresponding  rotation
            resultList.add(tile)

        }

        return resultList // size is six representing all possible rotation of  a tile
    }

    /**
     * Updates the draw stack by removing the specified tile and assigning it to the player at turn.
     *
     * @param tile The tile to be removed from the draw stack and assigned to the player.
     */
    private fun updateDrawStack(tile: Tile) {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        val drawStack = game.currentDrawStack

        // Get the index of the element to be removed
        val indexToRemove = drawStack.indexOf(tile)

        // Checks if the drawStack is not empty
        if (drawStack.isNotEmpty()) {
            val drawnTile = drawStack.removeAt(indexToRemove)
            game.playerAtTurn.heldTile = drawnTile
        } else {
            game.playerAtTurn.heldTile = null
        }
    }


    /**
     * Generates all possible next game states by considering all rotations of a given tile
     * and placing it in each valid position on the game board.
     *
     * @return A mutable list containing all possible next game states.
     */
    private fun getAllPossibleNextStates():  MutableMap<Pair<AxialPos, Tile>, GameState> {  // Rotation is considered here

        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        // Checks if the player has a tile to place
        val tile = game.playerAtTurn.heldTile
        requireNotNull(tile) { "The current player has no tile" }

        val oldGameState = getCurrentState()
        // get all Possible Rotation of  a tile

        // pick a tile for next run
        rootService.playerService.drawTile()

        // get all possible rotations for the current tile
        val allPossibleRotations = getAllTilePossibleRotations(tile)

        //get all valid positions  where the tile can be place
        val allValidPositions = findAllValidPositions()


        val newGameStateMap: MutableMap<Pair<AxialPos, Tile>, GameState> = mutableMapOf()

        for (i in 0..allPossibleRotations.size) { // each tile which are 6

            // set the current held tile of the current player to  the current tile and remove it from
            // the draw stack
            updateDrawStack(allPossibleRotations[i])

            for (j in 0..allValidPositions.size) { // each valid  position
                // Place tile
                game.currentBoard[allValidPositions[j]] = allPossibleRotations[i]
                // Move Gems
                rootService.playerService.moveGems(allValidPositions[j])
                // save the new Game state
                newGameStateMap[Pair(allValidPositions[j], allPossibleRotations[i])] = getCurrentState()
                // restart the state to the previous state
                setCurrentState(oldGameState)
            }

        }
        return newGameStateMap
    }


    /**
     * Calculates the Upper Confidence Bound (UCB) for a given Monte Carlo Tree Search (MCTS) node.
     *
     * @param node The node for which the UCB is calculated.
     * @param explorationWeight The weight given to exploration in the UCB formula (default is 1.0).
     * @return The calculated Upper Confidence Bound for the specified node.
     *
     * UCB is a combination of exploitation (average score of node's children) and exploration terms.
     * If the node is the root or unvisited, returns positive infinity to prioritize exploration.
     * The UCB formula balances exploitation and exploration to guide MCTS in selecting promising nodes.
     */

    private fun calculateUpperConfidenceBound(node: MontiCarloNode, explorationWeight: Double = 1.0): Double {
        // Handling the case when the node is the root (no parent) or unvisited.
        if (node.parent == null || node.visits == 0) {
            return Double.POSITIVE_INFINITY
        }

        // Exploitation term: average score of the node's children
        val exploitationTerm = node.totalScore / node.visits.toDouble()

        // Exploration term: encourages exploration based on the number of visits and the parent's visits
        val explorationTerm = explorationWeight * sqrt(ln(node.parent!!.visits.toDouble()) / node.visits.toDouble())

        // The sum of exploitation and exploration terms represents the Upper Confidence Bound
        return exploitationTerm + explorationTerm
    }


    /**
     * Selects the next state in the Monte Carlo Tree Search (MCTS) based on the Upper Confidence Bound (UCB).
     *
     * @param currentNode The current node from which to select the next state.
     * @return The next state node selected using the UCB criterion.
     * @throws IllegalStateException if the current node is null or has no children.
     */

    private fun selectRandomly(currentNode: MontiCarloNode): MontiCarloNode {
        return currentNode.children.random()
    }

    /**
     * Selects the next state in the Monte Carlo Tree Search (MCTS) using the Upper Confidence Bound (UCB) criterion.
     *
     * @param currentNode The current node in the Monte Carlo Tree.
     * @return The selected next state node.
     * @throws IllegalStateException if the next state cannot be determined unexpectedly.
     */
    private fun selectNextState(currentNode: MontiCarloNode): MontiCarloNode {


        // Select the child node with the highest UCB value
        val nextState = currentNode.children.maxByOrNull {
            calculateUpperConfidenceBound(it)
        } ?: throw IllegalStateException("Unexpected state: Unable to determine next state.")

        return nextState
    }

    /**
     * Generates a child node in the Monte Carlo Tree Search (MCTS).
     *
     * @param parentNode The parent node in the Monte Carlo Tree.
     * @param gameState The game state for the new child node.
     * @param action The action taken to reach the new state.
     */
    private fun generateChildNodes (parentNode : MontiCarloNode, gameState : GameState, action : Pair<AxialPos, Tile>){
        // generate a Child Node
        val currentChildNode = MontiCarloNode()
        // set it current state to new state
        currentChildNode.currentGameState  = gameState
        // save the taken action
        currentChildNode.action = action
        // increment the depth
        currentChildNode.currentDepth += parentNode.currentDepth

        // add the current child to children of the parent node
        parentNode.children.add(currentChildNode)
        // set the parent of the child to the current parent
        currentChildNode.parent = parentNode
    }
    /**
     * Performs the expansion step in the Monte Carlo Tree Search (MCTS) from a given node.
     *
     * @param currentNode The current node in the Monte Carlo Tree.
     */
    private fun montiCarloExpansion (currentNode : MontiCarloNode){
        // get all possible  moves and their next states
        val allFutureStates :  MutableMap<Pair<AxialPos, Tile> , GameState > = getAllPossibleNextStates()
        for ((action, nextState) in allFutureStates) {
            // for all states
            generateChildNodes(currentNode, nextState, action)

        }

    }


    // end condition --> no more valid positions  , no more cards in the draw stuck , or reached certain depth
    /**
     * Checks whether the Monte Carlo Tree Search (MCTS) should terminate based on certain conditions.
     *
     * @param currentNode The current node in the Monte Carlo Tree.
     * @param maxDepth The maximum depth to consider during the search.
     * @return `true` if the search should terminate, otherwise `false`.
     */
    private fun isTerminate ( currentNode: MontiCarloNode , maxDepth : Int): Boolean{
        val  currentState  = currentNode.currentGameState
        // check if no more valid positions to play
        var booleanResult = false
        if (currentState != null) {
            booleanResult=  findAllValidPositions().size ==0 || currentState.drawStack.isEmpty() || currentNode.currentDepth ==maxDepth
        }
        return booleanResult
    }
    /**
     * Performs the Monte Carlo Tree Search (MCTS) simulation from a given node.
     *
     * @param node The current node in the Monte Carlo Tree.
     * @param maxDepth The maximum depth to consider during the search.
     * @return The resulting node after the simulation.
     */

    private fun montiCarloSimulation(node : MontiCarloNode, maxDepth: Int):  MontiCarloNode {

        // Checks if a game is running
        var currentNode = node

        while (!isTerminate(currentNode, maxDepth)){
            if (currentNode.children.isEmpty()) {
                // expansion
                montiCarloExpansion(currentNode)
            }else{
                //select randomly
                currentNode = selectRandomly(currentNode)
            }
        }

        return currentNode

    }

    /**
     * Assigns a reward to a Monte Carlo Tree Search (MCTS) node based on the given gem's threshold.
     *
     * @param gemsThreshold The threshold of gems to consider for the reward.
     * @param node The current node in the Monte Carlo Tree.
     * @return A reward value: -1.0 if the player's points are below the threshold, 1.0 otherwise.
     */
    private fun assignReward (gemsThreshold: Int, node: MontiCarloNode): Double{
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }
        if (game.playerAtTurn.points.toDouble()  < gemsThreshold ){
            // assign negative value
            return -1.0
        }else {
            return 1.0
        }
    }

    /**
     * Backpropagates the results of Monte Carlo Tree Search (MCTS) from a leaf node to the root.
     *
     * @param threshold The threshold parameter for assigning rewards during backpropagation.
     * @param node The current node in the Monte Carlo Tree.
     */
    private fun backPropagation (threshold: Int, node: MontiCarloNode){
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }
        while (node.parent?.parent != null) {
            var currentNode = node
            // maximum reached value
            currentNode.totalScore = assignReward(threshold, currentNode)
            currentNode.visits += 1
            currentNode = currentNode.parent!!
        }

    }

    /**
     * Train the agent using Monte Carlo Tree Search (MCTS) and return the best action after training.
     *
     * @param numberOfSimulation The number of simulations to perform during training.
     * @param depth The maximum depth for each simulation in the MCTS.
     * @param collected The parameter for the backpropagation function.
     * @return The best action determined by the MCTS after training, or null if no action is selected.
     */
    fun trainAgent(numberOfSimulation: Int, depth: Int = 32, collected: Int): Pair<AxialPos, Tile>? {
        // Checks if a game is running


        val initialStateNode = MontiCarloNode()
        repeat( getAllPossibleNextStates().size) {
            // set the current state to the initial state


            initialStateNode.currentGameState = getCurrentState()

            // do an expansion to the current state

            montiCarloExpansion(initialStateNode)

            // select  next state

            val selectedNextState = selectNextState(initialStateNode)

            // number of repetition is the size of the number of possible state from the given state
            for (i in 0..numberOfSimulation) {

                val leafNode= montiCarloSimulation(selectedNextState, depth)
                backPropagation(collected , leafNode )
            }
        }


        //after training select the best action  from the given state

        return selectNextState(initialStateNode).action

    }





}