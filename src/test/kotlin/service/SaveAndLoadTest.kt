package service

import entity.*
import java.io.File
import kotlin.test.*

/**
 * This class contains test cases for the [GameService.save] and [GameService.load] functions.
 */
class SaveAndLoadTest {

    private lateinit var gameService: GameService
    private lateinit var playerService: PlayerService
    private lateinit var rootService: RootService

    /**
     * Sets up the necessary services and initializes them before each test case.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        playerService = PlayerService(rootService)
        gameService = GameService(rootService)
    }

    /**
     * Cleans up by deleting the save file after each test case.
     */
    @AfterTest
    fun tearDown() {
        File("saveGame.ser").delete()
    }

    /**
     * Tests the save function by starting a game, saving it,
     * and then verifying the existence and content of the save file.
     */
    @Test
    fun saveTest() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = null, isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = null, isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        gameService.save()
        val file = File("saveGame.ser")

        assert(file.exists())
        assert(file.readText().isNotEmpty())
    }

    /**
     * Tests the load function by starting a game, saving it,
     * then loading the game and comparing the loaded state to the original state.
     */
    @Test
    fun loadTest() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = null, isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = null, isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val sampleState = rootService.currentGame

        gameService.save()
        gameService.load()

        val loadedGame = rootService.currentGame!!

        assertEquals(sampleState, loadedGame)
        assertSame(loadedGame.playerAtTurn, loadedGame.currentPlayers[0])
        assertSame(
            (loadedGame.currentBoard[AxialPos(1, -5)] as GatewayTile).ownedBy.first(),
            loadedGame.currentPlayers[0]
        )
    }
}