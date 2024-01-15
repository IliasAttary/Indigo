package entity

import kotlinx.serialization.Serializable

/**
 * Entity that inherits from Tile to represent a Treasure Tile
 * @property gemPositions map that has the positions of the gems, or null if it is the center treasure tile
 * @property gems the gems on the center treasure tile, or null if it is a border treasure tile
 */
@Serializable
data class TreasureTile(val gemPositions: MutableMap<Int, Gem>?, val gems: MutableList<Gem>?) : Tile()
