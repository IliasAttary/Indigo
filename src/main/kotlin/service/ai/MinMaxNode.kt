package service.ai

import entity.AxialPos
import entity.GameState
import entity.Tile


class MinMaxNode {

    var children: MutableList<MinMaxNode> = mutableListOf()
    var parent: MinMaxNode? = null
    var currentGameState : GameState? = null
    var action: Pair<AxialPos, Tile>? = null   // Adjust the type of 'action' to AxialPos
    var playerType : String = "  " // this will specify if the current player is maximizer or minimizer
    var score  : Double  = 0.0

}