package network

import entity.*
import kotlin.test.*
import service.*
import kotlin.random.Random

class receivedPlacedTileFromOpponentTest {

    private val DELAY_IN_MS = 1000.toLong()

    private val secret = "game23d"

    private var sessionID = String()

    private var hostRootService = RootService()

    private var clientRootService = RootService()

    /**
     * Connects two clients and initialises a Game before every test
     */
    @BeforeTest
    fun setUp() {
        sessionID = "Gruppe_6_" + Random.nextInt(0, 10000).toString()
        hostRootService = RootService()
        clientRootService = RootService()

        val player = mutableListOf<Player>()
        player.add(Player("Player A", Color.BLUE,false,false, RouteTile(TileType.TILE0)))
        player.add(Player("Player B", Color.PURPLE,false,false, RouteTile(TileType.TILE0)))

        hostRootService.networkService.hostGame(secret,"Player A", sessionID)
        Thread.sleep(DELAY_IN_MS)
        clientRootService.networkService.joinGame(secret,"Player B", sessionID)
        Thread.sleep(DELAY_IN_MS)
        hostRootService.networkService.startNewHostedGame(player,false,1)
        Thread.sleep(DELAY_IN_MS)
    }

    /**
     * Tests if the state changes correctly for the waiting player
     */
    @Test
    fun testState(){
        if (hostRootService.networkService.connectionState == ConnectionState.PLAYING_MY_TURN){
            hostRootService.playerService.placeTile(AxialPos(-1,0))
            Thread.sleep(DELAY_IN_MS)

            assertEquals(ConnectionState.PLAYING_MY_TURN, clientRootService.networkService.connectionState)
        } else {
            clientRootService.playerService.placeTile(AxialPos(-1,0))
            Thread.sleep(DELAY_IN_MS)

            assertEquals(ConnectionState.PLAYING_MY_TURN, hostRootService.networkService.connectionState)
        }
    }

    /**
     * Tests if the boards match after a tile has been placed (same as in SendPlacedTileTest for redundancy)
     */
    @Test
    fun testSync() {


        if (hostRootService.networkService.connectionState == ConnectionState.PLAYING_MY_TURN) {
            hostRootService.playerService.placeTile(AxialPos(-1, 0))
            Thread.sleep(DELAY_IN_MS)

            val hostGame = hostRootService.currentGame
            val clientGame = clientRootService.currentGame
            checkNotNull(hostGame)
            checkNotNull(clientGame)

            assertEquals(hostGame.currentBoard, clientGame.currentBoard)
        } else {
            clientRootService.playerService.placeTile(AxialPos(-1, 0))
            Thread.sleep(DELAY_IN_MS)

            val hostGame = hostRootService.currentGame
            val clientGame = clientRootService.currentGame
            checkNotNull(hostGame)
            checkNotNull(clientGame)

            assertEquals(hostGame.currentBoard, clientGame.currentBoard)
        }
    }

}