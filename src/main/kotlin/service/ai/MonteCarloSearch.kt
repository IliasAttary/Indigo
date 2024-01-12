package service.ai
import service.RootService
import kotlin.math.sqrt
import kotlin.math.ln
import entity.GameState

class MonteCarloSearch (private val rootService:RootService ,
                        val simulationCount: Int = 1000 ,  aiIndex: Int )   {


    /**
     * Calculates the Upper Confidence Bound (UCB) for a given node in a tree structure.
     *
     * @param node The node for which to calculate the UCB.
     * @param explorationWeight The weight given to exploration (default is 1.0).
     * @return The computed UCB value for the node.
     */
    private fun calculateUpperConfidenceBound(node: Node, explorationWeight: Double = 1.0): Double {
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
     * Selects the next node in the tree for exploration based on the Upper Confidence Bound (UCB) criterion.
     *
     * @param node The starting node from which to begin the selection.
     * @return The selected node for further exploration.
     */
    private fun selectNextNode(node: Node): Node {
        // Initialize the current node for traversal
        var current = node

        // Traverse down the tree until a leaf node (no children) is reached
        while (current.children.isNotEmpty()) {
            // Select the child with the maximum UCB value using the calculateUpperConfidenceBound function
            current = current.children.maxByOrNull {
                calculateUpperConfidenceBound(it)
            }!!
        }

        // Return the selected leaf node for further exploration
        return current
    }


    /**
     * Expands the children of the given node by generating all valid states based on the current game state.
     *
     * @param currentNode The node to expand by creating children nodes.
     * @return True if the expansion was successful and children were added, false otherwise.
     */
    private fun expansion(currentNode: Node): Boolean {
        // Get the current game from the root service
        val game = rootService.currentGame

        // Check if the current game is valid (i.e., started)
        checkNotNull(game) { "No game started yet ;(." }

        // Find all valid positions to make a move from the current state/node
        val validPos = rootService.helpFunctions.findCurrentValidPositions(game.currentBoard)

        // Create child nodes for all possible states based on the available positions
        for (i in 0 until validPos.size) {
            // Create a new child node
            val currentCreatedChild = Node()

            // Set the depth of the child nodes
            currentCreatedChild.currentDepth = currentNode.currentDepth + 1

            // Set the parent of the new child to the current node
            currentCreatedChild.parent = currentNode

            // Add the new child to the children of the current parent
            currentNode.children.add(currentCreatedChild)

            // Take the action "place tile" in the current valid position, updating the game state
            rootService.helpFunctions.playAction(validPos[i])

            // Assign the taken action to the current child
            currentCreatedChild.action = validPos[i]

            // Set the new state of the game to the current child
            val newGameState = GameState(
                game.currentBoard,
                game.currentDrawStack,
                game.currentPlayers,
                game.currentGems
            )

            currentCreatedChild.currentGameState = newGameState

            // Reset the current game state to the previous state (undo the action)
            game.currentBoard = currentNode.currentGameState!!.board
            game.currentDrawStack = currentNode.currentGameState!!.drawStack
            game.currentPlayers = currentNode.currentGameState!!.players
            game.currentGems = currentNode.currentGameState!!.gems.toMutableList()
        }

        // Shuffle the children to introduce randomness
        currentNode.children.shuffle()

        // Return true if children were added during expansion, false otherwise
        return currentNode.children.isEmpty()
    }


    /**
     * Simulates the game by choosing next states until a terminal state or a specified depth is reached.
     *
     * @param depth The maximum depth to simulate the game up to.
     * @param currentNode The starting node for simulation.
     * @return The node representing the simulated state after the simulation process.
     */
    fun simulate(depth: Int, currentNode: Node): Node {
        // Get the current game from the root service
        val game = rootService.currentGame

        // Check if the current game is valid (i.e., started)
        checkNotNull(game) { "No game started yet ;(." }

        // Declare the chosen state before the loop
        var chosenState = currentNode

        // Keep choosing next states until a terminal state or the specified depth is reached
        while (!rootService.helpFunctions.isTerminal(rootService.helpFunctions.findCurrentValidPositions(game.currentBoard)) && currentNode.currentDepth <= depth) {
            // Choose the next node using the selectNextNode function (this can be turned to select randomly a child node)
            chosenState = selectNextNode(currentNode)

            // Expand the chosen state by creating children nodes
            expansion(chosenState)
        }

        // Return the node representing the simulated state after the simulation process
        return chosenState
    }


    /**
     * Applies the backpropagation step after the simulation by updating the scores and visit counts in the tree.
     *
     * @param chosenState The node representing the state that was chosen for simulation.
     * @param aiHaveWon A boolean indicating whether the AI has won in the simulated game.
     */
    fun applyBackpropagation(chosenState: Node, aiHaveWon: Boolean) {
        // Apply the backpropagation
        var currentState = chosenState

        // Traverse up the tree from the chosen state's parent to the root
        while (currentState.parent != null) {
            // Update the total score if AI has won
            if (aiHaveWon) {
                currentState.totalScore += 1
            }

            // Increment the visit count for the current node
            currentState.visits += 1

            // Move to the parent node
            currentState = currentState.parent!!
        }
    }
    fun train(currentNode: Node  , depth : Int)  {
        // input  state   , node
        // output action
        //  score


    }



}