package service.ai

import entity.*
import service.*
import kotlin.test.*

class AIServiceTest {

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
    fun testValidPositions() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)

        val aiServices = AIServices(rootService)

        //the valid coordinates are all the coordinates of the board.

        val coordinates1 = aiServices.findAllValidPositions()

        assertEquals(game.currentDrawStack.size, coordinates1.size)

        rootService.playerService.placeTile(AxialPos(1, -3))

        val coordinates = aiServices.findAllValidPositions()

        //assertEquals(game.currentDrawStack.size, coordinates.size)

    }


    @Test
    fun testSetCurrentState() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val newBoard: MutableMap<AxialPos, Tile> = mutableMapOf()
        val newDrawStack: MutableList<RouteTile> = mutableListOf(RouteTile(TileType.TILE0))
        val newPlayers: List<Player> =
            listOf(Player("one", Color.BLUE, heldTile = RouteTile(TileType.TILE4), isAI = false, smartAI = false))
        val newGems: MutableList<Gem> =
            mutableListOf(Gem.AMBER, Gem.EMERALD, Gem.EMERALD)

        val newGameState = GameState(newBoard, newDrawStack, newPlayers, newGems)
        val aiServices = AIServices(rootService)

        aiServices.setCurrentState(newGameState)
        val game = rootService.currentGame
        checkNotNull(game)

        val settedGameSate = GameState(
            game.currentBoard,
            game.currentDrawStack,
            game.currentPlayers,
            game.currentGems)

        //assert setting the Game state
        assertEquals(newGameState, settedGameSate)

        //assert getting the Game state
        val gottenState = aiServices.getCurrentState()
        assertEquals(newGameState, gottenState)
    }

    @Test
    fun testAllTilePossibleRotations() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val aiServices = AIServices(rootService)
        val game = rootService.currentGame
        checkNotNull(game)
        val tile = game.playerAtTurn.heldTile
        checkNotNull(tile)
        val possibleRotations = aiServices.getAllTilePossibleRotations(tile = tile)

        assertEquals(tile, game.playerAtTurn.heldTile)
    }

}