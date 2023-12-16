package view

import tools.aqua.bgw.core.BoardGameApplication
import service.RootService

/**
 * Implementation of the BGW [BoardGameApplication] for the boardgame "Indigo"
 */

class IndigoApplication : BoardGameApplication(), Refreshable {

    // Central service from which all others are created/accessed
    // also holds the currently active game
    private val rootService = RootService()

    //Scenes

    // first menu Scene seen when opening the Game
    private val launchScene = LaunchMenuScene(rootService).apply {
        newGameButton.onMouseClicked = {
            this@IndigoApplication.showMenuScene(preGameScene)
        }
        loadGameButton.onMouseClicked = {
            this@IndigoApplication.showGameScene(gameScene)
        }
    }

    // scene for choosing Game Style
    private val preGameScene = PreGameMenuScene(rootService)

    // Scene for configuring Players
    private val newGameScene = NewGameMenuScene(rootService)

    // the board game scene
    private val gameScene = MainGameScene(rootService)

    // scene that shows after a finished game
    private val endGameScene = EndGameMenuScene(rootService)


    init {

        // all scenes and the application itself need to
        // react to changes done in the service layer
        rootService.addRefreshables(
            this,
            launchScene,
            preGameScene,
            newGameScene,
            gameScene,
            endGameScene
        )
        this.showMenuScene(launchScene)
    }
}


