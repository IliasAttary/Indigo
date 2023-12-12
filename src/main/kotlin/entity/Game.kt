package entity

/**
 *Entity class that represents a game state of "Indigo"
 * @property aiMoveMilliSeconds the time that an AI player needs to finish a step
 * @property currentBoard to represent the board of the game
 * @property sharedGates tells if the gate is shared or not
 * @property currentPlayers the players of the game
 * @property playerAtTurn the player who has the turn to play
 * @property currentGems to represent the current gems on thr board
 * @property undoStack if the player wants to go to the next step
 * @property redoStack if the player wants to go to the last step
 */
data class Game(val aiMoveMilliSeconds:Int,
                val currentBoard:Map<AxialPos,Tile>,
                var sharedGates:Boolean,
                val currentPlayers:MutableList<Player>,
                var playerAtTurn:Player,
                val currentGems:MutableList<Gem>,
                val undoStack:MutableList<GameState>,
                val redoStack:MutableList<GameState>){

}
