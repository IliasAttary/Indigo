package network

import entity.Color
import entity.Player
import kotlin.test.*
import service.*
import kotlin.random.Random

class StartNewHostedGameTest {

    private val DELAY_IN_MS = 1000.toLong()

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
        Thread.sleep(DELAY_IN_MS)

        clientRootService.networkService.joinGame(secret,"Player B", sessionID)
        Thread.sleep(DELAY_IN_MS)
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
        Thread.sleep(DELAY_IN_MS)

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
        Thread.sleep(DELAY_IN_MS)

        assertEquals(hostRootService.currentGame, clientRootService.currentGame)
    }
}