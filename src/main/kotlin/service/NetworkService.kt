package service

import edu.udo.cs.sopra.ntf.*
import entity.*
import entity.Player
import entity.TileType

/**
 * Service layer class that realizes the necessary logic for sending and receiving messages
 * in multiplayer network games. Bridges between the [NetworkClient] and the other services.
 *
 * @property rootService the root service
 */
class NetworkService(private val rootService: RootService) : AbstractRefreshingService() {

    private companion object {
        /** URL of the BGW net server hosted for SoPra participants */
        private const val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"

        /** Name of the game as registered with the server */
        private const val GAME_ID = "Indigo"
    }

    /** Network client. Nullable for offline games. */
    private var client: NetworkClient? = null

    /**
     * Current state of the connection in a network game
     */
    var connectionState: ConnectionState = ConnectionState.DISCONNECTED

    /**
     * Whether to use an AI.
     * This variable is used to initialize *our* player when we are a guest player in a network game.
     *
     * @see [NetworkService.startNewJoinedGame]
     */
    var useAI: Boolean? = null

    /**
     * Whether the AI type is smart.
     * This variable is used to initialize *our* player when we are a guest player in a network game.
     *
     * @see [NetworkService.startNewJoinedGame]
     */
    var useSmartAI: Boolean? = null

    /**
     * Simulation speed of the artificial intelligence (AI) in milliseconds.
     * This variable is used to initialize the game when we are a guest player in a network game.
     *
     * @see [NetworkService.startNewJoinedGame]
     */
    var aiMoveMilliseconds: Int? = null

    /**
     * Connects to server, sets the [NetworkService.client] if successful and returns `true` on success.
     *
     * @param secret server secret; must not be blank (i.e. empty or only whitespaces)
     * @param playerName our player name; must not be blank
     *
     * @return whether it connected successfully
     *
     * @throws IllegalArgumentException if secret or player name is blank
     * @throws IllegalStateException if already connected to another game
     */
    private fun connect(secret: String, playerName: String): Boolean {
        require(secret.isNotBlank()) {
            "Server secret must not be blank"
        }

        require(playerName.isNotBlank()) {
            "Player name must not be blank"
        }

        check(connectionState == ConnectionState.DISCONNECTED && client == null) {
            "Already connected to another game"
        }

        val newClient = NetworkClient(
            playerName = playerName,
            host = SERVER_ADDRESS,
            secret = secret,
            networkService = this,
        )

        return if (newClient.connect()) {
            client = newClient
            connectionState = ConnectionState.CONNECTED
            true
        } else {
            false
        }
    }

    /**
     * Disconnects the [client] from the server, nulls it and updates the
     * [connectionState] to [ConnectionState.DISCONNECTED]. Can safely be called
     * even if no connection is currently active.
     */
    fun disconnect() {
        client?.apply {
            if (sessionID != null) {
                leaveGame("Goodbye!")
            }

            if (isOpen) {
                disconnect()
            }
        }

        client = null
        connectionState = ConnectionState.DISCONNECTED
    }

    /**
     * Connects to the server and creates a new game session.
     *
     * @param secret server secret; must not be blank (i.e. empty or only whitespaces)
     * @param playerName our player name; must not be blank
     * @param sessionID identifier of the hosted session (to be used by guest on join)
     *
     * @throws IllegalArgumentException if secret or player name is blank
     * @throws IllegalStateException if already connected to another game or connection attempt fails
     */
    fun hostGame(secret: String, playerName: String, sessionID: String?) {
        if (!connect(secret, playerName)) {
            error("Connection failed")
        }

        val client = client

        checkNotNull(client) {
            "Network client should not be null"
        }

        if (sessionID.isNullOrBlank()) {
            client.createGame(GAME_ID, "Welcome!")
        } else {
            client.createGame(GAME_ID, sessionID, "Welcome!")
        }

        connectionState = ConnectionState.HOST_WAITING_FOR_CONFIRMATION
    }

    /**
     * Connects to the server and joins a game session as a guest player.
     *
     * @param secret server secret; must not be blank (i.e. empty or only whitespaces)
     * @param playerName our player name; must not be blank
     * @param sessionID identifier of the joined session (as defined by host on create)
     *
     * @throws IllegalArgumentException if secret or player name is blank
     * @throws IllegalStateException if already connected to another game or connection attempt fails
     */
    fun joinGame(secret: String, playerName: String, sessionID: String) {
        if (!connect(secret, playerName)) {
            error("Connection failed")
        }

        val client = client

        checkNotNull(client) {
            "Network client should not be null"
        }

        client.joinGame(sessionID, "Hello!")
        connectionState = ConnectionState.GUEST_WAITING_FOR_CONFIRMATION
    }

