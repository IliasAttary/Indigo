package network

import kotlin.test.*
import service.*
import kotlin.random.Random

class DisconnectTest {

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