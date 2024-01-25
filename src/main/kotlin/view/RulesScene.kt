package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual

/**
 * A scene displaying the rules of the Indigo board game.
 *
 * This scene provides information about the rules of the game and
 * includes a return button to navigate back to the previous menu.
 *
 * @property returnButton The button used to return to the previous menu.
 */
class RulesScene : MenuScene(1000, 704), Refreshable {

        val returnButton = Button(
            width = 300,
            height = 150,
            posX = 730,
            posY = 560,
            text = "Return",
            font = Font(size = 40),
            visual = ImageVisual("button_background.png")
        ).apply {
            visual.borderRadius = BorderRadius(15)
            visual.backgroundRadius = BackgroundRadius(15)
            scale = 0.6
        }

        init {
            background = ImageVisual("Rules.png")
            opacity = 0.8
            addComponents(
                returnButton
            )

        }

    }