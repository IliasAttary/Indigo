package service

import entity.*
import kotlin.test.*

/**
 * This class contains test cases for the [PlayerService.moveGems] function.
 */
class MoveGemsTest {

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
            testRootService.playerService.moveGems(AxialPos(1,1))
        }
    }

    /**
     * Test function to verify the behavior when the tile is null.
     *
     * @throws IllegalArgumentException if an attempt is made to move gems when the tile is null.
     */
    @Test
    fun testTileNull(){
        val game = testRootService.currentGame
        checkNotNull(game)

        assertFailsWith<IllegalArgumentException> {
            testRootService.playerService.moveGems(AxialPos(1,1))
        }
    }

    /**
     * Test function to verify the behavior when the current tile is not a route tile.
     *
     * @throws IllegalArgumentException if an attempt is made to move gems on a non-route tile.
     */
    @Test
    fun testTileIsNotRouteTile(){
        val game = testRootService.currentGame
        checkNotNull(game)

        assertFailsWith<IllegalArgumentException> {
            testRootService.playerService.moveGems(AxialPos(0,0))
        }
    }


    /**
     * Test function to verify the computation of [PlayerService.tilePathsWithRotation]
     */
    @Test
    fun testPathsWithRotation(){
        val game = testRootService.currentGame
        checkNotNull(game)

        val newPaths = testRootService.playerService.tilePathsWithRotation(TileType.TILE0.paths, 1)

        assertEquals(newPaths[1],3)
        assertEquals(newPaths[2],5)
        assertEquals(newPaths[3],1)
        assertEquals(newPaths[4],0)
        assertEquals(newPaths[5],2)
        assertEquals(newPaths[0],4)
    }


    /**
     * Test function to validate moving gems from a treasure tile:
     * Places a tile on the board.
     * Confirms if the placed tile receives a gem.
     * Verifies gem removal from the treasure tile.
     */
    @Test
    fun testMoveGemsFromTreasureTile(){
        val game = testRootService.currentGame
        checkNotNull(game)

        game.playerAtTurn.heldTile = RouteTile(TileType.TILE0)

        val placedTile = game.playerAtTurn.heldTile
        testRootService.playerService.placeTile(AxialPos(1,-1))

        //check if the placedTile received the gem
        if (placedTile != null) {
            assertEquals(1, placedTile.gemPositions.size)
        }

        //check if the gem is at exit 5 of the placedTile
        if (placedTile != null) {
            assertEquals(Gem.EMERALD, placedTile.gemPositions[5])
        }

        val middleTreasureTile = game.currentBoard[AxialPos(0,0)]

        //check if the gem got removed from the treasureTile
        if(middleTreasureTile is TreasureTile){
            middleTreasureTile.gemPositions?.let { assertEquals(5, it.size) }
        }

    }

    /**
     * Test function to validate moving gems from one route tile to another route tile:
     * Places a tile at the edge of the middle TreasureTile.
     * Places a tile which is at the edge of the first placed tile
     * Checks that the second placed route tile receives a gem and if it gets removed from the first one
     */
    @Test
    fun testMoveGemsFromRouteTileToRouteTile(){
        val game = testRootService.currentGame
        checkNotNull(game)

        game.playerAtTurn.heldTile = RouteTile(TileType.TILE0)

        val firstPlacedTile = game.playerAtTurn.heldTile

        //same move as in the test before
        testRootService.playerService.placeTile(AxialPos(1,-1))

        //check if the placedTile received the gem
        if (firstPlacedTile != null) {
            assertEquals(1, firstPlacedTile.gemPositions.size)
        }

        game.playerAtTurn.heldTile = RouteTile(TileType.TILE1)
        val secondPlacedTile = game.playerAtTurn.heldTile

        // place the second tile (second Player)
        testRootService.playerService.placeTile(AxialPos(1,-2))

        //check if the gem got removed from the first placed Tile
        if (firstPlacedTile != null) {
            assertEquals(0, firstPlacedTile.gemPositions.size)
        }

        // check if the gem got added onto the second placed tile
        if (secondPlacedTile != null) {
            assertEquals(1, secondPlacedTile.gemPositions.size)
        }

        // check if the gem is on exit 5
        if (secondPlacedTile != null) {
            assertEquals(Gem.EMERALD, secondPlacedTile.gemPositions[5])
        }

    }

    /**
     * Test function to validate the behavior of colliding gems:
     * Assigns curved RouteTiles to both players.
     * Places tiles at specific positions to simulate a gem collision.
     * Confirm that the gems got removed.
     */
    @Test
    fun testCollidingGems(){
        val game = testRootService.currentGame
        checkNotNull(game)

        //give both players the curved RouteTiles
        game.currentPlayers[0].heldTile = RouteTile(TileType.TILE4)
        game.currentPlayers[1].heldTile = RouteTile(TileType.TILE4)

        val firstPlacedTile = game.currentPlayers[0].heldTile
        val secondPlacedTile = game.currentPlayers[1].heldTile

        testRootService.playerService.placeTile(AxialPos(1,-1))

        //check if there is a gem on the tile and if it is on exit 4
        if (firstPlacedTile != null) {
            assertEquals(1, firstPlacedTile.gemPositions.size)
            assertEquals(Gem.EMERALD, firstPlacedTile.gemPositions[4])
        }

        testRootService.playerService.placeTile(AxialPos(0,-1))

        //check if the colliding gems got removed
        if (firstPlacedTile != null) {
            assertEquals(0, firstPlacedTile.gemPositions.size)
        }

        if (secondPlacedTile != null) {
            assertEquals(0, secondPlacedTile.gemPositions.size)
        }
    }
}