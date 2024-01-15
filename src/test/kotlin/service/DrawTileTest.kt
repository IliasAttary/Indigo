package service

import entity.Color
import entity.Player
import entity.RouteTile
import entity.TileType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails


/**
 * This class contains test cases for the [PlayerService.drawTile] function.
 */
class DrawTileTest {

    /**
     * Tests the drawTile function.
     * Initially, it tries to draw a tile before the game starts, expecting the operation to fail.
     * Then, it starts a game and tests if a tile is successfully drawn from the draw stack
     * and assigned to the current player.
     */
    @Test
    fun testDrawTile() {

        val mc = RootService()

        assertFails { mc.playerService.drawTile() }

        mc.gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val lastTile = mc.currentGame!!.currentDrawStack.last()

        mc.playerService.drawTile()
        assertEquals(lastTile, mc.currentGame!!.playerAtTurn.heldTile)
    }
}