    /**
     * Set up the game using [GameService.startGame] and send the game init message
     * to the guest players. The [connectionState] needs to be [ConnectionState.WAITING_FOR_GUESTS].
     * This method should be called from the GUI.
     *
     * @param players list of players in the game
     * @param sharedGates if the shared gates mode for 3 players is used
     * @param aiMoveMilliseconds simulation speed of the artificial intelligence (AI) in milliseconds
     *
     * @throws IllegalStateException if [connectionState] != [ConnectionState.WAITING_FOR_GUESTS],
     *   the network client is null, or the number of players is not 2, 3, or 4.
     */
    fun startNewHostedGame(players: List<Player>, sharedGates: Boolean, aiMoveMilliseconds: Int) {
        check(connectionState == ConnectionState.WAITING_FOR_GUESTS) {
            "Currently not prepared to start a new hosted game"
        }

        val client = client

        checkNotNull(client) {
            "Network client should not be null"
        }

        rootService.gameService.startGame(
            players = players,
            sharedGates = sharedGates,
            aiSpeed = aiMoveMilliseconds,
        )
        val game = rootService.currentGame

        checkNotNull(game) {
            "Game should not be null right after starting it"
        }

        val networkPlayers = game.currentPlayers.map {
            edu.udo.cs.sopra.ntf.Player(
                it.name, when (it.color) {
                    Color.WHITE -> PlayerColor.WHITE
                    Color.RED -> PlayerColor.RED
                    Color.PURPLE -> PlayerColor.PURPLE
                    Color.BLUE -> PlayerColor.BLUE
                }
            )
        }
        val gameMode = when (game.currentPlayers.size) {
            2 -> GameMode.TWO_NOT_SHARED_GATEWAYS
            3 -> {
                if (game.sharedGates) {
                    GameMode.THREE_SHARED_GATEWAYS
                } else {
                    GameMode.THREE_NOT_SHARED_GATEWAYS
                }
            }
            4 -> GameMode.FOUR_SHARED_GATEWAYS
            else -> error("Invalid number of players")
        }
        val tileList = convertDrawStackToNetwork(game.currentDrawStack)

        val message = GameInitMessage(
            players = networkPlayers,
            gameMode = gameMode,
            tileList = tileList,
        )

        connectionState = if (networkPlayers.first().name == client.playerName) {
            ConnectionState.PLAYING_MY_TURN
        } else {
            ConnectionState.WAITING_FOR_OPPONENTS_TURN
        }

        client.sendGameActionMessage(message)
    }

    /**
     * Starts a new game with the data given by the [GameInitMessage] sent by the host.
     * The [connectionState] needs to be [ConnectionState.WAITING_FOR_INIT].
     * This method should be called from the [NetworkClient] when the host sends the init message.
     * See [NetworkClient.onInitReceived].
     *
     * @throws IllegalStateException if not currently waiting for an init message,
     *   the network client is null, or the AI speed has not been set
     *
     * @see [NetworkService.useAI]
     * @see [NetworkService.useSmartAI]
     * @see [NetworkService.aiMoveMilliseconds]
     */
    fun startNewJoinedGame(message: GameInitMessage) {
        check(connectionState == ConnectionState.WAITING_FOR_INIT) {
            "Not waiting for game init message"
        }

        val client = client

        checkNotNull(client) {
            "Network client should not be null"
        }

        val useAI = useAI
        val useSmartAI = useSmartAI
        val aiMoveMilliseconds = aiMoveMilliseconds

        checkNotNull(useAI) {
            "Whether to use an AI has not been set"
        }

        checkNotNull(useSmartAI) {
            "Whether the AI type is smart has not been set"
        }

        checkNotNull(aiMoveMilliseconds) {
            "The AI speed has not been set"
        }

        val players = message.players.map {
            val color = when (it.color) {
                PlayerColor.WHITE -> Color.WHITE
                PlayerColor.RED -> Color.RED
                PlayerColor.PURPLE -> Color.PURPLE
                PlayerColor.BLUE -> Color.BLUE
            }

            val isAI = if (it.name == client.playerName) {
                useAI
            } else {
                false
            }

            val isSmartAI = if (it.name == client.playerName) {
                useSmartAI
            } else {
                false
            }

            Player(
                name = it.name,
                color = color,
                isAI = isAI,
                smartAI = isSmartAI,
                heldTile = null,
            )
        }
        val sharedGates = message.gameMode == GameMode.THREE_SHARED_GATEWAYS
        val drawStack = convertDrawStackFromNetwork(message.tileList)

        rootService.gameService.startGame(
            players = players,
            drawStack = drawStack.toMutableList(),
            sharedGates = sharedGates,
            aiSpeed = aiMoveMilliseconds,
        )

        connectionState = if (message.players.first().name == client.playerName) {
            ConnectionState.PLAYING_MY_TURN
        } else {
            ConnectionState.WAITING_FOR_OPPONENTS_TURN
        }
    }

