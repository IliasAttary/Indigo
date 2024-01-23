package entity

import kotlinx.serialization.Serializable

/**
 * This Entity represents the state of the Game
 * @property board is a map which will represent the board of the game
 * @property drawStack the stack that will be used to draw a tile
 * @property players a list contains the players of the game
 * @property playerAtTurn the player whose turn it is
 * @property gems a list contains the available gems on the board.
 */
@Serializable
data class GameState(
    val board: MutableMap<AxialPos, Tile>,
    val drawStack: MutableList<RouteTile>,
    val players: List<Player>,
    val playerAtTurn: Player,
    val gems: MutableList<Gem>
)
{

}