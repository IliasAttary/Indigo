package entity

/**
 * This is an enum class that has the types and the paths of the tiles in map
 * @property path is a map that has the paths of a tile
 */
enum class TileType(val path: Map<Int, Int>) {
    TILE0(
        mapOf(
            0 to 2,
            1 to 4,
            2 to 0,
            3 to 5,
            4 to 1,
            5 to 3
        )
    ),
    TILE1(
        mapOf(
            0 to 3,
            1 to 4,
            2 to 5,
            3 to 0,
            4 to 1,
            5 to 2
        )
    ),
    TILE2(
        mapOf(
            0 to 5,
            1 to 4,
            2 to 3,
            3 to 2,
            4 to 1,
            5 to 0
        )
    ),
    TILE3(
        mapOf(
            0 to 5,
            1 to 3,
            2 to 4,
            3 to 1,
            4 to 2,
            5 to 0
        )
    ),
    TILE4(
        mapOf(
            0 to 5,
            1 to 2,
            2 to 1,
            3 to 4,
            4 to 3,
            5 to 0
        )
    )
}
