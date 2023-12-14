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

    fun refreshAfterNewGame() {}

    fun refreshAfterEndGame(players: List<Player>) {}

    fun refreshAfterPlaceTile(position: AxialPos) {}

    fun refreshAfterChangePlayer() {}

    fun refreshAfterUndo() {}

    fun refreshAfterRedo() {}

    fun refreshAfterSaveGame() {}

    fun refreshAfterLoadGame() {}

    fun refreshAfterJoinPlayer(playerName: String) {}
}