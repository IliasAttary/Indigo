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
    fun startGame(players : MutableList<Player>, aiSpeed : Int, sharedGates:Boolean){

        //check if the player names are valid
        val playerNames = players.map { it.name }.toMutableList()

        check(playerNames.all { it.isNotEmpty() }) { "The players need to have a name" }
        check(playerNames.toSet().size == playerNames.size) { "The players need to have different names" }

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

        rootService.currentGame = Game(currentPlayers = players,
            currentBoard = initializeBoard(players, sharedGates),
            currentDrawStack = initializeDrawStack(),
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

        var border = 4
        var wasAtSymmetricLine = false
        var offset = 0

        for(r in -4 .. 4){
            for(q in 0..border){

                // TilePositions for a TreasureTile

                if(r == -4 && q == 0){
                    placeTreasureTile(gameBoard,q,r)
                }

                if(r == -4 && q == 4){
                    placeTreasureTile(gameBoard,q,r)
                }

                //Middle TilePosition
                if(r == 0 && q == 4){
                    gameBoard[AxialPos(0,r)] = TreasureTile(null,gemsOnMiddleTreasureTile)
                }

                if(r == 0 && q == 0){
                    placeTreasureTile(gameBoard,-4,r)
                }

                if(r == 0 && q == 8){
                    placeTreasureTile(gameBoard,4,r)
                }

                if(r == 4 && q == 0){
                    placeTreasureTile(gameBoard,-4,r)
                }

                if(r == 4 && q == 4){
                    placeTreasureTile(gameBoard,0,r)
                }

                //TilePositions for a GatewayTile

                // Gates at 1
                if(r == -4 && 1 <= q && q <= 3){
                    placeGateWayTile(gameBoard,q,r,1,players,sharedGates)
                }

                // Gates at 2
                if( (r == -3 && q == 5) || (r == -2 && q == 6) || (r == -1 && q == 7) ){
                    placeGateWayTile(gameBoard,4,r,2,players,sharedGates)
                }

                // Gates at 3
                if( (r == 1 && q == 7) || (r == 2 && q == 6) || (r == 1 && q == 5) ){
                    placeGateWayTile(gameBoard,q-4,r,3,players,sharedGates)
                }

                // Gates at 4
                if(r == 4 && 1 <= q && q <= 3 ){
                    placeGateWayTile(gameBoard,q-4,r,4,players,sharedGates)
                }

                //Gates at 5
                if(r == 1 && q == 0 || r == 2 && q == 0 || r == 3 && q == 0){
                    placeGateWayTile(gameBoard,-4,r,5,players,sharedGates)
                }

                // Gates at 6
                if(r == -3 && q == 0){
                    placeGateWayTile(gameBoard,-1,r,6,players,sharedGates)
                }

                if(r == -2 && q == 0){
                    placeGateWayTile(gameBoard,-2,r,6,players,sharedGates)
                }

                if(r == -1 && q == 0){
                    placeGateWayTile(gameBoard,-3,r,6,players,sharedGates)
                }

                //TilePosition for a RouteTile
                else{
                    gameBoard[AxialPos(q-offset,r)] = RouteTile(null)
                }
            }

            if(border == 8){
                wasAtSymmetricLine = true
            }

            if(wasAtSymmetricLine){
                border--
            }

            else{
                border++
            }

            if(offset < 4){
                offset++
            }

        }

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
     * @param q The axial coordinate q where the tile is to be placed.
     * @param r The axial coordinate r where the tile is to be placed.
     */
    private fun placeTreasureTile(board : MutableMap<AxialPos, Tile>, q : Int, r : Int){
        val gemPositions = mutableMapOf<Int,Gem>()
        gemPositions[0] = Gem.AMBER
        board[AxialPos(q,r)] = TreasureTile( gemPositions, mutableListOf(Gem.AMBER))
    }


    /**
     * Places a gateway tile on the game board at the specified axial position (q, r).
     *
     * @param board The mutable map representing the game board.
     * @param q The axial coordinate q where the tile is to be placed.
     * @param r The axial coordinate r where the tile is to be placed.
     * @param gate determines where the gate is on the game-board
     * @param players The list of players in the game.
     * @param sharedGates indicating whether gates are shared among players.
     */
    private fun placeGateWayTile(board : MutableMap<AxialPos, Tile>, q : Int, r : Int,
                                 gate : Int, players : List<Player>, sharedGates: Boolean){

        //initialize gatePlayers with something, this gets overwritten in when block
        var gatePlayers = mutableListOf(players[0],players[1])

        when (players.size){
            2 -> {
                when(gate){

                    1 -> { gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED) }

                    2 -> { gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE) }

                    3 -> { gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED) }

                    4 -> { gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE) }

                    5 -> { gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED) }

                    6 -> { gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE) }
                }
            }

            3 -> {
                when(gate){

                    1 -> { gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED) }

                    2 -> {
                        gatePlayers = if(sharedGates){
                            searchPlayerWithColor(players,Color.RED,Color.BLUE)
                        } else{
                            searchPlayerWithColor(players,Color.BLUE,Color.BLUE)
                        }
                    }

                    3 -> { gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.WHITE) }

                    4 -> {
                        gatePlayers = if(sharedGates){
                            searchPlayerWithColor(players,Color.WHITE,Color.RED)
                        } else{
                            searchPlayerWithColor(players,Color.RED,Color.RED)
                        }
                    }

                    5 -> { gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE) }

                    6 -> {
                        gatePlayers = if(sharedGates){
                            searchPlayerWithColor(players,Color.BLUE,Color.WHITE)
                        } else{
                            searchPlayerWithColor(players,Color.WHITE,Color.WHITE)
                        }
                    }
                }
            }

            4 -> {
                when(gate){

                    1 -> { gatePlayers = searchPlayerWithColor(players,Color.RED,Color.BLUE) }

                    2 -> { gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.WHITE) }

                    3 -> { gatePlayers = searchPlayerWithColor(players,Color.RED,Color.PURPLE) }

                    4 -> { gatePlayers = searchPlayerWithColor(players,Color.PURPLE,Color.BLUE) }

                    5 -> { gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.RED) }

                    6 -> { gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.PURPLE) }
                }
            }
        }

        if(gatePlayers.first() == gatePlayers.last()){
            gatePlayers.removeLast()
        }

        board[AxialPos(q,r)] = GatewayTile(gatePlayers)
    }

    /**
     * Initializes the draw stack for the game, consisting of a shuffled collection of route tiles.
     *
     * @return ArrayDeque<Tile> representing the initialized draw stack.
     */
    private fun initializeDrawStack() : MutableList<Tile>{

        val drawStack = mutableListOf<Tile>()

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
    fun save(){
        val file = File("saveGame.ser")
        file.writeText(Json.encodeToString(rootService.currentGame))
    }

    /**
     * loads the current Game from the "saveGame.ser" file
     */
    fun load(){
        val file = File("saveGame.ser")
        rootService.currentGame = Json.decodeFromString<Game>(file.readText())
        onAllRefreshables { refreshAfterNewGame() }
    }
}