package service

import entity.*
import kotlin.test.*

/**
 * This class contains test cases for the [PlayerService.redo] function.
 */
class RedoTest {

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
            testRootService.playerService.redo()
        }
    }

    /**
     * Tests the behavior when attempting an undo action with an empty redo stack.
     *
     * This test checks that an [IllegalArgumentException] is thrown when the
     * redo method is invoked from the player service and the redo stack is empty.
     *
     * @throws IllegalArgumentException if the undo stack is empty when attempting to undo.
     */
    @Test
    fun testEmptyUndoStack() {
        val game = testRootService.currentGame
        checkNotNull(game)

        assertFailsWith<IllegalArgumentException> {
            testRootService.playerService.redo()
        }
    }

    /**
     * Test function to validate the redo functionality.
     *
     * This test places a tile with the current player, performs an undo operation,
     * then redoes the action. It ensures that player references and game state are
     * correctly restored after the redo operation, and confirms that the undo stack
     * is not empty.
     */
    @Test
    fun testRedo() {
        val game = testRootService.currentGame
        checkNotNull(game)

        //make a move with the current Player
        testRootService.playerService.placeTile(AxialPos(1, 2))

        // get the reference information
        val currentPlayerAtTurn = game.playerAtTurn
        val currentBoardSize = game.currentBoard.size
        val currentDrawStackSize = game.currentDrawStack.size
        val currentGemsSize = game.currentGems.size

        testRootService.playerService.undo()
        testRootService.playerService.redo()

        assertEquals(currentPlayerAtTurn, game.playerAtTurn)
        assertEquals(currentBoardSize, game.currentBoard.size)
        assertEquals(currentDrawStackSize, game.currentDrawStack.size)
        assertEquals(currentGemsSize, game.currentGems.size)
        assert(game.undoStack.isNotEmpty())
    }
}