    /**
     * Converts our internal draw stack to the network representation.
     *
     * @param drawStack internal draw stack
     *
     * @return network draw stack
     */
    private fun convertDrawStackToNetwork(drawStack: List<RouteTile>) = drawStack.map {
        when (it.tileType) {
            TileType.TILE0 -> edu.udo.cs.sopra.ntf.TileType.TYPE_0
            TileType.TILE1 -> edu.udo.cs.sopra.ntf.TileType.TYPE_1
            TileType.TILE2 -> edu.udo.cs.sopra.ntf.TileType.TYPE_2
            TileType.TILE3 -> edu.udo.cs.sopra.ntf.TileType.TYPE_3
            TileType.TILE4 -> edu.udo.cs.sopra.ntf.TileType.TYPE_4
        }
    }

    /**
     * Converts a draw stack in network representation to an internal draw stack.
     *
     * @param drawStack network draw stack
     *
     * @return internal draw stack
     */
    private fun convertDrawStackFromNetwork(drawStack: List<edu.udo.cs.sopra.ntf.TileType>) = drawStack.map {
        val tileType = when (it) {
            edu.udo.cs.sopra.ntf.TileType.TYPE_0 -> TileType.TILE0
            edu.udo.cs.sopra.ntf.TileType.TYPE_1 -> TileType.TILE1
            edu.udo.cs.sopra.ntf.TileType.TYPE_2 -> TileType.TILE2
            edu.udo.cs.sopra.ntf.TileType.TYPE_3 -> TileType.TILE3
            edu.udo.cs.sopra.ntf.TileType.TYPE_4 -> TileType.TILE4
        }

        RouteTile(tileType)
    }

    /**
     * Send a [TilePlacedMessage] to the opponents.
     *
     * @param position the position of the placed tile
     *
     * @throws IllegalStateException if one of these is true:
     *   * it is not my turn
     *   * network client is null
     *   * game is null
     *   * placed tile is null
     *   * placed tile is not a route tile
     */
    fun sendPlacedTile(position: AxialPos) {
        check(connectionState == ConnectionState.PLAYING_MY_TURN) {
            "It is not your turn"
        }

        val client = client

        checkNotNull(client) {
            "The network client should not be null"
        }

        val game = rootService.currentGame

        checkNotNull(game) {
            "Game should not be null"
        }

        val tile = game.currentBoard[position]

        checkNotNull(tile) {
            "Placed tile should not be null"
        }

        check(tile is RouteTile) {
            "The placed tile must be a route tile"
        }

        val message = TilePlacedMessage(
            rCoordinate = position.r,
            qCoordinate = position.q,
            rotation = tile.rotation,
        )

        connectionState = ConnectionState.WAITING_FOR_OPPONENTS_TURN
        client.sendGameActionMessage(message)
    }

    /**
     * Play the opponent's turn by handling the [TilePlacedMessage] sent through the server.
     *
     * @param message the message to handle
     * @param sender the sender's player name
     *
     * @throws IllegalStateException if one of these is true:
     *   * it is not the opponent's turn
     *   * network client is null
     *   * game is null
     *   * the tile was placed by someone, even though it is somebody else's turn
     *   * held tile of current player is null
     */
    fun receivePlacedTileFromOpponent(message: TilePlacedMessage, sender: String) {
        check(connectionState == ConnectionState.WAITING_FOR_OPPONENTS_TURN) {
            "Currently not expecting an opponent's turn"
        }

        val client = client

        checkNotNull(client) {
            "The network client should not be null"
        }

        val game = rootService.currentGame

        checkNotNull(game) {
            "Game should not be null"
        }

        check(game.playerAtTurn.name == sender) {
            "Received TilePlacedMessage from $sender even though it is ${game.playerAtTurn.name}'s turn"
        }

        val position = AxialPos(
            r = message.rCoordinate,
            q = message.qCoordinate,
        )
        val tile = game.playerAtTurn.heldTile

        checkNotNull(tile) {
            "The tile that should be placed cannot be null"
        }

        tile.rotation = message.rotation
        rootService.playerService.placeTile(position)

        if (game.playerAtTurn.name == client.playerName) {
            connectionState = ConnectionState.PLAYING_MY_TURN
        }
    }

}
