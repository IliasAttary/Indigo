package network

import kotlin.test.*
import service.*
import kotlin.random.Random

class JoinGameTest {

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
        hostRootService.waitForState(ConnectionState.WAITING_FOR_GUESTS)
    }

    /**
     * Tests if the state changes correctly
     */
    @Test
    fun testJoin() {
        assertEquals(clientRootService.networkService.connectionState, ConnectionState.DISCONNECTED)
        clientRootService.networkService.joinGame(secret,"Player B", sessionID)
        clientRootService.waitForState(ConnectionState.WAITING_FOR_INIT)

        assertEquals(clientRootService.networkService.connectionState, ConnectionState.WAITING_FOR_INIT)
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