package service

import entity.*
import org.junit.jupiter.api.Test
import kotlin.test.*


/**
 * This class contains test cases for the [GameService.startGame] function.
 */
class StartGameTest {

    /**
     * Tests the startGame function with different scenarios, including valid game initialization,
     * ending a game, and attempting to start a game with invalid configurations.
     */
    @Test
    fun testStartGame() {
        val mc = RootService()

        assertNull(mc.currentGame)

        mc.gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        assertNotNull(mc.currentGame)

        val currentGame = mc.currentGame
        assertNotEquals(currentGame!!.currentPlayers[0].name, currentGame.currentPlayers[1].name)
        assertNotNull(currentGame.playerAtTurn)
        assertEquals(31, currentGame.currentBoard.size)
        assertEquals(24, currentGame.currentGems.size)
        assertEquals(54, currentGame.currentDrawStack.size)
        assertEquals(0, currentGame.undoStack.size)
        assertEquals(0, currentGame.redoStack.size)

        val middleTreasureTile = currentGame.currentBoard[AxialPos(0, 0)]

        if (middleTreasureTile is TreasureTile) {
            assertEquals(6, middleTreasureTile.gems!!.size)
        }


        mc.gameService.endGame()

        assertNull(mc.currentGame)


        assertFails {
            mc.gameService.startGame(
                players = listOf(
                    Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                    Player("P1", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false)
                ),
                aiSpeed = 10,
                sharedGates = false
            )
        }
    }
}