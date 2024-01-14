package service.ai

import entity.AxialPos
import entity.GameState

class Node(currentState: GameState) {

    /**
     * Represents a node in the context of a Monte Carlo Tree Search (MCTS) algorithm.
     * Each node corresponds to a specific game state.
     *
     * @property visits The number of times this node has been visited during the search.
     * @property totalScore The total score accumulated for this node during simulations.
     * @property children The list of child nodes representing possible actions or future states.
     * @property parent The parent node from which this node was reached.
     * @property currentGameState The game state associated with this node.
     * @property currentDepth The depth of this node in the tree.
     * @property action The action (e.g., move or decision) that led to this node's state.
     */


    //attributes
    var visits: Int = 0
    var totalScore: Double = 0.0 // wins
    var children: MutableList<Node> = mutableListOf()
    var parent: Node? = null
    var currentGameState : GameState? = null
    var currentDepth : Int=0
    var action: AxialPos? = null  // Adjust the type of 'action' to AxialPos



    override fun toString(): String {
        return "Node(visits=$visits, totalScore=$totalScore,)"
    }


}

