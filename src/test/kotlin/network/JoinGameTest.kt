package network

import kotlin.test.*
import service.*
import kotlin.random.Random

class JoinGameTest {
    private val DELAY_IN_MS = 1000.toLong()

    private val secret = "game23d"

    private var sessionID = String()

    private var hostRootService = RootService()

    private var clientRootService = RootService()

    /**
     * Hosts a game before every test
     */
    @BeforeTest
    fun setUp() {
        sessionID = "Gruppe_6_" + Random.nextInt(0, 10000).toString()
        hostRootService = RootService()
        clientRootService = RootService()

        hostRootService.networkService.hostGame(secret,"Player A", sessionID)
        Thread.sleep(DELAY_IN_MS)
    }

    /**
     * Tests if the state changes correctly
     */
    @Test
    fun testJoin() {
        assertEquals(clientRootService.networkService.connectionState, ConnectionState.DISCONNECTED)
        clientRootService.networkService.joinGame(secret,"Player B", sessionID)
        Thread.sleep(DELAY_IN_MS)

        assertEquals(clientRootService.networkService.connectionState, ConnectionState.WAITING_FOR_INIT)
    }
}