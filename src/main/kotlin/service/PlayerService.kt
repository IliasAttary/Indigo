package service
import entity.*
import tools.aqua.bgw.core.BoardGameApplication
import kotlin.math.max


/**
 * Service layer class which provides all the actions that the players can do.
 *
 * This class handles various player actions in the game.
 * It contains rotating the tile, placing a tile or doing a redo/undo action.
 *
 * @property rootService The reference to the root service, enabling communication with the core game entity.
 */
class PlayerService(private val rootService:RootService) : AbstractRefreshingService() {

    private val neighborOffsetMap = mapOf(      0 to AxialPos(q=0,  r=-1),
                                        1 to AxialPos(q=1,  r=-1),
                                        2 to  AxialPos(q=1,  r=0),
                                        3 to  AxialPos(q=0, r=1),
                                        4 to  AxialPos(q=-1, r=1),
                                        5 to  AxialPos(q=-1,  r=0))

    private val treasureTilePaths = mapOf(
        1 to 5,
        5 to 1,
    )

    /**
     * The id of the current placeTileAi thread.
     * This variable is used to know whether it is an old placeTileAi thread.
     */
    private var currentPlaceTileAiThreadId: Long? = null

    /**
     * A lock for [currentPlaceTileAiThreadId] to prevent race conditions.
     */
    private val currentPlaceTileAiThreadIdLock = Any()

    /**
     * Rotates the tile held by the current player in the game.
     * The rotation is done in 60-degree increments.
     *
     * @throws IllegalStateException if no game is started
     */
    fun rotateTile(){
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}

        val heldTile = game.playerAtTurn.heldTile
        checkNotNull(heldTile){"There is no held tile."}

        if(heldTile.rotation == 5){
            heldTile.rotation = 0
        }
        else{
            heldTile.rotation += 1
        }


