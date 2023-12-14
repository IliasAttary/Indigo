package service
import entity.*

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
        for(i in 0 .. 5){
            allGems.add(Gem.AMBER)
            allGems.add(Gem.AMBER)
        }
        for(i in 0..4){
            allGems.add(Gem.EMERALD)
            allGems.add(Gem.EMERALD)
        }
        allGems.add(Gem.SAPPHIRE)
        allGems.add(Gem.SAPPHIRE)

        rootService.game = Game(currentPlayers = players,
            currentBoard = initializeBoard(players, sharedGates),
            currentDrawStack = initializeDrawStack(),
            aiMoveMilliseconds = aiSpeed,
            sharedGates = sharedGates,
            playerAtTurn = players.first(),
            currentGems = allGems)

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
                    val gemPositions = hashMapOf<Int,Gem>()
                    gemPositions[0] = Gem.AMBER
                    gameBoard[AxialPos(q,r)] = TreasureTile( gemPositions, mutableListOf(Gem.AMBER))
                }

                if(r == -4 && q == 4){
                    val gemPositions = hashMapOf<Int,Gem>()
                    gemPositions[0] = Gem.AMBER
                    gameBoard[AxialPos(q,r)] = TreasureTile( gemPositions, mutableListOf(Gem.AMBER))
                }

                //Middle TilePosition
                if(r == 0 && q == 4){
                    gameBoard[AxialPos(0,r)] = TreasureTile(null,gemsOnMiddleTreasureTile)
                }

                if(r == 0 && q == 0){
                    val gemPositions = hashMapOf<Int,Gem>()
                    gemPositions[0] = Gem.AMBER
                    gameBoard[AxialPos(-4,r)] = TreasureTile(gemPositions,mutableListOf(Gem.AMBER))
                }

                if(r == 0 && q == 8){
                    val gemPositions = hashMapOf<Int,Gem>()
                    gemPositions[0] = Gem.AMBER
                    gameBoard[AxialPos(q-4,r)] = TreasureTile(gemPositions,mutableListOf(Gem.AMBER))
                }

                if(r == 4 && q == 0){
                    val gemPositions = hashMapOf<Int,Gem>()
                    gemPositions[0] = Gem.AMBER
                    gameBoard[AxialPos(-4,r)] = TreasureTile(gemPositions,mutableListOf(Gem.AMBER))
                }

                if(r == 4 && q == 4){
                    val gemPositions = hashMapOf<Int,Gem>()
                    gemPositions[0] = Gem.AMBER
                    gameBoard[AxialPos(0,r)] = TreasureTile(gemPositions,mutableListOf(Gem.AMBER))
                }

                //TilePositions for a GatewayTile

                // Gates at 1
                if(r == -4 && 1 <= q && q <= 3){
                    when(players.size){
                        2 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(q,r)] = GatewayTile(gatePlayers)
                        }

                        3 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(q,r)] = GatewayTile(gatePlayers)
                        }
                    }


                }

                // Gates at 2
                if( (r == -3 && q == 5) || (r == -2 && q == 6) || (r == -1 && q == 7) ){
                    when (players.size){
                        2 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(4,r)] = GatewayTile(gatePlayers)
                        }

                        3 -> {
                            if(sharedGates){
                                val gatePlayers = searchPlayerWithColor(players,Color.RED,Color.BLUE)
                                gameBoard[AxialPos(4,r)] = GatewayTile(gatePlayers)
                            }
                            else{
                                val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE)
                                gatePlayers.removeLast()
                                gameBoard[AxialPos(4,r)] = GatewayTile(gatePlayers)
                            }
                        }

                        4 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.WHITE)
                            gameBoard[AxialPos(4,r)] = GatewayTile(gatePlayers)
                        }
                    }
                }

                // Gates at 3
                if( (r == 1 && q == 7) || (r == 2 && q == 6) || (r == 1 && q == 5) ){
                    when (players.size){
                        2 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(q-4,r)] = GatewayTile(gatePlayers)
                        }

                        3 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.WHITE)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(q-4,r)] = GatewayTile(gatePlayers)
                        }

                        4 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.RED,Color.PURPLE)
                            gameBoard[AxialPos(q-4,r)] = GatewayTile(gatePlayers)
                        }
                    }
                }

                // Gates at 4
                if(r == 4 && 1 <= q && q <= 3 ){
                    when (players.size){
                        2 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(q-4,r)] = GatewayTile(gatePlayers)
                        }

                        3 -> {
                            if(sharedGates){
                                val gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.RED)
                                gameBoard[AxialPos(q-4,r)] = GatewayTile(gatePlayers)
                            }
                            else{
                                val gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED)
                                gatePlayers.removeLast()
                                gameBoard[AxialPos(q-4,r)] = GatewayTile(gatePlayers)
                            }

                        }

                        4 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.PURPLE,Color.BLUE)
                            gameBoard[AxialPos(q-4,r)] = GatewayTile(gatePlayers)
                        }
                    }
                }

                //Gates at 5
                if(r == 1 && q == 0 || r == 2 && q == 0 || r == 3 && q == 0){
                    when (players.size){
                        2 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.RED,Color.RED)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(-4,r)] = GatewayTile(gatePlayers)
                        }

                        3 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(-4,r)] = GatewayTile(gatePlayers)
                        }

                        4 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.RED)
                            gameBoard[AxialPos(-4,r)] = GatewayTile(gatePlayers)
                        }
                    }
                }

                // Gates at 6
                if(r == -3 && q == 0){
                    when (players.size){
                        2 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(-1,r)] = GatewayTile(gatePlayers)
                        }

                        3 -> {
                            if(sharedGates){
                                val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.WHITE)
                                gameBoard[AxialPos(-1,r)] = GatewayTile(gatePlayers)
                            }
                            else{
                                val gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.WHITE)
                                gatePlayers.removeLast()
                                gameBoard[AxialPos(-1,r)] = GatewayTile(gatePlayers)
                            }
                        }

                        4 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.PURPLE)
                            gameBoard[AxialPos(-1,r)] = GatewayTile(gatePlayers)
                        }
                    }
                }

                if(r == -2 && q == 0){
                    when (players.size){
                        2 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(-2,r)] = GatewayTile(gatePlayers)
                        }

                        3 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.WHITE)
                            gameBoard[AxialPos(-2,r)] = GatewayTile(gatePlayers)
                        }

                        4 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.PURPLE)
                            gameBoard[AxialPos(-2,r)] = GatewayTile(gatePlayers)
                        }
                    }
                }

                if(r == -1 && q == 0){
                    when (players.size){
                        2 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.BLUE)
                            gatePlayers.removeLast()
                            gameBoard[AxialPos(-3,r)] = GatewayTile(gatePlayers)
                        }

                        3 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.BLUE,Color.WHITE)
                            gameBoard[AxialPos(-3,r)] = GatewayTile(gatePlayers)
                        }

                        4 -> {
                            val gatePlayers = searchPlayerWithColor(players,Color.WHITE,Color.PURPLE)
                            gameBoard[AxialPos(-3,r)] = GatewayTile(gatePlayers)
                        }
                    }
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
     * Initializes the draw stack for the game, consisting of a shuffled collection of route tiles.
     *
     * @return ArrayDeque<Tile> representing the initialized draw stack.
     */
    private fun initializeDrawStack() : MutableList<Tile>{

        val drawStack = mutableListOf<Tile>()

        for(i in 0 .. 13){
            drawStack.add(RouteTile(TileType.TILE0))
        }

        for(i in 0..5){
            drawStack.add(RouteTile(TileType.TILE1))
        }

        for(i in 0..13){
            drawStack.add(RouteTile(TileType.TILE2))
        }

        for(i in 0..13){
            drawStack.add(RouteTile(TileType.TILE3))
        }

        for(i in 0..5){
            drawStack.add(RouteTile(TileType.TILE4))
        }

        drawStack.shuffle()

        return drawStack
    }

    fun endGame(){
        val game = rootService.game
        checkNotNull(game){"No game started yet!"}

        val players = game.currentPlayers
        rootService.game = null

        onAllRefreshables{refreshAfterEndGame(players)}
    }
}