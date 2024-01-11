package view

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * The class which handles all the scenes of the application window
 */

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
            this.showGameScene(mainGameScene)
        }
        launchMenuScene.newGameButton.onMouseClicked = { this.showMenuScene(preGameMenuScene)}
        launchMenuScene.loadGameButton.onMouseClicked = {
            this.hideMenuScene()
            this.showGameScene(mainGameScene)}

        mainGameScene.rulesButton.onMouseClicked = {this.showMenuScene(rules)}    }
    }



}