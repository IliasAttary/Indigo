package network

import kotlin.test.*
import service.*
import kotlin.random.Random

class DisconnectTest {
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
     * Tests if the state changed correctly from connected to disconnected
     */
    @Test
    fun testConnectedToDisconnected() {
        assertEquals(clientRootService.networkService.connectionState, ConnectionState.WAITING_FOR_INIT)
        clientRootService.networkService.disconnect()
        assertEquals(clientRootService.networkService.connectionState, ConnectionState.DISCONNECTED)
    }

    /**
     * Tests if the state stays in Disconnected
     */
    @Test
    fun testDisconnectedToConnected() {
        clientRootService.networkService.disconnect()
        clientRootService.networkService.disconnect()
        assertEquals(clientRootService.networkService.connectionState, ConnectionState.DISCONNECTED)
    }
}