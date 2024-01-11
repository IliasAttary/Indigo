package service
import entity.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


/**
 * The GameService class manages game-related logic and actions that affect the overall game state.
 *
 * This class provides key functionalities for handling game-related tasks, including the creation
 * of the game-board, the initialization of the game's draw stack
 * and the start of a new game with the specified player names.
 *
 * @property rootService The reference to the root service, enabling communication with the core game entity.
 */

class GameService(private val rootService:RootService):AbstractRefreshingService() {

    /**
     * Initializes and starts a new game with the specified parameters.
     *
     * @param players List of players in the game.
     * @param aiSpeed simulation speed of the artificial intelligence (AI) players in milliseconds.
     * @param sharedGates indicating whether the gates are shared among players.
     *
     * @throws IllegalArgumentException if player names are empty or not unique.
     *
     */
    fun startGame(players : List<Player>,
                  aiSpeed : Int,
                  drawStack : MutableList<RouteTile> = initializeDrawStack(),
                  sharedGates:Boolean){

        //check if the player names are valid
        val playerNames = players.map { it.name }.toMutableList()

        check(playerNames.all { it.isNotEmpty() }) { "The players need to have a name !" }
        check(playerNames.toSet().size == playerNames.size) { "The players need to have different names !" }

        val allGems = mutableListOf<Gem>()
        repeat(6){
            allGems.add(Gem.AMBER)
            allGems.add(Gem.AMBER)
        }

        repeat(5){
            allGems.add(Gem.EMERALD)
            allGems.add(Gem.EMERALD)
        }

        allGems.add(Gem.SAPPHIRE)
        allGems.add(Gem.SAPPHIRE)

        rootService.currentGame = Game(
            currentPlayers = players,
            currentBoard = initializeBoard(players, sharedGates),
            currentDrawStack = drawStack,
            aiMoveMilliseconds = aiSpeed,
            sharedGates = sharedGates,
            playerAtTurn = players.first(),
            currentGems = allGems)

        //refresh view layer
        onAllRefreshables { refreshAfterNewGame() }

    }

