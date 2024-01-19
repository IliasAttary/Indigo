package network

import entity.Color
import entity.Player
import kotlin.test.*
import service.*
import kotlin.random.Random

class StartNewHostedGameTest {

    private val secret = "game23d"

    private var sessionID = String()

    private var hostRootService = RootService()

    private var clientRootService = RootService()

    /**
     * Connects two clients before every test
     */
    @BeforeTest
    fun setUp() {
        sessionID = "Gruppe_6_" + Random.nextInt(0, 10000).toString()
        hostRootService = RootService()
        clientRootService = RootService()

        hostRootService.networkService.hostGame(secret,"Player A", sessionID)
        hostRootService.waitForState(ConnectionState.WAITING_FOR_GUESTS)

        clientRootService.networkService.joinGame(secret,"Player B", sessionID)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_INIT)

        clientRootService.networkService.useAI = false
        clientRootService.networkService.useSmartAI = false
        clientRootService.networkService.aiMoveMilliseconds = 1
    }

    /**
     * Tests if the state changes correctly
     */
    @Test
    fun testState(){
        val player = mutableListOf<Player>()
        player.add(Player("Player A", Color.BLUE,false,false,null))
        player.add(Player("Player B", Color.PURPLE,false,false,null))

        hostRootService.networkService.startNewHostedGame(player,false,1)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)

        val game = hostRootService.currentGame
        checkNotNull(game)

        if (game.currentPlayers.first().name == "Player A") {
            assertEquals(hostRootService.networkService.connectionState, ConnectionState.PLAYING_MY_TURN)
        } else {
            assertEquals(hostRootService.networkService.connectionState, ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }
    }

    /**
     * Tests if the boards match the game has been initialised
     */
    @Test
    fun testSync(){
        val player = mutableListOf<Player>()
        player.add(Player("Player A", Color.BLUE,false,false,null))
        player.add(Player("Player B", Color.PURPLE,false,false,null))

        hostRootService.networkService.startNewHostedGame(player,false,1)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)

        assertEquals(hostRootService.currentGame, clientRootService.currentGame)
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