package service.ai

import entity.*
import service.AbstractRefreshingService
import service.RootService
import kotlin.math.ln
import kotlin.math.sqrt

class AIServices (private val rootService: RootService) : AbstractRefreshingService() {


    /**
     * Generates all positions on the game-board and checks each position using the `checkPlacement` function
     * from the `PlayerService` class. If the current position is valid, it is added to the list of valid positions.
     *
     * @return A mutable list of valid Axial positions on the game-board.
     */
    fun generateAllGamePositions(): MutableList<AxialPos> {

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
                //if (rootService.playerService.checkPlacement(AxialPos(index, secondIndex))) {
                allPositions.add(AxialPos(index, secondIndex))
                // }

            }
            startIndex -= 1
        }

        // Second loop
        (1..maxQ).forEach { index ->
            (minQ..target).forEach { secondIndex ->

                //if (rootService.playerService.checkPlacement(AxialPos(index, secondIndex))) {

                allPositions.add(AxialPos(index, secondIndex))
                // }

            }
            target -= 1
        }

        return allPositions
    }

    // checks if two states are equal or not
    /**
     * Checks if two game states are equal by comparing their board, draw stack, players, and gems.
     *
     * @param gameState1 The first game state to compare.
     * @param gameState2 The second game state to compare.
     * @return `true` if the game states are equal, `false` otherwise.
     */
    fun areGameStatesEqual(gameState1: GameState, gameState2: GameState): Boolean {
        return (gameState1.board == gameState2.board
                && gameState1.drawStack == gameState2.drawStack
                && gameState1.players == gameState2.players
                && gameState1.gems == gameState2.gems)
    }


    fun findAllValidPositions(): MutableList<AxialPos> {
        // generate all  game  positions
        val gameAllBoardPositions = generateAllGamePositions()
        //get the current game state
        val currentGameState  = getCurrentState()

        val result : MutableList<AxialPos> = mutableListOf()
        // check if  the current pos is valid
        gameAllBoardPositions.forEach { axialPos ->
            if (!currentGameState.board.containsKey(axialPos) ) {
                result.add(axialPos)
            }
        }
        return result

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

    fun getAllTilePossibleRotations(tile:RouteTile ): MutableList<RouteTile> {
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}
        val result  : MutableList<RouteTile> = mutableListOf()
        for ( i  in 0 ..5 ){
            // create an object to save it
            val newRoutTile = RouteTile(tile.tileType)
            newRoutTile.rotation =  i
            result.add(newRoutTile)

        }

        return result
    }

    /**
     * Updates the draw stack by removing the specified tile and assigning it to the player at turn.
     *
     * @param tile The tile to be removed from the draw stack and assigned to the player.
     */
    fun updateDrawStack(tile: Tile) {
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
    fun getAllPossibleNextStates():  MutableList<Pair<Pair<AxialPos, Tile>,GameState> > {  // Rotation is considered here

        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        // Checks if the player has a tile to place
        val tile = game.playerAtTurn.heldTile
        // print("the current chosen tile is $tile\n\n\n")

        val result   : MutableList<Pair<Pair<AxialPos, Tile>,GameState> > = mutableListOf()

        if ( tile == null ){
            return result
        }

        // this will save the old state
        val oldGameState = getCurrentState()

        // pick a tile for next run
        rootService.playerService.drawTile()

        // get all possible rotations for the current tile
        val allPossibleRotations = getAllTilePossibleRotations(tile)

        //get all valid positions  where the tile can be place
        val allValidPositions = findAllValidPositions()

        var count = 0

        for (i in 0 until allPossibleRotations.size) { // each tile which are 6

            // set the current held tile of the current player to  the current tile and remove it from

            for (j in 0 until allValidPositions.size) { // each valid  position





                //print("index of tail : $i, type of style: ${allPossibleRotations[i]} ,rotation :${allPossibleRotations[i].rotation} , placed in position : ${allValidPositions[j]} , index j : ${j}  ,count :$count \n" )

                //print( "the current game Board  size before placement   : ${getCurrentState().board.size} \n")



                // place a tile  and get the new state and set it to the current game state
                game.currentBoard[allValidPositions[j]] = allPossibleRotations[i]


                // Move Gems
                if (rootService.playerService.checkPlacement(allValidPositions[j])) {
                    rootService.playerService.moveGems(allValidPositions[j])
                }

                //print( "the current game Board  size after placement   : ${getCurrentState().board.size} \n")


                //print("the current result List is  : $result \n\n")

                result.add(Pair(Pair(allValidPositions[j], allPossibleRotations[i]) ,getCurrentState()))

                // reset the game state to the previous state

                setCurrentState(oldGameState)

                //print( "the current game Board  size after restart   : ${getCurrentState().board.size} \n")

                count+=1
            }
            // this need to be removed add it  for tracking
            count  = 0



        }

        return result


    }


    fun playRandomly(): Pair<AxialPos, Tile> {  // Rotation is considered here

        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        // Checks if the player has a tile to place
        val tile = game.playerAtTurn.heldTile
        requireNotNull(tile) { "The current player has no tile" }

        print("the current tile is the Following  : $tile\n\n\n")
        // pick a tile for next run
        rootService.playerService.drawTile()

        // get all possible rotations for the current tile
        val allPossibleRotations = getAllTilePossibleRotations(tile)

        //get all valid positions  where the tile can be place
        val allValidPositions = findAllValidPositions()

        // choose randomly a  rotation of the tile
        val tileRotation = allPossibleRotations.random()
        // choose randomly a valid positions

        val tilePosition = allValidPositions.random()
        // return the given action

        return Pair(tilePosition, tileRotation)


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
        } ?: throw IllegalStateException("the current child has no  children § ")

        // this ensures moving to the new state
        setCurrentState(nextState.currentGameState!!)

        return nextState
    }

    /**
     * Generates a child node in the Monte Carlo Tree Search (MCTS).
     *
     * @param parentNode The parent node in the Monte Carlo Tree.
     * @param gameState The game state for the new child node.
     * @param action The action taken to reach the new state.
     */
    fun generateChildNodes (parentNode : MontiCarloNode, gameState : GameState, action : Pair<AxialPos, Tile>){
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
    fun montiCarloExpansion (currentNode : MontiCarloNode){


        // get all possible  moves and their next states
        val allFutureStates :  MutableList<Pair<Pair<AxialPos, Tile>,GameState> >  = getAllPossibleNextStates()

        for ((action, nextState) in allFutureStates) {

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
    fun isTerminate ( currentNode: MontiCarloNode , maxDepth : Int): Boolean{
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
     * @param currentNode The current node in the Monte Carlo Tree.
     * @param maxDepth The maximum depth to consider during the search.
     * @return The resulting node after the simulation.
     */

    fun montiCarloSimulation(currentNode : MontiCarloNode ,maxDepth: Int):  MontiCarloNode {

        var node = currentNode

        while (true ){
            if (isTerminate(node , maxDepth))   {
                //print("the current valid positions before selection : ${findAllValidPositions()} ,
                // then number of states : ${currentNode.children.size} , currentDepth:${currentNode.currentDepth} ,
                // DrawStackSize:${getCurrentState().drawStack.size} \n\n")
                return node
            }

            if (node.children.isEmpty()) {
                // has no children expend
                montiCarloExpansion(node)
            }else{
                //print("the current valid positions before selection : ${findAllValidPositions().size} ,
                // then number of states : ${currentNode.children.size} ,
                // drawStack size ${getCurrentState().drawStack.size}\n\n")
                node = selectNextState(node)
                //print("the current valid positions after selection : ${findAllValidPositions().size} ,
            // then number of states : ${currentNode.children.size} ,
            // drawStack size ${getCurrentState().drawStack.size}\n\n")

            }
        }

    }

    /**
     * Assigns a reward to a Monte Carlo Tree Search (MCTS) node based on the given gems' threshold.
     *
     * @param gemsThreshold The threshold of gems to consider for the reward.
     * @return A reward value: -1.0 if the player's points are below the threshold, 1.0 otherwise.
     */
    fun assignReward (gemsThreshold: Int): Double{
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }
        return if (  game.playerAtTurn.points.toDouble()  < gemsThreshold ){
            // assign negative value
            -1.0
        }else {
            1.0
        }
    }


    fun assignRewardMinMax (gemsThreshold: Int): Double{
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }
        return if (  game.playerAtTurn.points.toDouble()  < gemsThreshold ){
            // assign negative value
            -1.0
        }else {
            1.0
        }
    }


    /**
     * Backpropagates the results of Monte Carlo Tree Search (MCTS) from a leaf node to the root.
     *
     * @param threshold The threshold parameter for assigning rewards during backpropagation.
     * @param currentNode The current node in the Monte Carlo Tree.
     */
    fun backPropagation (threshold: Int ,currentNode: MontiCarloNode) : MontiCarloNode{

        // Checks if a game is running

        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        var node=currentNode
        var count = 0
        while (node.parent != null && count <=10) {

            //print(" the current valid positions  : ${findAllValidPositions().size}\n\n\n")
            // update the score of the current node
            node.totalScore = assignReward(threshold)
            // update the score of the parent node
            node.parent!!.totalScore  += node.totalScore
            // increment the number of visits
            node.parent!!.visits += 1
            //print(" the current node visits  : ${currentNode.visits}\n\n\n")
            // move to the parent node
            setCurrentState(node.parent!!.currentGameState!!)

            node = node.parent!!
            // set the game state to the new game state
            count+=1
        }

        return  node
    }

    /**
     * Train the agent using Monte Carlo Tree Search (MCTS) and return the best action after training.
     *
     * @param numberOfSimulation The number of simulations to perform during training.
     * @param depth The maximum depth for each simulation in the MCTS.
     * @param collected The parameter for the backpropagation function.
     * @return The best action determined by the MCTS after training, or null if no action is selected.
     */
    fun trainMontiCarloAgent(numberOfSimulation: Int, depth: Int = 2, collected: Int): Pair<AxialPos, Tile>? {
        // Checks if a game is running
        val initialStateNode = MontiCarloNode()
        initialStateNode.currentGameState = getCurrentState()
        montiCarloExpansion(initialStateNode)

        repeat(getAllPossibleNextStates().size) {
            // select  next state
            val selectedNextState = selectNextState(initialStateNode)
            //println(" the current selected node : ${selectedNextState.action}  , Rotation  :${selectedNextState.action!!.second.rotation}")
            // number of repetition is the size of the number of possible state from the given state
            for (i in 0 until numberOfSimulation - 1) {

                val leafNode = montiCarloSimulation(selectedNextState, depth)
                backPropagation(collected, leafNode)
            }
        }
        setCurrentState(initialStateNode.currentGameState!!)
        val result = selectNextState(initialStateNode)
        println(result.action)

        //after training select the best action  from the given state
        return result.action
    }



    // min max algorithm



    private fun minMaxExpansion(currentNode  : MinMaxNode) {

        currentNode.currentGameState = getCurrentState()
        currentNode.playerType = "Maximizer"

        // get all possible  moves and their next states
        val allFutureStates: MutableList<Pair<Pair<AxialPos, Tile>, GameState>> = getAllPossibleNextStates()
        for ((action, nextState) in allFutureStates) {

            // generate a Child Node
            val currentChildNode = MinMaxNode()
            // set it current state to new state
            currentChildNode.currentGameState = nextState
            // save the taken action
            currentChildNode.action = action

            // add the current child to children of the parent node
            currentNode.children.add(currentChildNode)
            // set the parent of the child to the current parent
            currentChildNode.parent = currentNode
            // set  also the type of the new node
            if (currentNode.parent != null) {
                if (currentNode.parent!!.playerType == "Maximizer") {
                    currentNode.playerType == "Minimizer"

                } else {
                    currentNode.playerType == "Maximizer"
                }
            }

        }

    }

    fun minMaxSimulation (currentNode: MinMaxNode, threshold: Int ) : MutableList<MinMaxNode>{
        // save the leaf nodes
        val leafNode   = mutableListOf<MinMaxNode>()

        if (currentNode.children.size == 0 ){ // leaf node
            currentNode.score  = assignRewardMinMax(threshold)
            currentNode.parent?.let { leafNode.add(it) }
        }

        var node = currentNode
        for (child  in node.children  ){
            node = child
            minMaxExpansion(node)
            minMaxSimulation ( node ,threshold )
        }
        return leafNode
    }

    fun  minMaxBackpropagation (nodeList : MutableList<MinMaxNode>){

        var currentNode: MinMaxNode
        for( node in nodeList){
            currentNode = node
            while ( currentNode.parent!!.parent != null ) {

                if (currentNode.playerType == "Maximizer") {
                    val selectedChild: MinMaxNode = node.children.minBy { child -> child.score }
                    currentNode.score  = selectedChild.score
                    currentNode = currentNode.parent!!

                }else{
                    val selectedChild: MinMaxNode = node.children.maxBy { child -> child.score }
                    currentNode.score  = selectedChild.score
                    currentNode = currentNode.parent!!
                }
            }
        }
    }


    fun trainMinMax ( ): Pair<AxialPos, Tile>? {

        val threshold  =  2
        // here the train min max
        val currentState  =  MinMaxNode()
        currentState.currentGameState = getCurrentState()
        currentState.playerType = "Maximizer"
        //  simulation
        val resultList  = minMaxSimulation(currentState  , threshold  )
        // backpropagation
        minMaxBackpropagation(resultList)
        // choose the best action

        val chosenNextState : MinMaxNode  = currentState.children.maxBy { child -> child.score }

        return chosenNextState.action

    }

}