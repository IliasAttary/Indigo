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

        if(heldTile.rotation == 300){
            heldTile.rotation = 0
        }
        else{
            heldTile.rotation += 60
        }


        onAllRefreshables { refreshAfterRotateTile() }
    }

    fun checkPlacement(coordinates : AxialPos) : Boolean{
        val game = rootService.currentGame
        checkNotNull(game){"No game started yet"}

        var tileHasCurve = false

        val heldTile = game.playerAtTurn.heldTile
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

    private fun checkRotation(tileIsAtGate : Int, typeOfTile : TileType, rotation : Int) : Boolean{
        when(tileIsAtGate){
            1 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 120 || rotation == 300){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 300){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 60 || rotation == 180 || rotation == 300){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            2 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 0 || rotation == 180){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 0){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 0 || rotation == 120 || rotation == 240){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            3 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 60 || rotation == 240){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 60){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 60 || rotation == 180 || rotation == 300){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            4 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 120 || rotation == 300){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 120){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 0 || rotation == 120 || rotation == 240){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            5 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 0 || rotation == 180){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 180){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 60 || rotation == 180 || rotation == 300){
                            return false
                        }
                    }

                    else -> { return true }
                }
            }

            6 -> {
                when(typeOfTile){
                    TileType.TILE2 -> {
                        if(rotation == 60 || rotation == 240){
                            return false
                        }
                    }

                    TileType.TILE3 -> {
                        if(rotation == 240){
                            return false
                        }
                    }

                    TileType.TILE4 -> {
                        if(rotation == 0 || rotation == 120 || rotation == 240){
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
