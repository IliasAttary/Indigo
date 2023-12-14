package entity

import kotlinx.serialization.Serializable

/**
 * This a class that represents the position of a tile in the board game
 * @param q q-coordinate
 * @param r r-coordinate
 */
@Serializable
data class AxialPos(val q: Int, val r: Int)
