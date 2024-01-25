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
        }

        repeat(5){
            allGems.add(Gem.EMERALD)
        }

        allGems.add(Gem.SAPPHIRE)

        rootService.currentGame = Game(
            currentPlayers = players,
            currentBoard = initializeBoard(players, sharedGates),
            currentDrawStack = drawStack,
            aiMoveMilliseconds = aiSpeed,
            sharedGates = sharedGates,
            playerAtTurn = players.first(),
            currentGems = allGems)

        // give every player a tile to start with
        for(player in players){
            player.heldTile = drawStack.removeLast()
        }

        //refresh view layer
        onAllRefreshables { refreshAfterNewGame() }

        val game = rootService.currentGame
        checkNotNull(game){"No game started yet!"}

        if (game.playerAtTurn.isAI){
            rootService.playerService.placeTileAi()
        }
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
    ){
        val gatePlayers = determineGatePlayers(gate, players, sharedGates)
        board[coordinates] = GatewayTile(gatePlayers, gate)
    }

    /**
     * Determines the players associated with a specified gate in the game.
     *
     * The function considers the number of players and the gate configuration to determine
     * the players associated with the given gate. If sharedGates is true, multiple players
     * may be associated with a single gate.
     *
     * @param gate The gate number for which players need to be determined.
     * @param players The list of players in the game.
     * @param sharedGates A boolean indicating whether gates can be shared among multiple players.
     * @return A mutable list of players associated with the specified gate.
     */
    private fun determineGatePlayers(gate: Int, players: List<Player>, sharedGates: Boolean): MutableList<Player> {

        val gatePlayers = mutableListOf<Player>()

        when(players.size){
            2 -> {

                if(gate % 2 == 1){
                   gatePlayers.add(players[0])
                }
                else{
                    gatePlayers.add(players[1])
                }
            }

            3 -> {
                when(gate){

                    1 -> gatePlayers.add(players[0])

                    2 -> {
                        if(sharedGates){
                            gatePlayers.add(players[0])
                            gatePlayers.add(players[1])
                        }
                        else{
                            gatePlayers.add(players[1])
                        }
                    }

                    3 -> gatePlayers.add(players[2])

                    4 -> {
                        if(sharedGates){
                            gatePlayers.add(players[2])
                            gatePlayers.add(players[0])
                        }
                        else{
                            gatePlayers.add(players[0])
                        }
                    }

                    5 -> gatePlayers.add(players[1])

                    6 -> {
                        if(sharedGates){
                            gatePlayers.add(players[1])
                            gatePlayers.add(players[2])
                        }
                        else{
                            gatePlayers.add(players[2])
                        }
                    }
                }
            }

            4 -> {
                when(gate){
                    1 -> {
                        gatePlayers.add(players[0])
                        gatePlayers.add(players[1])
                    }

                    2 -> {
                        gatePlayers.add(players[1])
                        gatePlayers.add(players[2])
                    }

                    3 -> {
                        gatePlayers.add(players[0])
                        gatePlayers.add(players[3])
                    }

                    4 -> {
                        gatePlayers.add(players[3])
                        gatePlayers.add(players[1])
                    }

                    5 -> {
                        gatePlayers.add(players[2])
                        gatePlayers.add(players[0])
                    }

                    6 -> {
                        gatePlayers.add(players[2])
                        gatePlayers.add(players[3])
                    }
                }
            }
        }

        return gatePlayers
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

        if (rootService.networkService.connectionState != ConnectionState.DISCONNECTED) {
            rootService.networkService.disconnect()
        }

        onAllRefreshables{refreshAfterEndGame(players)}
    }

    /**
     * saves the current Game in the file "saveGame.ser"
     */
    fun save() {
        val file = File("saveGame.ser")
        file.writeText(json.encodeToString(rootService.currentGame))
    }

    /**
     * loads the current Game from the "saveGame.ser" file
     */
    fun load() {
        val file = File("saveGame.ser")
        val game = json.decodeFromString<Game>(file.readText())
        fixPlayerReferences(game)
        rootService.currentGame = game

        if(game.playerAtTurn.isAI){
            rootService.playerService.placeTileAi()
        } else {
            rootService.playerService.abortPlaceTileAi()
        }

        onAllRefreshables { refreshAfterLoadGame() }
    }

    /**
     * Creates a clone of the current game state.
     *
     * @return cloned game state
     *
     * @throws IllegalStateException if no game has started yet
     */
    fun cloneGameState(): GameState {
        val game = rootService.currentGame

        checkNotNull(game) {
            "No game started yet."
        }

        val clonedGame = json.decodeFromString<Game>(json.encodeToString(game))
        fixPlayerReferences(clonedGame)

        return GameState(
            board = clonedGame.currentBoard,
            drawStack = clonedGame.currentDrawStack,
            players = clonedGame.currentPlayers,
            playerAtTurn = clonedGame.playerAtTurn,
            gems = clonedGame.currentGems,
        )
    }

    companion object {
        /**
         * The json instance to encode/decode the Game object
         */
        private val json = Json {
            allowStructuredMapKeys = true
        }

        /**
         * Fix the player references, so they are all the same instances for the same player name.
         *
         * @param game the game to fix
         */
        private fun fixPlayerReferences(game: Game) {
            game.playerAtTurn = game.currentPlayers.first { originalPlayer ->
                originalPlayer.name == game.playerAtTurn.name
            }

            for ((pos, tile) in game.currentBoard) {
                if (tile !is GatewayTile) {
                    continue
                }

                val originalOwners = tile.ownedBy.map { clonedPlayer ->
                    game.currentPlayers.first { originalPlayer -> originalPlayer.name == clonedPlayer.name }
                }

                game.currentBoard[pos] = GatewayTile(originalOwners, tile.gate)
            }
        }
    }
}