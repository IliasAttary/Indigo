package entity

/**
 * Entity to represent a player in the game
 * @property name to represent the name of the player
 * @property points to represent how many points does a player have
 * @property isLocalAI to choose if the AI local or not
 * @property smartAI to choose if the AI player has to be smart or random
 *@property heldTile to represent the tile that the player will place
 * @property color to represent the color that the player choosed
 * @property gems  to represent the gems of the player
 */
data class Player(val name:String,var heldTile:Tile , val color:Color,val gems:MutableList<Gem>){
    var points = 0
    var isLocalAI = false
    var smartAI  = false
}
