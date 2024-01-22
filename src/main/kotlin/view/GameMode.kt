package view

/**
 * Enum to describe in whether we are playing a local game,
 * host the game, or joined someone's other game.
 */
enum class GameMode {
    /**
     * Local game
     */
    LOCAL,

    /**
     * We host the game
     */
    HOST,

    /**
     * We joined someone's game
     */
    JOIN,
}
