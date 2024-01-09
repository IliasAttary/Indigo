package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color

/**
 * Main Game Scene for Indigo.
 */
class MainGameScene(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable {

    //Button to undo action
    val undoButton = Button(
        width = 300,
        height = 150,
        posX = (1920 - 350),
        posY = 40,
        text = "Undo",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 0.9
    }

    //Button to redo action
    val redoButton = Button(
        width = 300,
        height = 150,
        posX = (1920 - 350),
        posY = 45 + 150,
        text = "Redo",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 0.9
    }

    //Button to see the rules
    val rulesButton = Button(
        width = 300,
        height = 150,
        posX =  (1920 - 350),
        posY = (1080-150)/2,
        text = "Rules",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 0.9
    }

    // Button to save the game
    val saveButton = Button(
        width = 300,
        height = 150,
        posX = (1920 - 350),
        posY = 900 - 155,
        text = "Save",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 0.9
    }

    //Button to quit the game
    val quitButton = Button(
        width = 300,
        height = 150,
        posX = (1920 - 350),
        posY = 900,
        text = "Quit",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 0.9
    }

    // background image with black overlay
    val blackOverlay = ColorVisual(color = Color.black).apply { transparency = 0.7 }
    val backgroundOverlay = CompoundVisual(children = listOf( ImageVisual("background.png"),blackOverlay))


    init {
        background = backgroundOverlay

        addComponents(
            undoButton,
            redoButton,
            rulesButton,
            saveButton,
            quitButton
        )
    }
}