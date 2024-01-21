package service

import entity.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

/**
 * This class contains test cases for the [PlayerService.checkPlacement] function.
 */

class CheckPlacementTest {

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
     * Tests the behavior of checkPlacement when the game has not started.
     * Expects an IllegalStateException to be thrown.
     */
    @Test
    fun testGameNotStarted() {
        assertFailsWith<IllegalStateException> {
            playerService.checkPlacement(AxialPos(1, 2))
        }
    }

    /**
     * Tests the behavior of checkPlacement when no tile is held by the player.
     * Expects an IllegalArgumentException to be thrown.
     */
    @Test
    fun testNoHeldTile() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = null, isAI = false, smartAI = false),
                Player("P2", Color.RED, heldTile = null, isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)

        game.currentPlayers[0].heldTile = null

        assertFailsWith<IllegalArgumentException> {
            playerService.checkPlacement(AxialPos(1, 2))
        }
    }

    /**
     * Tests the behavior of checkPlacement for placing a tile on an occupied space.
     * Expects the method to return false indicating an invalid placement.
     */
    @Test
    fun testTilePlacementOnOccupiedSpace() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = null, isAI = false, smartAI = false),
                Player("P2", Color.RED, heldTile = null, isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE0)
        playerService.placeTile(AxialPos(1, 2))

        assertFalse(playerService.checkPlacement(AxialPos(1, 2)))
    }

    /**
     * Tests valid placement scenarios for non-curved tiles.
     * Expects the method to return true, indicating a valid placement.
     */
    @Test
    fun testValidPlacementForNonCurvedTile() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = null, isAI = false, smartAI = false),
                Player("P2", Color.RED, heldTile = null, isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE0)
        assert(playerService.checkPlacement(AxialPos(1, 2)))

        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE1)
        assert(playerService.checkPlacement(AxialPos(1, 2)))
    }

    /**
     * Tests the placement of a tile not next to a gateway tile.
     * Expects the method to return true for valid placement away from gateway tiles.
     */
    @Test
    fun testPlacementNotNextToGatewayTile() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = null, isAI = false, smartAI = false),
                Player("P2", Color.RED, heldTile = null, isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE3)

        assert(playerService.checkPlacement(AxialPos(-2, 0)))
    }

    /**
     * Tests the placement of a tile next to a gateway tile.
     * Expects the method to return false, indicating an invalid placement.
     */
    @Test
    fun testPlacementNextToGatewayTile() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = null, isAI = false, smartAI = false),
                Player("P2", Color.RED, heldTile = null, isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        // gate 1
        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE2)
        rootService.currentGame?.playerAtTurn?.heldTile!!.rotation = 1

        assertFalse(playerService.checkPlacement(AxialPos(3, -4)))

        // gate 2
        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE2)
        rootService.currentGame?.playerAtTurn?.heldTile!!.rotation = 2

        assertFalse(playerService.checkPlacement(AxialPos(4, -1)))

        // gate 3
        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE2)
        rootService.currentGame?.playerAtTurn?.heldTile!!.rotation = 3

        assertFalse(playerService.checkPlacement(AxialPos(1, 3)))

        // gate 4
        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE2)
        rootService.currentGame?.playerAtTurn?.heldTile!!.rotation = 4

        assertFalse(playerService.checkPlacement(AxialPos(-3, 4)))

        // gate 5
        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE2)
        rootService.currentGame?.playerAtTurn?.heldTile!!.rotation = 5

        assertFalse(playerService.checkPlacement(AxialPos(-4, 1)))

        // gate 6
        rootService.currentGame?.playerAtTurn?.heldTile = RouteTile(TileType.TILE2)
        rootService.currentGame?.playerAtTurn?.heldTile!!.rotation = 0

        assertFalse(playerService.checkPlacement(AxialPos(-1, -3)))
    }
}