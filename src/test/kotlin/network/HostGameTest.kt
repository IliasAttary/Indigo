package network

import kotlin.test.*
import service.*
import kotlin.random.Random

/**
 * This class contains test cases for the [NetworkService.hostGame] function.
 */
class HostGameTest {

    private val secret = "game23d"

    private var sessionID = String()

    private var hostRootService = RootService()

    /**
     * Creates a new client before every test
     */
    @BeforeTest
    fun setUp() {
        hostRootService = RootService()
    }

    /**
     * Tests if the state changes correctly with a blank SessionID
     */
    @Test
    fun testBlankSessionID() {
        hostRootService.networkService.hostGame(secret,"Player A","")
        hostRootService.waitForState(ConnectionState.WAITING_FOR_GUESTS)

        assertEquals(hostRootService.networkService.connectionState, ConnectionState.WAITING_FOR_GUESTS)
    }

    /**
     * Tests if the state changes correctly with a SessionID
     */
    @Test
    fun testWithSessionID() {
        sessionID = "Gruppe_6_" + Random.nextInt(0, 10000).toString()
        hostRootService.networkService.hostGame(secret,"Player A", sessionID)
        hostRootService.waitForState(ConnectionState.WAITING_FOR_GUESTS)

        assertEquals(hostRootService.networkService.connectionState, ConnectionState.WAITING_FOR_GUESTS)
    }

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