package network

import entity.*
import kotlin.test.*
import service.*
import kotlin.random.Random

/**
 * This class contains test cases for the [NetworkService.receivePlacedTileFromOpponent] function.
 */
class receivedPlacedTileFromOpponentTest {

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
        player.add(Player("Player A", Color.BLUE,false,false, null))
        player.add(Player("Player B", Color.PURPLE,false,false, null))

        hostRootService.networkService.hostGame(secret,"Player A", sessionID)
        hostRootService.waitForState(ConnectionState.WAITING_FOR_GUESTS)

        clientRootService.networkService.joinGame(secret,"Player B", sessionID)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_INIT)

        clientRootService.networkService.useAI = false
        clientRootService.networkService.useSmartAI = false
        clientRootService.networkService.aiMoveMilliseconds = 1

        hostRootService.networkService.startNewHostedGame(player,false,1)
        hostRootService.waitForState(ConnectionState.PLAYING_MY_TURN)
    }

    /**
     * Tests if the state changes correctly for the waiting player
     */
    @Test
    fun testState(){
        if (hostRootService.networkService.connectionState == ConnectionState.PLAYING_MY_TURN){
            hostRootService.playerService.placeTile(AxialPos(-1,0))
            clientRootService.waitForState(ConnectionState.PLAYING_MY_TURN)

            assertEquals(ConnectionState.PLAYING_MY_TURN, clientRootService.networkService.connectionState)
        } else {
            clientRootService.playerService.placeTile(AxialPos(-1,0))
            hostRootService.waitForState(ConnectionState.PLAYING_MY_TURN)

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
            clientRootService.waitForState(ConnectionState.PLAYING_MY_TURN)

            val hostGame = hostRootService.currentGame
            val clientGame = clientRootService.currentGame
            checkNotNull(hostGame)
            checkNotNull(clientGame)

            assertEquals(hostGame.currentBoard, clientGame.currentBoard)
        } else {
            clientRootService.playerService.placeTile(AxialPos(-1, 0))
            hostRootService.waitForState(ConnectionState.PLAYING_MY_TURN)

            val hostGame = hostRootService.currentGame
            val clientGame = clientRootService.currentGame
            checkNotNull(hostGame)
            checkNotNull(clientGame)

            assertEquals(hostGame.currentBoard, clientGame.currentBoard)
        }
    }

    /**
     * Waits the appropriate time for the response if the server
     *
     * @param state The desired State of the client after a response from the server
     * @param timeout The time before a timeout
     *
     * @throws error If timed out
     */
    private fun RootService.waitForState(state: ConnectionState, timeout: Int = 5000) {
        var timePassed = 0
        while (timePassed < timeout) {
            if (networkService.connectionState == state)
                return
            else {
                Thread.sleep(100)
                timePassed += 100
            }
        }
        error("Did not arrive at state $state after waiting $timeout ms")
    }

}