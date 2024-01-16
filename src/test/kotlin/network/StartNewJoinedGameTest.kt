package network

import entity.Color
import entity.Player
import kotlin.test.*
import service.*
import kotlin.random.Random

import service.RootService

class StartNewJoinedGameTest {

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

        val game = clientRootService.currentGame
        checkNotNull(game)

        if (game.currentPlayers.first().name == "Player B") {
            assertEquals(clientRootService.networkService.connectionState, ConnectionState.PLAYING_MY_TURN)
        } else {
            assertEquals(clientRootService.networkService.connectionState, ConnectionState.WAITING_FOR_OPPONENTS_TURN)
        }
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
        Thread.sleep(DELAY_IN_MS)

        assertEquals(clientRootService.currentGame, hostRootService.currentGame)
    }
}