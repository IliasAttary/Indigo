package view

import entity.AxialPos
import entity.Player
import service.AbstractRefreshingService

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the view classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * UI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 *
 */
interface Refreshable {

    /**
     * Called when a new game is started.
     * This method should be used to reset or initialize the UI elements relevant to a new game state.
     */
    fun refreshAfterNewGame() {}

    /**
     * Called at the end of a game.
     * This method can be used to update the UI with final game results or player scores.
     *
     * @param players The list of players involved in the game to display end-game statistics.
     */
    fun refreshAfterEndGame(players: List<Player>) {}

    /**
     * Called after a tile is placed in the game.
     * Use this method to update the UI to reflect the new position of a tile.
     *
     * @param position The axial position where the tile has been placed.
     */
    fun refreshAfterPlaceTile(position: AxialPos) {}

    /**
     * Called when there is a change in the active player.
     * This method should be used to update any UI elements that reflect the current player's turn.
     */
    fun refreshAfterChangePlayer() {}

    /**
     * Called after an undo action is performed.
     * Use this method to revert the UI to a previous state that matches the game state before the latest action.
     */
    fun refreshAfterUndo() {}

    /**
     * Called after a redo action is performed.
     * Use this to update the UI to reflect the reinstated state of the game following an undo.
     */
    fun refreshAfterRedo() {}

    /**
     * Called when a game is loaded.
     * This method should update the UI to reflect the loaded game state, including any necessary UI elements.
     */
    fun refreshAfterLoadGame() {}

    /**
     * Called when a new player joins the game.
     * This method should update the UI to include the new player, such as updating player lists or indicators.
     *
     * @param playerName The name of the player who has joined the game.
     */
    fun refreshAfterJoinPlayer(playerName: String) {}

    /**
     *  Called when a new player joins the game.
     *  Updates the UI for the guest player that joins the game.
     *
     *  @param playerNames The names of the players that get displayed in the name fields.
     */
    fun refreshAfterJoiningGame(playerNames: List<String>) {}

    /**
     * Updates the UI whenever a Gem is moved
     */
    fun refreshAfterMoveGems(){}

    /**
     * Called when a player rotates his held tile.
     * This method should update the UI with the new rotation of the tile.
     */
    fun refreshAfterRotateTile() {}
}