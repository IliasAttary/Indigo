package entity

/**
 * Entity that inherits from Tile to represent  a routeTile
 * @property gemPsitions map that has the positions of the Gems on the RouteTile
 * @property tileType the type of the tile with it's paths
 */
data class RouteTile(val gemPsitions:Map<Int,Gem>,val tileType:TileType):Tile()
