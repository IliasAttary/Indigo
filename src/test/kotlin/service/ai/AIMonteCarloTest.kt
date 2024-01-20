package service.ai

import entity.*
import service.GameService
import service.PlayerService
import service.RootService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AIMonteCarloTest {
    private lateinit var gameService: GameService
    private lateinit var playerService: PlayerService
    private lateinit var rootService: RootService
    private lateinit var aiServices: AIServices

    /**
     * Sets up the necessary services and initializes them before each test case.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        playerService = PlayerService(rootService)
        gameService = GameService(rootService)
        aiServices = AIServices(rootService)
    }

    @Test
    fun testUCB() {
        val upperBound = Double.POSITIVE_INFINITY
        val node = MontiCarloNode()
        node.parent = MontiCarloNode()
        node.parent!!.visits = 1
        node.visits = 3
        node.totalScore = 9.0

        var bound = aiServices.calculateUpperConfidenceBound(node,1.0)
        assertEquals(3.0, bound)

        node.visits = 0
        node.totalScore = 3.0

        bound = aiServices.calculateUpperConfidenceBound(node,1.0)
        assertEquals(upperBound, bound)
    }

    @Test
    fun testGenerateChildNodes() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val game = rootService.currentGame
        checkNotNull(game)

        val gameSate = GameState(
            game.currentBoard,
            game.currentDrawStack,
            game.currentPlayers,
            game.currentGems)

        val action: Pair<AxialPos, Tile> = Pair(AxialPos(-1,1), RouteTile(TileType.TILE1))
        val node1 = MontiCarloNode()
        node1.parent = MontiCarloNode()
        node1.parent!!.visits = 1
        node1.visits = 3
        node1.totalScore = 9.0

        assertEquals(0, node1.children.size)

        aiServices.generateChildNodes(node1, gameSate, action)

        //assert that node has one more children
        assertEquals(1, node1.children.size)

        //assert that action coordination was set correctly
        assertEquals(AxialPos(-1,1), node1.children.first().action?.first)

        //assert that action Tile was set correctly
        assertEquals(RouteTile(TileType.TILE1), node1.children.first().action?.second)
    }

    @Test
    fun testSelectNextState() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val game = rootService.currentGame
        checkNotNull(game)

        val gameSate = GameState(
            game.currentBoard,
            game.currentDrawStack,
            game.currentPlayers,
            game.currentGems)

        val action: Pair<AxialPos, Tile> = Pair(AxialPos(-1,1), RouteTile(TileType.TILE1))
        val node = MontiCarloNode()
        node.parent = MontiCarloNode()
        node.parent!!.visits = 1
        node.visits = 3
        node.totalScore = 9.0


        aiServices.generateChildNodes(node, gameSate, action)

        val node1 = aiServices.selectNextState(node)

        println(node1.action!!.first)
        println(node1.action!!.second)

    }


    @Test
    fun testExpansion() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val game = rootService.currentGame
        checkNotNull(game)

        val node = MontiCarloNode()
        val initialNodeChildrenNumber = node.children.size

        assertEquals(0, initialNodeChildrenNumber)

        aiServices.montiCarloExpansion(node)

        rootService.playerService.placeTile(AxialPos(-4,1))

        aiServices.montiCarloExpansion(node)


        val finalNodeChildrenNumber = node.children.size

        assertTrue(finalNodeChildrenNumber > initialNodeChildrenNumber)
    }

    @Test
    fun testTerminate() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val game = rootService.currentGame
        checkNotNull(game)

        val depth = 0
        val node = MontiCarloNode()
        node.currentGameState = aiServices.getCurrentState()
        val bool = aiServices.isTerminate(node, depth)
        //true if no depth
        assertTrue(bool)

    }

    @Test
    fun testSimulation() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val game = rootService.currentGame
        checkNotNull(game)

        val depth = 10
        val node = MontiCarloNode()
        node.currentGameState = aiServices.getCurrentState()

        aiServices.montiCarloSimulation(node, depth)
    }

    @Test
    fun testMonteCarloTraining() {
        //assertFails { playerService.placeTile(AxialPos(1, -3)) }

        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = true, smartAI = false),
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)
        val result = aiServices.trainMontiCarloAgent(2, 2, 2)

        if (result != null) {
            println("Selected Action: ${result.first} with Rotation: ${result.second.rotation}")
        } else {
            println("No action selected.")
        }
    }

}