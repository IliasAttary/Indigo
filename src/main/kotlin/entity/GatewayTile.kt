package entity

import kotlinx.serialization.Serializable

/**
 * Entity to represent the gate of a player or the two players if the gate is shared
 * @property ownedBy the players who own the gate
 */

@Serializable
data class GatewayTile(val ownedBy: List<Player>, val gate: Int) : Tile()
