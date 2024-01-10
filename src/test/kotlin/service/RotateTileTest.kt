package service

import entity.Color
import entity.Player
import entity.RouteTile
import entity.TileType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * This class contains test cases for the [PlayerService.rotateTile] function.
 */
class RotateTileTest {

    private val testRootService = RootService()

    /**
     * Sets up the necessary game before each test case.
     */
    @BeforeTest
    fun setUpGame() {
        testRootService.gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
    }

    /**
     * Test function to verify the behavior when the game is not started.
     *
     * @throws IllegalStateException if the game has not been started.
     */
    @Test
    fun testGameNotStarted() {
        testRootService.currentGame = null
        assertFailsWith<IllegalStateException> {
            testRootService.playerService.rotateTile()
        }
    }

    /**
     * Test function to verify the behavior when the current player does not have a held tile.
     *
     * @throws IllegalStateException if the player at turn does not have a held tile.
     */
    @Test
    fun testNoHeldTile() {
        val game = testRootService.currentGame
        checkNotNull(game)

        game.playerAtTurn.heldTile = null

        assertFailsWith<IllegalStateException> {
            testRootService.playerService.rotateTile()
        }
    }

    /**
     * Test function to verify modulo cycle behavior for the tile rotation.
     *
     * This function rotates the tile six times using the player service and checks
     * if the held tile's rotation resets to 0 after a full cycle.
     *
     * @throws AssertionError if the held tile's rotation is not reset to 0.
     */
    @Test
    fun testModuloCycle() {
        val game = testRootService.currentGame
        checkNotNull(game)

        repeat(6) {
            testRootService.playerService.rotateTile()
        }

        val heldTile = game.playerAtTurn.heldTile
        checkNotNull(heldTile)
        assertEquals(0, heldTile.rotation)
    }

    /**
     * Test function to verify a single cycle behavior for the tile rotation.
     *
     * This function rotates the tile once using the player service and checks
     * if the held tile's rotation increments to 1 after one rotation cycle.
     *
     * @throws AssertionError if the held tile's rotation is not 1.
     */
    @Test
    fun testOneCycle() {
        val game = testRootService.currentGame
        checkNotNull(game)

        testRootService.playerService.rotateTile()

        val heldTile = game.playerAtTurn.heldTile
        checkNotNull(heldTile)
        assertEquals(1, heldTile.rotation)
    }

}