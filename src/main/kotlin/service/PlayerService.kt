package service

import entity.GameState

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
        heldTile.rotation += 60

        onAllRefreshables { refreshAfterRotateTile() }
    }
    /**
     * change the current player to the player who has the turn to play
     * @throws IllegalStateException if no game is started
     */
    private fun changePlayer(){
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}
        val playerAtturn = game.playerAtTurn
        val currentPlayers = game.currentPlayers
        val indexOfCurrentPlayer = currentPlayers.indexOf(playerAtturn)
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
        val playerAtturn = game.playerAtTurn
        val currentPlayers = game.currentPlayers
        val indexOfCurrentPlayer = currentPlayers.indexOf(playerAtturn)
        if(indexOfCurrentPlayer == 0){
            game.playerAtTurn = game.currentPlayers[currentPlayers.size-1]
        }
        else{
            game.playerAtTurn = game.currentPlayers[indexOfCurrentPlayer - 1]
        }
    }
    /**
     * undo enables the player to go back to the last step
     * @throws IllegalStateException if no game is started or if the list is empty
     */
    fun undo(){
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}
        require(game.undoStack.isNotEmpty()){
            "The undo list is empty"
        }
        val redo = GameState(game.currentBoard,
                            game.currentDrawStack,
                            game.currentPlayers,
                            game.currentGems)

        game.redoStack.add(redo)
        val gameState = game.undoStack.last()
        game.currentBoard = gameState.board
        game.currentDrawStack = gameState.drawStack
        game.currentPlayers = gameState.players
        changePlayerBack()
        game.undoStack.removeLast()
        onAllRefreshables { refreshAfterUndo() }
    }

    /**
     * redo enables the player to go to the next step
     *  @throws IllegalStateException if no game is started or if the list is empty
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

        val gameState = game.redoStack.last()

        game.currentBoard = gameState.board
        game.currentDrawStack = gameState.drawStack
        game.currentPlayers = gameState.players
        changePlayer()
        game.redoStack.removeLast()
        onAllRefreshables { refreshAfterRedo() }

    }
}
