package service

import edu.udo.cs.sopra.ntf.GameInitMessage
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponse

/**
 * Enum to distinguish the different states that occur in networked games, in particular
 * during connection and game setup. Used in [NetworkService].
 */
enum class ConnectionState {
    /**
     * No connection active. Initial state at the start of the program and after
     * an active connection was closed.
     */
    DISCONNECTED,

    /**
     * Connected to server, but no game started or joined yet.
     */
    CONNECTED,

    /**
     * HostGame request sent to server. Waiting for confirmation (i.e. [CreateGameResponse]).
     */
    HOST_WAITING_FOR_CONFIRMATION,

    /**
     * JoinGame request sent to server. Waiting for confirmation (i.e. [JoinGameResponse]).
     */
    GUEST_WAITING_FOR_CONFIRMATION,

    /**
     * Host game started. Waiting for guest players to join.
     */
    WAITING_FOR_GUESTS,

    /**
     * Joined game as a guest and waiting for host to send init message (i.e. [GameInitMessage]).
     */
    WAITING_FOR_INIT,

    /**
     * Game is running. It is my turn.
     */
    PLAYING_MY_TURN,

    /**
     * Game is running. I did my turn. Waiting for opponent to send their turn.
     */
    WAITING_FOR_OPPONENTS_TURN,
}
