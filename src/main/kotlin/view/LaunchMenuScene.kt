package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.visual.ImageVisual

/**
 * The first scene seen when opening the game.
 * Possesses a [newGameButton] and a [loadGameButton]
 *
 */

class LaunchMenuScene : MenuScene(1920, 1080), Refreshable {

    // button to start a new game
    val newGameButton = Button(
        posX = 570, posY = 920,
        text = "New Game"
    ).apply {
        visual = ImageVisual("button.png")
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 3.0
    }

    //button to load a game
    val loadGameButton = Button(
        posX = 1250, posY = 920,
        text = "Load Game",
    ).apply {
        visual = ImageVisual("button.png")
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 3.0
    }

    init {
        background = ImageVisual("background.png")
        addComponents(
            loadGameButton,
            newGameButton
        )
    }

}
