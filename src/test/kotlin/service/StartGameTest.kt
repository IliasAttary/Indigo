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
    fun testStartGameTwoPlayers() {
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
        assertEquals(12, currentGame.currentGems.size)
        assertEquals(52, currentGame.currentDrawStack.size)
        assertEquals(0, currentGame.undoStack.size)
        assertEquals(0, currentGame.redoStack.size)

        val middleTreasureTile = currentGame.currentBoard[AxialPos(0, 0)] as TreasureTile
        middleTreasureTile.gems?.let { assertEquals(6, it.size) }



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

    /**
     * Tests the startGame with 3 players with SharedGates
     */
    @Test
    fun testStartGameThreePlayersSharedGates(){
        val mc = RootService()

        mc.gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false),
                Player("P3", Color.WHITE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        assertNotNull(mc.currentGame)

        val currentGame = mc.currentGame
        assertNotEquals(currentGame!!.currentPlayers[0].name, currentGame.currentPlayers[1].name,
            currentGame.currentPlayers[2].name)
        assertNotNull(currentGame.playerAtTurn)
        assertEquals(31, currentGame.currentBoard.size)
        assertEquals(12, currentGame.currentGems.size)
        assertEquals(51, currentGame.currentDrawStack.size)
        assertEquals(0, currentGame.undoStack.size)
        assertEquals(0, currentGame.redoStack.size)


    }

    /**
     * Tests the startGame with 3 players without SharedGates
     */
    @Test
    fun testStartGameThreePlayersNoSharedGates(){
        val mc = RootService()

        mc.gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false),
                Player("P3", Color.WHITE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = true
        )

        assertNotNull(mc.currentGame)

        val currentGame = mc.currentGame
        assertNotEquals(currentGame!!.currentPlayers[0].name, currentGame.currentPlayers[1].name,
            currentGame.currentPlayers[2].name)
        assertNotNull(currentGame.playerAtTurn)
        assertEquals(31, currentGame.currentBoard.size)
        assertEquals(12, currentGame.currentGems.size)
        assertEquals(51, currentGame.currentDrawStack.size)
        assertEquals(0, currentGame.undoStack.size)
        assertEquals(0, currentGame.redoStack.size)


    }

    /**
     * Tests the startGame with 4 players
     */
    @Test
    fun testStartGameFourPlayers(){
        val mc = RootService()

        mc.gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false),
                Player("P3", Color.WHITE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false),
                Player("P4", Color.PURPLE, heldTile = RouteTile(TileType.TILE3), isAI = false, smartAI = false),
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        assertNotNull(mc.currentGame)

        val currentGame = mc.currentGame
        assertNotEquals(currentGame!!.currentPlayers[0].name, currentGame.currentPlayers[1].name,
            currentGame.currentPlayers[2].name)
        assertNotNull(currentGame.playerAtTurn)
        assertEquals(31, currentGame.currentBoard.size)
        assertEquals(12, currentGame.currentGems.size)
        assertEquals(50, currentGame.currentDrawStack.size)
        assertEquals(0, currentGame.undoStack.size)
        assertEquals(0, currentGame.redoStack.size)


    }

    /**
     * Test startGame for AI as first player
     */
    @Test
    fun testStartGameFirstPlayerAI(){
        val mc = RootService()

        mc.gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = true, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = false, smartAI = false),
                Player("P3", Color.WHITE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false),
                Player("P4", Color.PURPLE, heldTile = RouteTile(TileType.TILE3), isAI = false, smartAI = false),
            ),
            aiSpeed = 1000,
            sharedGates = false
        )
        Thread.sleep(3000)

        //Check if the AI player makes the first move
        val game = mc.currentGame
        checkNotNull(game)
        assertEquals(32,game.currentBoard.size)
    }
}