    /**
     * Initializes the game board based on the specified players and gate sharing configuration.
     *
     * @param players List of players participating in the game.
     * @param sharedGates indicating whether the gates are shared among players.
     * @return Map representing the initialized game board with Axial Positions as keys and Tiles as values.
     */
    private fun initializeBoard(players : List<Player>, sharedGates : Boolean) : MutableMap<AxialPos,Tile>{

        val gameBoard = mutableMapOf<AxialPos,Tile>()
        val gemsOnMiddleTreasureTile = mutableListOf(Gem.SAPPHIRE, Gem.EMERALD, Gem.EMERALD,
                                                     Gem.EMERALD, Gem.EMERALD, Gem.EMERALD)

        // place the tiles onto the game-board by filling the map

        // place all TreasureTiles
        placeTreasureTile(gameBoard,AxialPos(0,-4), 3)
        placeTreasureTile(gameBoard,AxialPos(4,-4), 4)
        placeTreasureTile(gameBoard,AxialPos(4,0), 5)
        placeTreasureTile(gameBoard,AxialPos(0,4), 0)
        placeTreasureTile(gameBoard,AxialPos(-4,4),1)
        placeTreasureTile(gameBoard,AxialPos(-4,0),2)

        // place middle TreasureTile
        gameBoard[AxialPos(0,0)] = TreasureTile(null,gemsOnMiddleTreasureTile)

        // place all GateWayTiles

        // Gates at 1
        placeGateWayTile(gameBoard,AxialPos(1,-5),1,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(2,-5),1,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(3,-5),1,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(4,-5),1,players,sharedGates)

        // Gates at 2
        placeGateWayTile(gameBoard,AxialPos(5,-4),2,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(5,-3),2,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(5,-2),2,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(5,-1),2,players,sharedGates)

        //Gates at 3
        placeGateWayTile(gameBoard,AxialPos(4,1),3,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(3,2),3,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(2,3),3,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(1,4),3,players,sharedGates)

        //Gates at 4
        placeGateWayTile(gameBoard,AxialPos(-1,5),4,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-2,5),4,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-3,5),4,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-4,5),4,players,sharedGates)

        //Gates at 5
        placeGateWayTile(gameBoard,AxialPos(-5,4),5,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-5,3),5,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-5,2),5,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-5,1),5,players,sharedGates)

        //Gates at 6
        placeGateWayTile(gameBoard,AxialPos(-4,-1),6,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-3,-2),6,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-2,-3),6,players,sharedGates)
        placeGateWayTile(gameBoard,AxialPos(-1,-4),6,players,sharedGates)

        return gameBoard
    }

    /**
     * Searches for players with specified colors in the given list of players.
     *
     * @param players List of players to search through.
     * @param color1 First color to search for.
     * @param color2 Second color to search for.
     * @return MutableList<Player> containing players with the specified colors.
     */
    private fun searchPlayerWithColor(players : List<Player>, color1:Color, color2:Color) : MutableList<Player>{

        var color1Player = players.first()
        var color2Player = players.first()

        for(player in players){
            if(player.color == color1){
                color1Player = player
            }
            if(player.color == color2){
                color2Player = player
            }
        }
        return mutableListOf(color1Player,color2Player)
    }

    /**
     * Places a treasure tile on the game board at the specified axial position (q, r).
     *
     * @param board The mutable map representing the game board.
     * @param coordinates The AxialPos where the tile gets placed to
     */
    private fun placeTreasureTile(board : MutableMap<AxialPos, Tile>, coordinates: AxialPos, rotation : Int){
        val gemPositions = mutableMapOf<Int,Gem>()
        gemPositions[rotation] = Gem.AMBER
        val tile = TreasureTile(gemPositions, null)
        tile.rotation = rotation
        board[coordinates] = tile
    }

    /**
     * Places a gateway tile on the game board at the specified axial position (q, r).
     *
     * @param board The mutable map representing the game board.
     * @param coordinates the coordinates where the tile should get placed
     * @param gate determines where the gate is on the game-board
     * @param players The list of players in the game.
     * @param sharedGates indicating whether gates are shared among players.
     */
    private fun placeGateWayTile(
        board: MutableMap<AxialPos, Tile>,
        coordinates: AxialPos,
        gate: Int,
        players: List<Player>,
        sharedGates: Boolean
    ) {
        var gatePlayers = determineGatePlayers(gate, players, sharedGates)

        // Remove duplicate player if both assigned players are the same
        if (gatePlayers.size > 1 && gatePlayers.first() == gatePlayers.last()) {
            gatePlayers = mutableListOf(gatePlayers.first())
        }

        board[coordinates] = GatewayTile(gatePlayers, gate)
    }

    private fun determineGatePlayers(gate: Int, players: List<Player>, sharedGates: Boolean): MutableList<Player> {
        val colorMapping = getColorMappingForPlayers(players.size, sharedGates)
        val playerColors = colorMapping[gate] ?: return mutableListOf(players[0])
        return searchPlayerWithColor(players, playerColors.first, playerColors.second)
    }

    private fun getColorMappingForPlayers(playerCount: Int, sharedGates: Boolean): Map<Int, Pair<Color, Color>> {
        return when (playerCount) {
            2 -> mapOf(
                1 to Pair(Color.RED, Color.RED),
                2 to Pair(Color.BLUE, Color.BLUE),
                3 to Pair(Color.RED, Color.RED),
                4 to Pair(Color.BLUE, Color.BLUE),
                5 to Pair(Color.RED, Color.RED),
                6 to Pair(Color.BLUE, Color.BLUE),
            )

            3 -> if (sharedGates) {
                mapOf(
                    1 to Pair(Color.RED, Color.RED),
                    2 to Pair(Color.RED, Color.BLUE),
                    3 to Pair(Color.WHITE, Color.WHITE),
                    4 to Pair(Color.WHITE, Color.RED),
                    5 to Pair(Color.BLUE, Color.BLUE),
                    6 to Pair(Color.BLUE, Color.WHITE)
                )
            } else {
                mapOf(
                    1 to Pair(Color.RED, Color.RED),
                    2 to Pair(Color.BLUE, Color.BLUE),
                    3 to Pair(Color.WHITE, Color.WHITE),
                    4 to Pair(Color.RED, Color.RED),
                    5 to Pair(Color.BLUE, Color.BLUE),
                    6 to Pair(Color.WHITE, Color.WHITE)
                )
            }

            4 -> mapOf(
                1 to Pair(Color.RED, Color.BLUE),
                2 to Pair(Color.BLUE, Color.WHITE),
                3 to Pair(Color.RED, Color.PURPLE),
                4 to Pair(Color.PURPLE, Color.BLUE),
                5 to Pair(Color.WHITE, Color.RED),
                6 to Pair(Color.WHITE, Color.PURPLE)
            )

            else -> emptyMap()
        }
    }

    /**
     * Initializes the draw stack for the game, consisting of a shuffled collection of route tiles.
     *
     * @return ArrayDeque<Tile> representing the initialized draw stack.
     */
    private fun initializeDrawStack() : MutableList<RouteTile>{

        val drawStack = mutableListOf<RouteTile>()

        repeat(14){
            drawStack.add(RouteTile(TileType.TILE0))
        }

        repeat(6){
            drawStack.add(RouteTile(TileType.TILE1))
        }

        repeat(14){
            drawStack.add(RouteTile(TileType.TILE2))
        }

        repeat(14){
            drawStack.add(RouteTile(TileType.TILE3))
        }

        repeat(6){
            drawStack.add(RouteTile(TileType.TILE4))
        }

        drawStack.shuffle()

        return drawStack
    }

    /**
     * Ends the current game session.
     *
     * @throws IllegalStateException if called when no game is in progress.
     */
    fun endGame(){
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet!"}

        val players = game.currentPlayers
        rootService.currentGame = null

        onAllRefreshables{refreshAfterEndGame(players)}
    }

    /**
     * saves the current Game in the file "saveGame.ser"
     */
    fun save() {
        val file = File("saveGame.ser")
        val json = Json {
            allowStructuredMapKeys = true
        }
        file.writeText(json.encodeToString(rootService.currentGame))
    }

    /**
     * loads the current Game from the "saveGame.ser" file
     */
    fun load() {
        val file = File("saveGame.ser")
        val json = Json {
            allowStructuredMapKeys = true
        }
        rootService.currentGame = json.decodeFromString<Game>(file.readText())
        onAllRefreshables { refreshAfterNewGame() }
    }
}