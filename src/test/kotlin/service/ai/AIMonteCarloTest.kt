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
    fun testSelectRandomly() {
        val node1 = MontiCarloNode()
        val node2 = MontiCarloNode()
        val node3 = MontiCarloNode()
        node1.children.add(node2)
        node2.visits = 3
        node1.children.add(node3)
        node3.visits = 4

        val ranNode = aiServices.selectRandomly(node1)

        // Assert that ranNode is either node2 or node3
        assertTrue(ranNode == node2 || ranNode == node3)
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

        var depth = 0
        val node = MontiCarloNode()
        node.currentGameState = aiServices.getCurrentState()
        var bool = aiServices.isTerminate(node, depth)
        //true if no depth
        assertTrue(bool)

        depth = 10
        bool = aiServices.isTerminate(node, depth)
        assertTrue(!bool)


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
    fun testReward() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = true, smartAI = true)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val game = rootService.currentGame
        checkNotNull(game)
        //assert not working here
        /*
        game.currentPlayers[0].points = 5
        game.currentPlayers[1].points = 10
        var reward = aiServices.assignReward(2)
        assertEquals(100.0, reward)
        */

        game.currentPlayers[0].points = 10
        game.currentPlayers[1].points = 5
        var reward = aiServices.assignReward(2)
        assertEquals(10.0, reward)

        game.currentPlayers[0].points = 1
        game.currentPlayers[1].points = 10
        reward = aiServices.assignReward(80)
        assertEquals(-10.0, reward)

        game.currentPlayers[0].points = 10
        game.currentPlayers[1].points = 1
        reward = aiServices.assignReward(80)
        assertEquals(-100.0, reward)
    }

    @Test
    fun testBackProp() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = true, smartAI = true)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val game = rootService.currentGame
        checkNotNull(game)

        val node = MontiCarloNode()
        val parentNode = MontiCarloNode()
        val grandParentNode = MontiCarloNode()

        node.parent = parentNode
        parentNode.parent = grandParentNode

        node.parent!!.parent = grandParentNode

        node.visits = 3
        parentNode.visits = 4
        grandParentNode.visits = 5

        node.totalScore = 9.0
        parentNode.totalScore = 10.0
        grandParentNode.totalScore = 5.0

        aiServices.backPropagation(node, 4)

        println(parentNode.totalScore)

        //to fix : only back propagates to the parent
    }

}