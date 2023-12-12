package entity

/**
 * Entity that inherits from Tile to represent  a TreasureTile
 * @property gemPsitions map that has the positions of the Gems on the TreasureTile
 * @property gems the gems on the TreasureTile
 */
data class TreasureTile(val gemPsitions:Map<Int,Gem>,val gems:MutableList<Gem>):Tile()
