package view

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

        // show the mainGameScene in the background and open the preGameMenuScene
        this.showMenuScene(preGameMenuScene)

        preGameMenuScene.startButton.onMouseClicked = {
            this.showMenuScene(newGameMenuScene)
        }
        newGameMenuScene.returnButton.onMouseClicked = { this.showMenuScene(preGameMenuScene) }
        newGameMenuScene.startRoundButton.onMouseClicked = {
            this.hideMenuScene()
            this.showGameScene(mainGameScene)
        }
    }


}