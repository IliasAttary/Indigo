package service

import entity.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * This class contains test cases for the [PlayerService.undo] function.
 */
class UndoTest {

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
            testRootService.playerService.undo()
        }
    }

    /**
     * Tests the behavior when attempting an undo action with an empty undo stack.
     *
     * This test checks that an [IllegalArgumentException] is thrown when the
     * undo method is invoked from the player service and the undo stack is empty.
     *
     * @throws IllegalArgumentException if the undo stack is empty when attempting to undo.
     */
    @Test
    fun testEmptyUndoStack() {
        val game = testRootService.currentGame
        checkNotNull(game)

        assertFailsWith<IllegalArgumentException> {
            testRootService.playerService.undo()
        }
    }

    /**
     * Test function to validate the undo functionality.
     *
     * This test checks if undoing a move restores the player's turn,
     * retains the previous board and draw stack states, and fills the redo stack.
     */
    @Test
    fun testUndo() {
        val game = testRootService.currentGame
        checkNotNull(game)

        //test if the player changes
        val clonedGameState = testRootService.gameService.cloneGameState()
        val currentPlayerAtTurn = clonedGameState.playerAtTurn

        //make a move with the first player to set the GameState
        testRootService.playerService.placeTile(AxialPos(2, 1))
        testRootService.playerService.undo()

        assertEquals(currentPlayerAtTurn, game.playerAtTurn)

        //test if the drawStack and the board get set to the last version

        val currentBoardSize = game.currentBoard.size
        val currentDrawStackSize = game.currentDrawStack.size
        val currentGemsSize = game.currentGems.size

        testRootService.playerService.placeTile(AxialPos(-2, 0))
        testRootService.playerService.undo()

        assertEquals(currentBoardSize, game.currentBoard.size)
        assertEquals(currentDrawStackSize, game.currentDrawStack.size)
        assertEquals(currentGemsSize, game.currentGems.size)
        assert(game.redoStack.isNotEmpty())
    }
}