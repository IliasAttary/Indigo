package service.ai

import entity.*
import service.*
import kotlin.test.*

class AIServiceTest {

    private lateinit var gameService: GameService
    private lateinit var playerService: PlayerService
    private lateinit var rootService: RootService
    private lateinit var helpFunctions: HelpFunctions

    /**
     * Sets up the necessary services and initializes them before each test case.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        playerService = PlayerService(rootService)
        gameService = GameService(rootService)
        helpFunctions = HelpFunctions(rootService)
    }

    /**
     * Tests the behavior of the random AI and the chosen move.
     * Expects an IllegalStateException to be thrown.
     */
    @Test
    fun testRandomAIMoves() {
        assertFails { playerService.placeTile(AxialPos(1, -3)) }

        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = true, smartAI = false),
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)
    }

    /**
     * Tests the behavior of the smart AI and the chosen move.
     * Expects an IllegalStateException to be thrown.
     */
    @Test
    fun testSmartAIMoves() {
        assertFails { playerService.placeTile(AxialPos(1, -3)) }

        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P3", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = true, smartAI = true)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)
    }


    @Test
    fun testCurrentValidCoordinates() {
        val validCoordinates = mutableListOf(AxialPos(3,4))
        val currentValidCoordinates = helpFunctions.getCurrentValidCoordinates()
        assertSame(currentValidCoordinates, validCoordinates)
    }
}