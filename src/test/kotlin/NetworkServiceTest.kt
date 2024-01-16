import entity.AxialPos
import kotlin.test.*
import service.*
import kotlin.random.Random

class NetworkServiceTest {
    private val DELAY_IN_MS = 1.toLong()

    private var sessionID = String()

    private var hostRootService = RootService()

    private var clientRootService = RootService()
    /**
     * Setups a new instance of [sessionID], [hostRootService] and [clientRootService]
     */
    @BeforeTest
    fun setUp() {
        sessionID = "Gruppe_6_" + Random.nextInt(0, 10000).toString()
        hostRootService = RootService()
        clientRootService = RootService()
        hostRootService.networkService.hostGame("Apple","Player A", sessionID)
        Thread.sleep(DELAY_IN_MS)
        clientRootService.networkService.joinGame("Apple","Player B", sessionID)
        Thread.sleep(DELAY_IN_MS)
    }

    @Test
    fun testDisconnect() {
        assertEquals(clientRootService.networkService.connectionState, ConnectionState.CONNECTED)
        clientRootService.networkService.disconnect()
        assertEquals(clientRootService.networkService.connectionState, ConnectionState.DISCONNECTED)
    }
}