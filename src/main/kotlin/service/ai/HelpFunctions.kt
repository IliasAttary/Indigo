package service.ai

import entity.*
import service.*

/**
 * Utility functions class that provides various helper functions for the game,
 * particularly related to game actions, placements, and validations.
 *
 * @property rootService The root service managing the game state and related services.
 */
class HelpFunctions(private val rootService: RootService) : AbstractRefreshingService()  {

    /**
     * Plays an action at the specified coordinates on the game board.
     *
     * @param coordinates The axial position where the action should be performed.
     */
    fun playAction(coordinates: AxialPos) {
        // Checks if a game is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        // Checks if position is valid
        require(rootService.playerService.checkPlacement(coordinates)) {
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
        rootService.playerService

        // Place tile
        game.currentBoard[coordinates] = tile

        // Move Gems
        rootService.playerService.moveGems(coordinates)

        // Refresh GUI
        onAllRefreshables { refreshAfterPlaceTile(coordinates) }
        // return new gme state
    }


    /**
     * Draws a tile for the current player from the draw stack.
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
     * Finds all available positions on the game board that are not yet occupied.
     *
     * @param gameBoard The current state of the game board.
     * @return A list of empty axial positions.
     */
    private fun findEmptyPositions(gameBoard: MutableMap<AxialPos, Tile>): MutableList<AxialPos> {

        val game = rootService.currentGame

        checkNotNull(game) { "No game started yet ;(." }

        val emptyPositions = mutableListOf<AxialPos>()

        // Define the range of axial coordinates you want to check
        val minQ = -4
        val maxQ = 4
        val minR = -4
        val maxR = 4

        // Iterate over all possible positions within the specified range
        for (q in minQ..maxQ) {
            for (r in minR..maxR) {
                val currentPos = AxialPos(q, r)
                if (!gameBoard.containsKey(currentPos)) {
                    emptyPositions.add(currentPos)
                }
            }

            return emptyPositions
        }



        /**
         * Checks if a tile placement at a specified coordinates is valid.
         *
         * @param coordinates The axial position to check for tile placement validity.
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

        return emptyPositions
    }
    /**
     * Checks if a tile placement at a specified coordinates is valid.
     *
     * @param coordinates The axial position to check for tile placement validity.
     * @return `true` if the placement is valid, `false` otherwise.
     */
    private fun checkPlacement(coordinates : AxialPos) : Boolean{
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
     * Checks if the paths of a tile, considering their rotation, would block exits
     * when placed at a specific gate on the game board.
     *
     * @param paths The paths of the tile with their rotation.
     * @param atGate The gate at which the tile should be placed.
     * @return `true` if the paths do not block exits, `false` otherwise.
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
     * Retrieves the current player from the game.
     *
     * @return The current player.
     * @throws IllegalStateException if no game is started.
     */
    fun getCurrentPlayer() : Player {
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}
        return game.playerAtTurn
    }

    /**
     * Finds and returns a list of valid positions on the game board.
     *
     * @param board The mutable map representing the game board.
     * @return A mutable list of valid positions (AxialPos) on the board.
     */
    fun findCurrentValidPositions(board: MutableMap<AxialPos, Tile>) :MutableList<AxialPos> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet ;(." }
        val emptySpaces = rootService.helpFunctions.findEmptyPositions(game.currentBoard)
        val result : MutableList<AxialPos> = mutableListOf()
        emptySpaces.forEach {
                value -> if( rootService.helpFunctions.checkPlacement(value)) {
            result.add(value)
        }
        }
        return result
    }





    /**
     * Checks if the game has reached a terminal state based on the provided list of valid positions.
     *
     * @param validPositionsList The list of valid positions on the game board.
     * @return True if there are no valid positions left, indicating a terminal state; false otherwise.
     */
    fun isTerminal(validPositionsList: MutableList<AxialPos>): Boolean {
        // Check if the size of the valid positions list is zero
        // If there are no valid positions left, the game is in a terminal state
        return validPositionsList.size == 0

        // If there are still valid positions, the game is not in a terminal state
    }


}