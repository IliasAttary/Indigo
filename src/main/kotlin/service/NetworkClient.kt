package service

import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.TilePlacedMessage
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.client.NetworkLogging
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.*

/**
 * [BoardGameClient] implementation for network communication.
 *
 * @param playerName our player name
 * @param host the server ip or hostname
 * @param secret the server secret
 * @property networkService the [NetworkService] to potentially forward received messages to
 */
class NetworkClient(
    playerName: String,
    host: String,
    secret: String,
    private var networkService: NetworkService,
) : BoardGameClient(playerName, host, secret, NetworkLogging.ERRORS) {

    private companion object {
        /**
         * The maximum amount of players in a game
         */
        private const val PLAYER_LIMIT = 4
    }

    /** The identifier of this game session; can be null if no session started yet */
    var sessionID: String? = null

    /** The name of the opponent players; can be null if no message from the opponent has been received yet */
    private var otherPlayerNames: MutableList<String>? = null

    /**
     * Handle a [CreateGameResponse] sent by the server. Will await the guest player when its
     * status is [CreateGameResponseStatus.SUCCESS]. As recovery from network problems is not
     * implemented, the method disconnects from the server and throws an [IllegalStateException] otherwise.
     *
     * @throws IllegalStateException if status != success or currently not waiting for a game creation response.
     */
    override fun onCreateGameResponse(response: CreateGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.HOST_WAITING_FOR_CONFIRMATION) {
                "Unexpected CreateGameResponse"
            }

            when (response.status) {
                CreateGameResponseStatus.SUCCESS -> {
                    sessionID = response.sessionID
                    otherPlayerNames = mutableListOf()
                    networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
                }
                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handle a [JoinGameResponse] sent by the server. Will await the init message when its
     * status is [JoinGameResponseStatus.SUCCESS]. As recovery from network problems is not
     * implemented, the method disconnects from the server and throws an [IllegalStateException] otherwise.
     *
     * @throws IllegalStateException if status != success or currently not waiting for a join game response.
     */
    override fun onJoinGameResponse(response: JoinGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.GUEST_WAITING_FOR_CONFIRMATION) {
                "Unexpected JoinGameResponse"
            }

            when (response.status) {
                JoinGameResponseStatus.SUCCESS -> {
                    val opponents = response.opponents.take(PLAYER_LIMIT - 1).toMutableList()

                    sessionID = response.sessionID
                    otherPlayerNames = opponents
                    networkService.connectionState = ConnectionState.WAITING_FOR_INIT

                    networkService.onAllRefreshables {
                        refreshAfterJoiningGame(opponents)
                    }
                }
                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handle a [PlayerJoinedNotification] sent by the server.
     *
     * @param notification the player joined notification
     *
     * @throws IllegalStateException if the player name list is null
     */
    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        BoardGameApplication.runOnGUIThread {
            if (networkService.connectionState != ConnectionState.WAITING_FOR_GUESTS
                && networkService.connectionState != ConnectionState.WAITING_FOR_INIT) {
                // Not awaiting any guests
                return@runOnGUIThread
            }

            val otherPlayerNames = otherPlayerNames

            checkNotNull(otherPlayerNames) {
                "The list for other player names is null"
            }

            if (otherPlayerNames.size >= PLAYER_LIMIT - 1) {
                // Ignore joined player as the room is already full
                return@runOnGUIThread
            }

            otherPlayerNames.add(notification.sender)

            networkService.onAllRefreshables {
                refreshAfterJoinPlayer(notification.sender)
            }
        }
    }

    /**
     * Handle a [GameActionResponse] sent by the server. Does nothing when its
     * status is [GameActionResponseStatus.SUCCESS]. As recovery from network problems is not
     * implemented, the method disconnects from the server and throws an [IllegalStateException] otherwise.
     *
     * @param response the game action response
     *
     * @throws IllegalArgumentException if it is neither my turn nor my opponent's turn
     */
    override fun onGameActionResponse(response: GameActionResponse) {
        BoardGameApplication.runOnGUIThread {
            check(
                networkService.connectionState == ConnectionState.PLAYING_MY_TURN
                        || networkService.connectionState == ConnectionState.WAITING_FOR_OPPONENTS_TURN
            ) {
                "Not currently playing in a network game"
            }

            when (response.status) {
                GameActionResponseStatus.SUCCESS -> {} // do nothing in this case
                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handle a [GameInitMessage] sent by the server.
     *
     * @param message the game init message
     * @param sender the sender's player name
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onInitReceived(message: GameInitMessage, sender: String) {
        BoardGameApplication.runOnGUIThread {
            networkService.startNewJoinedGame(message)
        }
    }

    /**
     * Handle a [TilePlacedMessage] sent by the server.
     *
     * @param message the tile placed message
     * @param sender the sender's player name
     */
    @Suppress("unused")
    @GameActionReceiver
    fun onTilePlacedReceived(message: TilePlacedMessage, sender: String) {
        BoardGameApplication.runOnGUIThread {
            networkService.receivePlacedTileFromOpponent(message, sender)
        }
    }

    /**
     * Disconnects from the server and throws an [IllegalArgumentException] with the given message.
     *
     * @param message the message for the [IllegalArgumentException]
     *
     * @throws IllegalArgumentException always thrown with the given message
     */
    private fun disconnectAndError(message: Any) {
        networkService.disconnect()
        error(message)
    }

}
