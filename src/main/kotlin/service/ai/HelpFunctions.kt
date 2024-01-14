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
     * Get a list of empty coordinates on the game board.
     *
     * @return List of empty coordinates.
     */
    private fun getCurrentEmptyCoordinates(): MutableList<AxialPos> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet." }

        val emptyCoordinates = mutableListOf<AxialPos>()

        for (q in game.currentBoard.keys.minOfOrNull { it.q }?.rangeTo(game.currentBoard.keys.maxOfOrNull { it.q }!!)!!) {
            for (r in game.currentBoard.keys.minOfOrNull { it.r }!!..game.currentBoard.keys.maxOfOrNull { it.r }!!) {
                val coordinates = AxialPos(q, r)
                if (!game.currentBoard.containsKey(coordinates)) {
                    emptyCoordinates.add(coordinates)
                }
            }
        }

        return emptyCoordinates
    }

    /**
     * Finds and returns a list of valid positions on the game board.
     *
     * @return A mutable list of valid positions (AxialPos) on the board.
     */
    fun getCurrentValidCoordinates() :MutableList<AxialPos> {
        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet ;(." }
        val emptySpaces = rootService.helpFunctions.getCurrentEmptyCoordinates()
        val result : MutableList<AxialPos> = mutableListOf()
        emptySpaces.forEach {
                value -> if( rootService.playerService.checkPlacement(value)) {
            result.add(value)
                }
        }
        return result
    }

    /**
     * checks if there are more moves to play.
     *
     * @return true if there are moves let to play.
     */
    fun hasMovesLeft(): Boolean {
        return getCurrentValidCoordinates().isNotEmpty()
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

    fun hasWon(): Boolean {
        return false
    }


}