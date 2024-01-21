package service.ai

import entity.AxialPos
import entity.GameState
import entity.Tile

/**
 * The `MinMaxNode` class represents a node in the Minimax tree used for Minimax algorithm simulations.
 *
 * It contains information about the current game state, associated actions, parent-child relationships,
 * player types (Maximizer or Minimizer), scores, and child nodes.
 *
 * @property children A MutableList containing child nodes of the current MinMaxNode.
 * @property parent The parent MinMaxNode in the Minimax tree.
 * @property currentGameState The GameState associated with the current MinMaxNode.
 * @property action The action (position and rotated tile) that led to the current game state.
 * @property playerType A String specifying the player type ("Maximizer" or "Minimizer") for the current node.
 * @property score A Double representing the score associated with the current MinMaxNode.
 * @constructor Creates a MinMaxNode with default initial values for its properties.
 **/
class MinMaxNode {

    var children: MutableList<MinMaxNode> = mutableListOf()
    var parent: MinMaxNode? = null
    var currentGameState: GameState? = null
    var action: Pair<AxialPos, Tile>? = null   // Adjust the type of 'action' to AxialPos
    var playerType: String = "  " // this will specify if the current player is maximizer or minimizer
    var score: Double = 0.0

}