        onAllRefreshables { refreshAfterRotateTile() }
    }
    /**
     * change the current player to the player who has the turn to play
     * @throws IllegalStateException if no game is started
     */
    private fun changePlayer(){
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}

        val playerAtTurn = game.playerAtTurn
        val currentPlayers = game.currentPlayers
        val indexOfCurrentPlayer = currentPlayers.indexOf(playerAtTurn)
        if(indexOfCurrentPlayer == currentPlayers.size-1){
            game.playerAtTurn = game.currentPlayers[0]
        }
        else{
            game.playerAtTurn = game.currentPlayers[indexOfCurrentPlayer + 1]
        }

        onAllRefreshables { refreshAfterChangePlayer() }

        if (game.playerAtTurn.isAI){
            placeTileAi()
        }
    }

    /**
     * Places a tile on the game board using the AI.
     *
     * This function is responsible for triggering an AI move, whether it is a smart AI move using Monte Carlo
     * or a random move. It runs the AI move calculation in a separate thread.
     * After calculating the move, it sleeps for a specific duration to control the pace of the AI's actions.
     *
     */
    fun placeTileAi() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet" }

        Thread {
            val timeStart = System.currentTimeMillis()
            val (aiMove, tileRotation) = if (game.playerAtTurn.smartAI) {
                val trainMonti = rootService.aiServices.trainMontiCarloAgent(30, 10)
                Pair(trainMonti.first, trainMonti.second.rotation)
            } else {
                val randomMove = rootService.aiServices.playRandomly()
                Pair(randomMove.first, randomMove.second.rotation)
            }

            val timeEnd = System.currentTimeMillis()
            val diffMillis = timeEnd - timeStart
            val restMillis = max(0, game.aiMoveMilliseconds - diffMillis)
            Thread.sleep(restMillis)

            val threadId = Thread.currentThread().id

            BoardGameApplication.runOnGUIThread {
                synchronized(currentPlaceTileAiThreadIdLock) {
                    if (currentPlaceTileAiThreadId == threadId) {
                        currentPlaceTileAiThreadId = null
                    } else {
                        return@runOnGUIThread
                    }
                }

                game.playerAtTurn.heldTile!!.rotation = tileRotation
                placeTile(aiMove)
            }
        }.apply { isDaemon = true }.also {
            synchronized(currentPlaceTileAiThreadIdLock) {
                currentPlaceTileAiThreadId = it.id
            }
        }.start()
    }

    /**
     * Aborts old placeTileAi threads, if they exist
     */
    fun abortPlaceTileAi() {
        synchronized(currentPlaceTileAiThreadIdLock) {
            currentPlaceTileAiThreadId = null
        }
    }

    /**
     * undo enables the player to go back to the last step
     * @throws IllegalStateException if no game is started or if the list is empty.
     */
    fun undo(){
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}

        require(game.undoStack.isNotEmpty()){ "The undo list is empty" }

        val redo = rootService.gameService.cloneGameState()
        game.redoStack.add(redo)

        val gameState = game.undoStack.removeLast()
        game.currentBoard = gameState.board
        game.currentDrawStack = gameState.drawStack
        game.currentPlayers = gameState.players
        game.playerAtTurn = gameState.playerAtTurn
        game.currentGems = gameState.gems

        if(game.playerAtTurn.isAI){
            placeTileAi()
        } else {
            abortPlaceTileAi()
        }

        onAllRefreshables { refreshAfterUndo() }
    }


    /**
     * redo enables the player to go to the next step
     *  @throws IllegalStateException if no game is started or if the list is empty.
     */
    fun redo(){
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}
        require(game.redoStack.isNotEmpty()){
            "The redo list is empty"
        }

        val undo = rootService.gameService.cloneGameState()
        game.undoStack.add(undo)

        val gameState = game.redoStack.removeLast()
        game.currentBoard = gameState.board
        game.currentDrawStack = gameState.drawStack
        game.currentPlayers = gameState.players
        game.playerAtTurn = gameState.playerAtTurn
        game.currentGems = gameState.gems

        if(game.playerAtTurn.isAI){
            placeTileAi()
        } else {
            abortPlaceTileAi()
        }

        onAllRefreshables { refreshAfterRedo() }
    }

    /**
     * Checks whether placing a tile at the specified coordinates is valid.
     *
     * @param coordinates The axial position where the player intends to place the tile.
     * @return `true` if the placement is valid, `false` otherwise.
     */
    fun checkPlacement(coordinates : AxialPos) : Boolean{
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}

        //check if there is already a Tile at this coordinates
        if(coordinates in game.currentBoard){
            return false
        }

        val heldTile = game.playerAtTurn.heldTile
        requireNotNull(heldTile){"There is no held tile."}

        val typeOfTile = heldTile.tileType
        val rotation = heldTile.rotation

        //if RouteTile has no curves, it can't block two exits
        if(typeOfTile == TileType.TILE0 || typeOfTile == TileType.TILE1){
            return true
        }

        // get the neighbours of the tile
        val q = coordinates.q
        val r = coordinates.r

        val neighbours = listOf(
           game.currentBoard[AxialPos(q+1,r)],
           game.currentBoard[AxialPos(q+1,r-1)],
           game.currentBoard[AxialPos(q,r-1)],
           game.currentBoard[AxialPos(q-1,r)],
           game.currentBoard[AxialPos(q-1,r+1)],
           game.currentBoard[AxialPos(q,r+1)]
        )

        // this variable indicates at which gate the tile should get placed
        // if it is 0 after the for loop the tile is not at the edge of the game-board and can be placed.
        var atGate = 0

        for(neighbour in neighbours){
            if(neighbour == null){
                continue
            }
            else if(neighbour is GatewayTile){
               atGate = neighbour.gate
            }
        }

        // Retrieve the paths of the tile and consider its rotation
        val pathsWithRotation = mutableListOf<Pair<Int, Int>>()

        typeOfTile.paths.forEach{ (start, end) ->
            pathsWithRotation.add(Pair( (start + rotation) % 6, (end + rotation) % 6))
        }

        return checkPathsAtEdge(pathsWithRotation,atGate)
    }

    /**
     * Checks if a given list of paths contains specific paths based on the provided gate number.
     *
     * @param paths List of pairs representing paths of a tile.
     * @param atGate The gate number to determine which specific paths to check.
     * @return Returns true if the paths at the specified gate is not in the list, otherwise false.
     */
    private fun checkPathsAtEdge(paths: MutableList<Pair<Int, Int>>, atGate: Int): Boolean {
        if (atGate == 0) {
            return true
        }

        val edgeConditions = listOf(
            0 to 1,
            1 to 2,
            2 to 3,
            3 to 4,
            4 to 5,
            5 to 0
        )

        return paths.none { path ->
            path.first == edgeConditions[atGate - 1].first && path.second == edgeConditions[atGate - 1].second
        }
    }


    /**
     * Places a tile on the game board at the specified coordinates.
     *
     * @param coordinates The axial position where the player intends to place the tile.
     * @throws IllegalArgumentException if the position is already occupied or the tile is blocking two exits.
     * @throws IllegalStateException if the current player has no tile.
     */
    fun placeTile(coordinates: AxialPos) {
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        // Checks if position is valid
        require(checkPlacement(coordinates)) {
            "The position is already occupied or the tile is blocking two exits"
        }

        // Checks if the player has a tile to place
        val tile = game.playerAtTurn.heldTile
        requireNotNull(tile) { "The current player has no tile" }

        // Clone the current game state, and add it to the undo stack
        val clonedGameState = rootService.gameService.cloneGameState()
        game.undoStack.add(clonedGameState)
        game.redoStack.clear() // clear redoStack, as we might have diverged

        // Draw a tile
        drawTile()

        // Place tile
        game.currentBoard[coordinates] = tile

        // If network game, notify others of placed tile
        if (rootService.networkService.connectionState == ConnectionState.PLAYING_MY_TURN) {
            rootService.networkService.sendPlacedTile(coordinates)
        }

        // Move Gems
        moveGems(coordinates)

        // Refresh GUI
        onAllRefreshables { refreshAfterPlaceTile(coordinates) }

        if (game.currentGems.isEmpty()) {
            rootService.gameService.endGame()
        } else {
            // Swap current player
            changePlayer()
        }
    }

    /**
     * Draws a tile for the current player from the draw stack and updates the player's held tile.
     *
     * If the draw stack is not empty, it removes the first tile and assigns it to the current player's held tile.
     * If the draw stack is empty, the held tile remains unchanged.
     */
     fun drawTile(){
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        val drawStack = game.currentDrawStack

        // Checks if the drawStack is not empty
        if(drawStack.isNotEmpty()){
            val drawnTile = drawStack.removeLast()
            game.playerAtTurn.heldTile = drawnTile
        }
        else{
            game.playerAtTurn.heldTile = null
        }
    }

    /**
     * this returns the paths of a tile with considering the rotation
     */
    fun tilePathsWithRotation(paths: Map<Int, Int>, rotation: Int) :Map<Int, Int> {
        return paths
            .map { (start, end )-> ((start + rotation) % 6) to ((end + rotation) % 6) } // take tile rotation into account
            .toMap()
    }

    /**
     * Moves gems on the game board tiles according to defined paths.
     * This function handles the movement of gems from one tile to another, considering
     * different types of tiles (RouteTile, TreasureTile, GatewayTile) and their properties.
     * It also updates the points and collected gems for the players.
     *
     * @param coordinates The axial coordinates of the current tile on the game board. This is the position
     *                    of the tile where the gems are to be moved.
     * @throws IllegalStateException if an unexpected tile is encountered or if an invalid operation
     *                               is performed on the gems.
     * @throws IllegalArgumentException if the placed tile is null or not an instance of RouteTile.
     * @throws NullPointerException if no game has been started yet.
     */
    fun moveGems(coordinates : AxialPos){
        // retrieve current game from root service
        val game = rootService.currentGame
        checkNotNull(game){ "No game started yet." }

        // get the current placed tile
        val placedTile = game.currentBoard[coordinates]
        var currentTile = game.currentBoard[coordinates]
        var currentTilePos = coordinates

        require(placedTile != null && placedTile is RouteTile ){"placedTile is null or not RouteTile"}

        val placedTilePaths = tilePathsWithRotation(placedTile.tileType.paths, placedTile.rotation)
        // the positions of the gems that have been moved to the placed tile
        val newGemPositions = calculateNewGemPositions(placedTile,placedTilePaths,coordinates)

        // move gems to their final destination

        for (newGemPosition in newGemPositions){
            currentTile = placedTile
            currentTilePos = coordinates
            var currentGemPos = newGemPosition

            if (!placedTile.gemPositions.containsKey(newGemPosition)) {
                // gem got already removed due to collision, ignore
                continue
            }

            while(true){
                val nextTilePos = currentTilePos + neighborOffsetMap[currentGemPos]!!
                val nextTile = game.currentBoard[nextTilePos]
                // if the next tile is not existent, we can stop
                if (nextTile == null) {
                    break
                }

                //val gem = currentTile.gemPositions.remove(currentGemPos)
                val gem = if (currentTile is RouteTile) {
                    currentTile.gemPositions.remove(currentGemPos)!!
                } else if (currentTile is TreasureTile) {
                    currentTile.gemPositions!!.remove(currentGemPos)!!
                } else {
                    throw IllegalStateException ("unexpected")
                }


                val nextGemPosition = if (nextTile is GatewayTile){
                    for (owner in nextTile.ownedBy) {
                        owner.collectedGems[gem] = owner.collectedGems[gem] !! + 1
                        owner.points += gem.points

                    }
                    game.currentGems.remove(gem)
                    break
                }
                    else{
                    val nextTileRawPaths = if (nextTile is RouteTile) {
                        nextTile.tileType.paths
                    } else {
                        treasureTilePaths
                    }
                    val nextTilePaths = tilePathsWithRotation(nextTileRawPaths, nextTile.rotation)
                    val oppositeDirection = (currentGemPos + 3) % 6
                    val endPosition = nextTilePaths[oppositeDirection]!!

                    if (nextTile is RouteTile) {
                        if (nextTile.gemPositions.containsKey(oppositeDirection)) {
                            // collision, remove both gems
                            game.currentGems.remove(nextTile.gemPositions.remove(oppositeDirection)!!)
                            game.currentGems.remove(gem)
                            break
                        } else {
                            nextTile.gemPositions[endPosition] = gem
                        }
                    } else if (nextTile is TreasureTile) {
                        if (nextTile.gemPositions!!.containsKey(oppositeDirection)) {
                            // collision, remove both gems
                            game.currentGems.remove(nextTile.gemPositions.remove(oppositeDirection)!!)
                            game.currentGems.remove(gem)
                            break
                        } else {
                            nextTile.gemPositions[endPosition] = gem
                        }
                    } else {
                        throw IllegalStateException ("unexpected")
                    }
                    endPosition
                }
                currentTile = nextTile
                currentTilePos = nextTilePos
                currentGemPos = nextGemPosition

            }
        }
    }
    private fun calculateNewGemPositions(placedTile:RouteTile,
                                 placedTilePaths:Map<Int,Int> ,
                                 coordinates : AxialPos):MutableList<Int>{
        val game = rootService.currentGame
        checkNotNull(game)
        val newGemPositions = mutableListOf<Int>()

        for ((direction, neighborOffset) in neighborOffsetMap){
            val neighborPos = coordinates + neighborOffset
            val neighbor = game.currentBoard[neighborPos]

            if (neighbor == null ) {
                continue
            }

            val oppositeDirection = (direction + 3) % 6

            val gem = if (neighbor is RouteTile) {
                neighbor.gemPositions.remove(oppositeDirection)
            } else if (neighbor is TreasureTile) {
                if (neighbor.gems != null) {
                    neighbor.gems.removeLastOrNull()
                } else {
                    neighbor.gemPositions?.remove(oppositeDirection)
                }
            } else {
                continue
            }

            if (gem == null) {
                continue
            }

            // check for collision on path
            if (placedTile.gemPositions.containsKey(direction)) {
                // remove other gem
                newGemPositions.remove(direction)
                game.currentGems.remove(placedTile.gemPositions.remove(direction)!!)
                game.currentGems.remove(gem)
                continue
            }

            // move gem over to the current tile, to the end of the path
            val endPosition = placedTilePaths[direction]
            if(endPosition != null) {
                placedTile.gemPositions[endPosition] = gem
                newGemPositions.add(endPosition)
            }
        }

        return newGemPositions
    }
}
