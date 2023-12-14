package entity
import kotlinx.serialization.Serializable
/**
 * Entity to represent the tile
 */
@Serializable
sealed class Tile {
    /**
     * clockwise tile rotation in steps of 60°
     */
    var rotation = 0
}
