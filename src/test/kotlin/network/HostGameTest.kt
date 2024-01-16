package network

import kotlin.test.*
import service.*
import kotlin.random.Random

class HostGameTest {
    private val DELAY_IN_MS = 1000.toLong()

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
        Thread.sleep(DELAY_IN_MS)

        assertEquals(hostRootService.networkService.connectionState, ConnectionState.WAITING_FOR_GUESTS)
    }

    /**
     * Tests if the state changes correctly with a SessionID
     */
    @Test
    fun testWithSessionID() {
        sessionID = "Gruppe_6_" + Random.nextInt(0, 10000).toString()
        hostRootService.networkService.hostGame(secret,"Player A", sessionID)
        Thread.sleep(DELAY_IN_MS)

        assertEquals(hostRootService.networkService.connectionState, ConnectionState.WAITING_FOR_GUESTS)
    }
}