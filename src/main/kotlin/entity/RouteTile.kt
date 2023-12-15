package entity
import kotlinx.serialization.Serializable

/**
 * Entity that inherits from Tile to represent a Route Tile
 * @property gemPositions map that has the positions of the gems on the Route Tile
 * @property tileType the type of the tile with its paths
 */
@Serializable
data class RouteTile(val tileType: TileType) : Tile() {
    val gemPositions: MutableMap<Int, Gem> = mutableMapOf()
}
