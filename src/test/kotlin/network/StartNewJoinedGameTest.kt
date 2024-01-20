package network

import entity.Color
import entity.Player
import kotlin.test.*
import service.*
import kotlin.random.Random

import service.RootService

class StartNewJoinedGameTest {

    private val secret = "game23d"

    private var sessionID = String()

    private var hostRootService = RootService()

    private var clientRootService = RootService()

    private var clientRootService1 = RootService()

    private var clientRootService2 = RootService()

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
     * Tests if the state changes correctly when host is the second player
     */
    @Test
    fun testStateInSecond(){
        val player = mutableListOf<Player>()
        player.add(Player("Player B", Color.BLUE,false,false,null))
        player.add(Player("Player A", Color.PURPLE,false,false,null))

        hostRootService.networkService.startNewHostedGame(player,false,1)
        clientRootService.waitForState(ConnectionState.PLAYING_MY_TURN)

        val game = hostRootService.currentGame
        checkNotNull(game)

        assertEquals(clientRootService.networkService.connectionState, ConnectionState.PLAYING_MY_TURN)
    }

    /**
     * Tests if the state changes correctly with 3 Player mode
     */
    @Test
    fun testState3PLayer(){
        clientRootService1 = RootService()
        clientRootService1.networkService.useAI = false
        clientRootService1.networkService.useSmartAI = false
        clientRootService1.networkService.aiMoveMilliseconds = 1

        clientRootService1.networkService.joinGame(secret,"Player C", sessionID)
        clientRootService1.waitForState(ConnectionState.WAITING_FOR_INIT)

        val player = mutableListOf<Player>()
        player.add(Player("Player A", Color.BLUE,false,false,null))
        player.add(Player("Player B", Color.PURPLE,false,false,null))
        player.add(Player("Player C", Color.RED,false,false,null))

        hostRootService.networkService.startNewHostedGame(player,false,1)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)

        val game = hostRootService.currentGame
        checkNotNull(game)

        assertEquals(clientRootService.networkService.connectionState, ConnectionState.WAITING_FOR_OPPONENTS_TURN)
    }

    /**
     * Tests if the state changes correctly with 3 Player SharedGateways mode
     */
    @Test
    fun testState3PLayerSharedGateways(){
        clientRootService1 = RootService()
        clientRootService1.networkService.useAI = false
        clientRootService1.networkService.useSmartAI = false
        clientRootService1.networkService.aiMoveMilliseconds = 1

        clientRootService1.networkService.joinGame(secret,"Player C", sessionID)
        clientRootService1.waitForState(ConnectionState.WAITING_FOR_INIT)

        val player = mutableListOf<Player>()
        player.add(Player("Player A", Color.BLUE,false,false,null))
        player.add(Player("Player B", Color.PURPLE,false,false,null))
        player.add(Player("Player C", Color.RED,false,false,null))

        hostRootService.networkService.startNewHostedGame(player,true,1)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)

        val game = hostRootService.currentGame
        checkNotNull(game)

        assertEquals(clientRootService.networkService.connectionState, ConnectionState.WAITING_FOR_OPPONENTS_TURN)
    }

    /**
     * Tests if the state changes correctly with 4 Player mode
     */
    @Test
    fun testState4PLayer(){
        clientRootService1 = RootService()
        clientRootService1.networkService.useAI = false
        clientRootService1.networkService.useSmartAI = false
        clientRootService1.networkService.aiMoveMilliseconds = 1

        clientRootService1.networkService.joinGame(secret,"Player C", sessionID)
        clientRootService1.waitForState(ConnectionState.WAITING_FOR_INIT)

        clientRootService2 = RootService()
        clientRootService2.networkService.useAI = false
        clientRootService2.networkService.useSmartAI = false
        clientRootService2.networkService.aiMoveMilliseconds = 1

        clientRootService2.networkService.joinGame(secret,"Player D", sessionID)
        clientRootService2.waitForState(ConnectionState.WAITING_FOR_INIT)


        val player = mutableListOf<Player>()
        player.add(Player("Player A", Color.BLUE,false,false,null))
        player.add(Player("Player B", Color.PURPLE,false,false,null))
        player.add(Player("Player C", Color.RED,false,false,null))
        player.add(Player("Player D", Color.WHITE,false,false,null))

        hostRootService.networkService.startNewHostedGame(player,false,1)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)

        val game = hostRootService.currentGame
        checkNotNull(game)

        assertEquals(clientRootService.networkService.connectionState, ConnectionState.WAITING_FOR_OPPONENTS_TURN)
    }


    /**
     * Tests if the boards match after the game has been initialised
     */
    @Test
    fun testSync(){
        val player = mutableListOf<Player>()
        player.add(Player("Player A", Color.BLUE,false,false,null))
        player.add(Player("Player B", Color.PURPLE,false,false,null))

        hostRootService.networkService.startNewHostedGame(player,false,1)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS_TURN)

        assertEquals(clientRootService.currentGame, hostRootService.currentGame)
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