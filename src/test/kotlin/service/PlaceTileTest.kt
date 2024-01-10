package service

import entity.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * This class contains test cases for the [PlayerService.placeTile] function.
 */
class PlaceTileTest {

    /**
     * Tests the placeTile function.
     * It covers multiple scenarios including trying to place a tile before the game starts,
     * placing a tile without holding one, successfully placing a tile,
     * and attempting to place a tile on an occupied space.
     */
    @Test
    fun testPlaceTile() {
        val mc = RootService()
        assertFails { mc.playerService.placeTile(AxialPos(1, -3)) }

        mc.gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = mc.currentGame
        checkNotNull(game)

        val heldTile = game.playerAtTurn.heldTile

        game.playerAtTurn.heldTile = null
        assertFails { mc.playerService.placeTile(AxialPos(1, -3)) }

        game.playerAtTurn.heldTile = heldTile
        mc.playerService.placeTile(AxialPos(1, -3))
        assertEquals(1, game.undoStack.size)

        assertFails { mc.playerService.placeTile(AxialPos(1, -3)) }
    }
}