package service
import entity.*

/**
 * Service layer class which provides all the actions that the players can do.
 *
 * This class handles various player actions in the game.
 * It contains rotating the tile, placing a tile or doing a redo/undo action.
 *
 * @property rootService The reference to the root service, enabling communication with the core game entity.
 */
class PlayerService(private val rootService:RootService) : AbstractRefreshingService() {


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
    }
    /**
     * change the current player to the previous player if undo was called
     * @throws IllegalStateException if no game is started
     */
    private fun changePlayerBack(){
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}
        val playerAtTurn = game.playerAtTurn
        val currentPlayers = game.currentPlayers
        val indexOfCurrentPlayer = currentPlayers.indexOf(playerAtTurn)
        if(indexOfCurrentPlayer == 0){
            game.playerAtTurn = game.currentPlayers[currentPlayers.size-1]
        }
        else{
            game.playerAtTurn = game.currentPlayers[indexOfCurrentPlayer - 1]
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

        val redo = GameState(game.currentBoard,
                            game.currentDrawStack,
                            game.currentPlayers,
                            game.currentGems)

        game.redoStack.add(redo)
        val gameState = game.undoStack.removeLast()
        game.currentBoard = gameState.board
        game.currentDrawStack = gameState.drawStack
        game.currentPlayers = gameState.players
        changePlayerBack()
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
        val undo = GameState(game.currentBoard,
                            game.currentDrawStack,
                            game.currentPlayers,
                            game.currentGems)

        game.redoStack.add(undo)
        val gameState =  game.redoStack.removeLast()

        game.currentBoard = gameState.board
        game.currentDrawStack = gameState.drawStack
        game.currentPlayers = gameState.players
        changePlayer()
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
           game.currentBoard[AxialPos(q+1,r+1)],
           game.currentBoard[AxialPos(q,r+1)],
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
            pathsWithRotation.add(Pair(start + rotation % 6, end + rotation % 6))
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
    private fun checkPathsAtEdge(paths : MutableList<Pair<Int,Int>>, atGate : Int) : Boolean{
        when(atGate){
            1 -> {
                for(path in paths){
                    if(path.first == 0 && path.second == 1){
                        return false
                    }
                }
            }

            2 -> {
                for(path in paths){
                    if(path.first == 1 && path.second == 2){
                        return false
                    }
                }
            }

            3 -> {
                for(path in paths){
                    if(path.first == 2 && path.second == 3){
                        return false
                    }
                }
            }

            4 -> {
                for(path in paths){
                    if(path.first == 3 && path.second == 4){
                        return false
                    }
                }
            }

            5 -> {
                for(path in paths){
                    if(path.first == 4 && path.second == 5){
                        return false
                    }
                }
            }

            6 -> {
                for(path in paths){
                    if(path.first == 5 && path.second == 0){
                        return false
                    }
                }
            }

            else -> {return true}
        }

        return true
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

        // Create the current GameState
        val currentGameState =
            GameState(game.currentBoard, game.currentDrawStack, game.currentPlayers, game.currentGems)

        // Add the move to the undoStack
        game.undoStack.add(currentGameState)

        // Draw a tile
        drawTile()

        // Place tile
        game.currentBoard[coordinates] = tile

        // Move Gems
        moveGems(coordinates)

        // Refresh GUI
        onAllRefreshables { refreshAfterPlaceTile(coordinates) }

        // Swap current player
        changePlayer()
    }

    /**
     * Draws a tile for the current player from the draw stack and updates the player's held tile.
     *
     * If the draw stack is not empty, it removes the first tile and assigns it to the current player's held tile.
     * If the draw stack is empty, the held tile remains unchanged.
     */
    private fun drawTile(){
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

    fun moveGems(coordinates : AxialPos){
        TODO()
    }


}
