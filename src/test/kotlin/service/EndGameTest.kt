package service

import entity.Color
import entity.Player
import entity.RouteTile
import entity.TileType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * This class contains test cases for the [GameService.endGame] function.
 */
class EndGameTest {
    private val testRootService = RootService()

    /**
     * Sets up the necessary services and initializes them before each test case.
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
            testRootService.gameService.endGame()
        }
    }

    /**
     * Ensures that the game is correctly terminated when the endGame method is invoked.
     *
     * @throws AssertionError if the game reference remains non-null after the call
     */
    @Test
    fun testGameEnded() {
        testRootService.gameService.endGame()
        assertNull(testRootService.currentGame)
    }
}