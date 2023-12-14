package entity

import kotlinx.serialization.Serializable

/**
 * Entity class that represents a game state of "Indigo"
 * @property aiMoveMilliseconds the time that an AI player needs to finish a step
 * @property currentBoard to represent the board of the game
 * @property sharedGates if the shared gates mode for 3 players is used
 * @property currentPlayers the players of the game
 * @property playerAtTurn the player who has the turn to play
 * @property currentGems to represent the current gems on the board
 * @property undoStack previous game states
 * @property redoStack next game states
 * @property currentDrawStack represents the draw stack in the game
 */

@Serializable
data class Game(
    var aiMoveMilliseconds: Int,
    val currentBoard: MutableMap<AxialPos, Tile>,
    val sharedGates: Boolean,
    val currentPlayers: List<Player>,
    var playerAtTurn: Player,
    val currentGems: MutableList<Gem>,
    val currentDrawStack : MutableList<Tile>
) {
    val undoStack: MutableList<GameState> = mutableListOf()
    val redoStack: MutableList<GameState> = mutableListOf()
}
