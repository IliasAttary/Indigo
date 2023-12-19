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

        //check if there is already a Tile at the coordinates
        if(coordinates in game.currentBoard){
            return false
        }

        var tileHasCurve = false

        val heldTile = game.playerAtTurn.heldTile

        requireNotNull(heldTile){"There is no held tile."}

        val typeOfTile = heldTile.tileType
        val rotation = heldTile.rotation

        if(typeOfTile == TileType.TILE2 || typeOfTile == TileType.TILE3 || typeOfTile == TileType.TILE4 ){
            tileHasCurve = true
        }

        val q = coordinates.q
        val r = coordinates.r
        val isTileAtGate = tileAtGate(q,r)

        if(isTileAtGate.first && tileHasCurve) {
            val tileIsAtGate = isTileAtGate.second
            return checkRotation(tileIsAtGate, typeOfTile, rotation)
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
    fun placeTile(coordinates: AxialPos){
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        require(checkPlacement(coordinates)) { "The position is already occupied or the tile is blocking two exits" }

        val tile = game.playerAtTurn.heldTile
        requireNotNull(tile) { "The current player has no tile" }

        // Draw a tile
        drawTile()

        // Place tile
        game.currentBoard[coordinates] = tile

        //Move Gems
        moveGems(coordinates)

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

    /**
     * Checks if a tile is placed at a gateway location and returns a Pair indicating whether it is at a gateway
     * and the gateway number if applicable.
     *
     * @param q The axial q-coordinate of the tile.
     * @param r The axial r-coordinate of the tile.
     * @return A Pair where the first element is a Boolean indicating whether the tile is at a gateway location,
     *         and the second element is an Int representing the gateway number.
     */
    private fun tileAtGate(q : Int, r : Int) : Pair<Boolean,Int> {

        var atGate = 0
        var coordinatesAtAGate = false

        // Tile gets placed at 1 ?
        if(r == -4 && 1 <= q && q <= 3){
            coordinatesAtAGate = true
            atGate = 1
        }

        // Tile gets placed at 2 ?
        if( (r == -3 || r == -2 || r == -1) && q == 4){
            coordinatesAtAGate = true
            atGate = 2
        }

        // Tile gets placed at 3 ?
        if( r == 1 && q == 3 || r == 2 && q == 2 || r == 3 && q == 1){
            coordinatesAtAGate = true
            atGate = 3
        }

        // Tile gets placed at 4 ?
        if( (q == -3 || q == -2 || q == -1) && r == 4){
            coordinatesAtAGate = true
            atGate = 4
        }

        //Tile gets placed at 5 ?
        if( (r == 1 || r == 2 || r == 3) && q == -4){
            coordinatesAtAGate = true
            atGate = 5
        }

        //Tile gets placed at 6 ?
        if( r == -1 && q == -3 || r == -2 && q == -3 || r == -3 && q == -1){
            coordinatesAtAGate = true
            atGate = 6
        }

        return Pair(coordinatesAtAGate, atGate)
    }

    /**
     * Checks if the rotation of a tile at a gateway location is valid based on the tile type and rotation angle.
     *
     * @param tileIsAtGate An Int representing the gateway number (1 to 6) where the tile is placed.
     * @param typeOfTile The type of the tile (TILE2, TILE3, TILE4).
     * @param rotation The rotation angle of the tile. (1 -> 60°, 2 -> 120° ...)
     * @return Boolean indicating whether the rotation is valid for the given tile type and gateway location.
     */
    private fun checkRotation(tileIsAtGate : Int, typeOfTile : TileType, rotation : Int) : Boolean{
        when(tileIsAtGate){
            1 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 2 || rotation == 5){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 5){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 1 || rotation == 3 || rotation == 5){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            2 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 0 || rotation == 3){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 0){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 0 || rotation == 2 || rotation == 4){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            3 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 1 || rotation == 4){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 1){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 1 || rotation == 3 || rotation == 5){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            4 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 2 || rotation == 5){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 2){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 0 || rotation == 2 || rotation == 4){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            5 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 0 || rotation == 3){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 3){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 1 || rotation == 3 || rotation == 5){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            6 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 1 || rotation == 4){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 4){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 0 || rotation == 2 || rotation == 4){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }
        }

        return true
    }

}
