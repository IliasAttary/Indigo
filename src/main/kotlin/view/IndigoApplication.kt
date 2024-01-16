package view

import entity.Color
import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

class IndigoApplication : BoardGameApplication("Indigo"), Refreshable {

    /**
     * rootService for accessing the current game and other logic.
     */
    private val rootService = RootService()

    /**
     *  Main scene where the game is played.
     */
    private val mainGameScene = MainGameScene(rootService)

    /**
     * Menu scene that gets displayed right after launching the application.
     */
    private val launchMenuScene = LaunchMenuScene()

    /**
     * Menu Scene that shows the rules
     */
    private val rules = RulesScene()

    /**
     * Menu Scene that gets displayed after the player clicks on New Game.
     */
    private val preGameMenuScene = PreGameMenuScene()

    /**
     * Menu scene where the players can enter their name and configure the game mode, as well as AI Style.
     */
    private val newGameMenuScene = NewGameMenuScene()

    /**
     * Menu scene where the players see their points and who won the game.
     */
    private val endGameMenuScene = EndGameScene()

    init {
        rootService.addRefreshables(
            this,
            launchMenuScene,
            preGameMenuScene,
            newGameMenuScene,
            mainGameScene,
            endGameMenuScene
        )

        // open the launchMenuScene
        this.showMenuScene(launchMenuScene)


        preGameMenuScene.startButton.onMouseClicked = {
            this.showMenuScene(newGameMenuScene)
        }
        newGameMenuScene.returnButton.onMouseClicked = { this.showMenuScene(preGameMenuScene) }
        newGameMenuScene.startRoundButton.onMouseClicked = {
            this.hideMenuScene()

            // Find the correct player values and start a new game
            var isAI = false
            var smartAI = false
            val playerList = mutableListOf<Player>()
            var color = Color.WHITE
            val aiSpeed = newGameMenuScene.aiSpeed
            val sharedGates = newGameMenuScene.sharedGates
            for (i in 0 until newGameMenuScene.playerCount) {
                if (newGameMenuScene.actualPlayerTypes[i] == "random") {
                    isAI = true
                    smartAI = false
                }
                if (newGameMenuScene.actualPlayerTypes[i] == "smart") {
                    isAI = true
                    smartAI = true
                }
                when (newGameMenuScene.actualPlayerColors[i]) {
                    "white" -> color = Color.WHITE
                    "red" -> color = Color.RED
                    "blue" -> color = Color.BLUE
                    "purple" -> color = Color.PURPLE
                }

                playerList.add(
                    Player(
                        newGameMenuScene.actualPlayerNames[i],
                        color,
                        isAI,
                        smartAI,
                        heldTile = null
                    )
                )
            }
            rootService.gameService.startGame(playerList, aiSpeed, sharedGates = sharedGates)
            this.showGameScene(mainGameScene)
        }
        launchMenuScene.newGameButton.onMouseClicked = { this.showMenuScene(preGameMenuScene) }
        launchMenuScene.loadGameButton.onMouseClicked = {
            this.hideMenuScene()
            this.showGameScene(mainGameScene)
        }

        mainGameScene.rulesButton.onMouseClicked = { this.showMenuScene(rules) }
        mainGameScene.quitButton.onMouseClicked = { exit() }
    }
}
