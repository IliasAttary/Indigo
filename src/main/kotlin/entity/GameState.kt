package entity

/**
 * This Entity represents the state of the Game
 * @property board is a map which will represent the board of the game
 * @property drawStack the stack that will be used to draw a tile
 * @property players a list contains the players of the game
 * @property gems a list contains the available  gems on the board
 */
data class GameState(
    val board: Map<AxialPos, Tile>,
    val drawStack: MutableList<Tile>,
    val players: MutableList<Player>,
    val gems: MutableList<Gem>
)
