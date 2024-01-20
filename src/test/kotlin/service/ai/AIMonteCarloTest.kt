package service.ai

import entity.*
import service.GameService
import service.PlayerService
import service.RootService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The `AIMonteCarloTest` class contains unit tests for the AI Monte Carlo functionalities.
 * It tests the UCB (Upper Confidence Bound) calculation, generation of child nodes, selection of the next state,
 * and overall Monte Carlo training of the AI agent.
 *
 * @property gameService An instance of the GameService used for setting up and managing game-related operations.
 * @property playerService An instance of the PlayerService responsible for player-related functionalities.
 * @property rootService An instance of the RootService managing the root-level game data and services.
 * @property aiServices An instance of the AIServices providing AI-related functionalities.
 */
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

    /**
     * Test the UCB (Upper Confidence Bound) calculation in the AI Monte Carlo algorithm.
     */
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

    /**
     * Test the generation of child nodes in the AI Monte Carlo algorithm.
     */
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

    /**
     * Test the selection of the next state in the AI Monte Carlo algorithm.
     */
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

    /**
     * Test the overall Monte Carlo training of the AI agent.
     */
    @Test
    fun testMonteCarloTraining() {

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