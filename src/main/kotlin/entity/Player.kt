package entity

/**
 * Entity to represent a player in the game
 * @property name to represent the name of the player
 * @property points to represent how many points does a player have
 * @property isAI if it is an AI or not
 * @property smartAI if the AI is smart or random
 * @property heldTile to represent the tile that the player will place
 * @property color to represent the color that the player chose
 * @property collectedGems to represent the collected gems of the player
 */
data class Player(val name: String, val color: Color, val isAI: Boolean, val smartAI: Boolean, var heldTile: Tile) {
    var points = 0
    val collectedGems = Gem.values().associateWith { 0 }.toMutableMap()
}
