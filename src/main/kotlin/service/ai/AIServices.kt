package service.ai

import entity.*
import service.AbstractRefreshingService
import service.RootService
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * The `AIServices` class represents a set of services for implementing artificial intelligence (AI) agents
 * in a game. It includes functionalities for Random Agent, Monte Carlo Tree Search (MCTS) and Minimax-based agents.
 *
 * @property rootService The `RootService` instance providing access to the game state and services.
 * @constructor Creates an `AIServices` instance with the specified `RootService`.
 **/

class AIServices(private val rootService: RootService) : AbstractRefreshingService() {


    /**
     * Generates a list of all valid game positions based on the current game state.
     *
     * The function iterates through axial positions within specified bounds and checks the validity
     * of each position using the `checkPlacement` method from the `rootService.playerService`. Valid positions
     * are added to the result list `allPositions` of type MutableList<AxialPos>.
     *
     * @return A MutableList of AxialPos representing all valid game positions.
     * @throws IllegalStateException if no game has been started yet.
     */

    private fun generateAllGamePositions(): MutableList<AxialPos> {

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
                // add  only the valid  positions to the result list
                allPositions.add(AxialPos(index, secondIndex))

            }
            startIndex -= 1
        }

        // Second loop
        (1..maxQ).forEach { index ->
            (minQ..target).forEach { secondIndex ->
                allPositions.add(AxialPos(index, secondIndex))
            }
            target -= 1
        }

        return allPositions
    }


    /**
     * Finds all valid positions on the game board that are currently unoccupied.
     *
     * This function generates all possible game positions using the `generateAllGamePositions` method,
     * retrieves the current game state using the `getCurrentState` method, and then identifies
     * positions on the game board that are not currently occupied by checking against the
     * positions stored in the game state's board.
     *
     * @return A MutableList of AxialPos representing all valid, unoccupied game positions.
     */
    fun findAllValidPositions(): MutableList<AxialPos> {
        // generate all  game  positions
        val gameAllBoardPositions = generateAllGamePositions()
        //get the current game state
        val currentGameState = getCurrentState()

        val result: MutableList<AxialPos> = mutableListOf()
        // check if  the current pos is valid
        gameAllBoardPositions.forEach { axialPos ->
            if (!currentGameState.board.containsKey(axialPos)) {
                result.add(axialPos)
            }
        }
        return result

    }


    /**
     * Sets the current state of the game based on the provided GameState.
     *
     * This function is responsible for updating the current state of the game by modifying
     * the game's board, draw stack, players, and gems. It checks if a game is currently in progress
     * before applying the changes to the current game state.
     *
     * @param gameState The GameState object containing the new state information.
     * @throws IllegalStateException if no game has been started yet.
     */
    fun setCurrentState(gameState: GameState) {
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        game.currentBoard = gameState.board.toMutableMap()
        game.currentDrawStack = gameState.drawStack.toMutableList()
        game.currentPlayers = gameState.players
        game.currentGems = gameState.gems.toMutableList()
    }


    /**
     * Retrieves the current state of the game by creating a new instance of the `GameState` class
     * with the current board, draw stack, players, and gems from the provided game.
     *
     * @return The current state of the game encapsulated in a `GameState` object.
     */

    private fun getCurrentState(): GameState {

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
        return GameState(currentBoard, currentDrawStack, game.currentPlayers, game.playerAtTurn, currentGems)
    }

    /**
     * Function to retrieve all possible rotations of a single tile.
     *
     * @param tile The tile for which rotations are to be obtained.
     * @return A mutable list containing all possible rotations of the given tile.
     */

    fun getAllTilePossibleRotations(tile: RouteTile): MutableList<RouteTile> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet" }
        val result: MutableList<RouteTile> = mutableListOf()
        for (i in 0..5) {
            // create an object to save it
            val newRoutTile = RouteTile(tile.tileType)
            newRoutTile.rotation = i
            result.add(newRoutTile)

        }

        return result
    }


    /**
     * Generates all possible next game states by considering all rotations of a given tile
     * and placing it in each valid position on the game board.
     *
     * @return A mutable list containing all possible next game states.
     */
    fun getAllPossibleNextStates(): MutableList<Pair<Pair<AxialPos, Tile>, GameState>> {  // Rotation is considered here

        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        val tile = game.playerAtTurn.heldTile

        val result: MutableList<Pair<Pair<AxialPos, Tile>, GameState>> = mutableListOf()

        if (tile == null) {
            return result
        }

        val oldGameState = getCurrentState()

        val allPossibleRotations = getAllTilePossibleRotations(tile)

        val allValidPositions = findAllValidPositions()

        var count = 0

        for (i in 0 until allPossibleRotations.size) {

            for (j in 0 until allValidPositions.size) {

                game.currentBoard[allValidPositions[j]] = allPossibleRotations[i]


                // Move Gems
                if (rootService.playerService.checkPlacement(allValidPositions[j])) {
                    rootService.playerService.moveGems(allValidPositions[j])
                }

                result.add(Pair(Pair(allValidPositions[j], allPossibleRotations[i]), getCurrentState()))

                setCurrentState(oldGameState)

                count += 1
            }
            count = 0

        }
        return result

    }

    /**
     * Plays a tile randomly on the game board and returns the chosen position and rotated tile.
     *
     * This function is responsible for randomly selecting a valid position on the game board and a
     * rotation for the tile held by the current player. It ensures that a game is currently in progress,
     * and that the current player has a tile to place. The selected tile is then drawn for the next turn.
     * The function then determines all possible rotations for the tile and finds all valid positions on
     * the game board where the tile can be placed. Finally, it randomly selects a rotation and a position
     * and returns a Pair containing the chosen AxialPos and rotated Tile.
     *
     * @return A Pair containing the chosen AxialPos (position) and rotated Tile for the current turn.
     * @throws IllegalStateException if no game is currently in progress or if the current player has no tile.
     */
    fun playRandomly(): Pair<AxialPos, Tile> {

        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        // Checks if the player has a tile to place
        val tile = game.playerAtTurn.heldTile
        requireNotNull(tile) { "The current player has no tile" }

        while (true) {
            // get all possible rotations for the current tile
            val allPossibleRotations = getAllTilePossibleRotations(tile)

            //get all valid positions where the tile can be place
            val allValidPositions = findAllValidPositions()

            // choose randomly a rotation of the tile
            val tileRotation = allPossibleRotations.random()

            // choose random positions to place the tile
            val tilePosition = allValidPositions.random()

            // Check if the chosen position and rotation result in a valid placement
            val oldRotation = tile.rotation
            tile.rotation = tileRotation.rotation
            val isValid = rootService.playerService.checkPlacement(tilePosition)
            tile.rotation = oldRotation

            if (isValid) {
                return Pair(tilePosition, tileRotation)
            }
        }
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

    // larger explorationWeight value encourage randomization the agent will choose randomly
    // smaller values will encourage exploitation the agent will choose the best action
    // this hyperparameter need to adjusted properly for better performance
    // it should balance between exploration and exploitation

    fun calculateUpperConfidenceBound(node: MontiCarloNode, explorationWeight: Double = 1.0): Double {
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
     * @return The next state node selected using the UCB criterion.
     * @throws IllegalStateException if the current node is null or has no children.
     */
    /**
     * Selects the next state in the Monte Carlo Tree Search (MCTS) using the Upper Confidence Bound (UCB) criterion.
     *
     * @param currentNode The current node in the Monte Carlo Tree.
     * @return The selected next state node.
     * @throws IllegalStateException if the next state cannot be determined unexpectedly.
     */
    fun selectNextState(currentNode: MontiCarloNode): MontiCarloNode {
        // checks the current game
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        // Select the child node with the highest UCB value
        val nextState = currentNode.children.maxByOrNull {
            calculateUpperConfidenceBound(it)
        }

        return nextState ?: currentNode

    }


    /**
     * Generates a child node in the Monte Carlo Tree Search (MCTS).
     *
     * @param parentNode The parent node in the Monte Carlo Tree.
     * @param gameState The game state for the new child node.
     * @param action The action taken to reach the new state.
     */
    fun generateChildNodes(parentNode: MontiCarloNode, gameState: GameState, action: Pair<AxialPos, Tile>) {
        // generate a Child Node
        val currentChildNode = MontiCarloNode()
        // set it current state to new state
        currentChildNode.currentGameState = gameState
        // save the taken action
        currentChildNode.action = action
        // increment the depth
        currentChildNode.currentDepth = parentNode.currentDepth + 1
        // add the current child to children of the parent node
        parentNode.children.add(currentChildNode)
        // set the parent of the child to the current parent
        currentChildNode.parent = parentNode
    }


    /**
     * Performs Monte Carlo Tree Search expansion on the given MonteCarloNode.
     *
     * This function expands the search tree by generating and evaluating possible next states for the
     * provided MonteCarloNode. It retrieves all possible moves and their corresponding next states,
     * shuffles the list of next states to introduce randomness, and then expands the node by adding
     * the shuffled states to its child nodes.
     *
     * @param currentNode The MonteCarloNode to expand.
     */
    private fun montiCarloExpansion(currentNode: MontiCarloNode) {

        // get all possible  moves and their next states
        val allFutureStates: MutableList<Pair<Pair<AxialPos, Tile>, GameState>> = getAllPossibleNextStates()
        // shuffle the list that contains all the next states
        // Shuffle the elements
        allFutureStates.shuffle()

        // Select a random subset of the specified size
        //val randomSubset = allFutureStates.subList(0, butchSize)
        for ((action, nextState) in allFutureStates) {
            generateChildNodes(currentNode, nextState, action)
        }

    }


    /**
     * Performs Monte Carlo Simulation starting from the given currentNode.
     *
     * This function simulates future game states for a specified number of steps using Monte Carlo
     * Simulation. It starts the simulation from the provided currentNode and explores possible states
     * by selecting moves and evaluating their outcomes. The simulation runs for a specified number of
     * steps with a given batch size.
     *
     * @param currentNode The starting point for Monte Carlo Simulation.
     */
    private fun montiCarloSimulation(
        currentNode: MontiCarloNode,
        numberOfFutureSteps: Int
    ): MontiCarloNode {

        var startNode = currentNode

        while (true) {

            if ((startNode.currentDepth == numberOfFutureSteps)
                || findAllValidPositions().size == 0
            ) {
                return startNode
            }
            if (startNode.children.isEmpty()) {
                montiCarloExpansion(startNode)
            } else {
                startNode = selectNextState(startNode)
            }
        }

    }


    private fun assignReward(): Double {
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }
        return game.playerAtTurn.points.toDouble()
    }


    /**
     * Performs backpropagation in the Monte Carlo Tree Search.
     *
     * This function updates the scores and visit counts of nodes in the Monte Carlo Tree Search
     * by backpropagation the results from a simulated game state. It starts from the given currentNode,
     * traverses up the tree to the root, and updates the total scores and visit counts of each node
     * in the path. Additionally, it updates the game state to match the parent node's state during the
     * traversal.
     *
     * @param currentNode The MonteCarloNode representing the starting point for backpropagation.
     * @return The root of the Monte Carlo Tree after backpropagation.
     * @throws IllegalStateException if no game is currently in progress.
     */
    private fun backPropagation(currentNode: MontiCarloNode): MontiCarloNode {

        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }
        var starting = currentNode

        // the current leaf node visit is  not updated
        while (starting.parent != null) {

            // update the score
            starting.totalScore = assignReward()
            starting.parent!!.totalScore += starting.totalScore

            // update visits
            starting.visits += 1
            starting.parent!!.visits += starting.visits

            // update the state
            setCurrentState(starting.parent!!.currentGameState!!)
            starting = starting.parent!!
        }
        return starting

    }


    /**
     * Trains a Monte Carlo Tree Search-based agent for playing the game.
     *
     * This function trains a Monte Carlo Tree Search (MCTS) agent by performing a specified number
     * of simulations. It initializes the root node with the current game state, runs Monte Carlo
     * Simulation for each simulation, and backpropagates the results. The training process aims to
     * enhance the agent's decision-making capabilities over multiple simulations. The final selected
     * action is based on the statistics gathered during the training process.
     *
     * @param simulationNumber The number of Monte Carlo simulations to perform.
     * @param futureStepsNumber The number of steps to simulate into the future during each simulation.
     * @return A Pair representing the selected action (position and rotated tile) for the trained agent.
     * @throws IllegalStateException if no game is currently in progress.
     */
    fun trainMontiCarloAgent(simulationNumber: Int, futureStepsNumber: Int): Pair<AxialPos, Tile> {

        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        // Checks if the player has a tile to place
        val tile = game.playerAtTurn.heldTile
        requireNotNull(tile) { "The current player has no tile" }
        while (true) {

            val initialStateNode = MontiCarloNode()
            initialStateNode.currentGameState = getCurrentState()
            for (i in 0 until simulationNumber) {
                val leafNode = montiCarloSimulation(initialStateNode, futureStepsNumber)

                backPropagation(leafNode)

            }

            val nextStateAction = selectNextState(initialStateNode).action

            //game.currentBoard.remove(nextStateAction!!.first)

            // Check if the chosen position and rotation result in a valid placement
            val tilePosition = nextStateAction!!.first
            val tileRotation = nextStateAction.second

            tile.rotation = tileRotation.rotation
            val isValid = rootService.playerService.checkPlacement(tilePosition)

            if (isValid) {
                return Pair(tilePosition, tileRotation)
            }
        }
    }

}