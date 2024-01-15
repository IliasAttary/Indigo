package view

import service.RootService
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.TextVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color

/**
 * Main Game Scene for Indigo.
 */
class MainGameScene(private val rootService: RootService) : BoardGameScene(2160, 1080), Refreshable {

    //Button to undo action
    val undoButton = Button(
        width = 300,
        height = 150,
        posX = (2160 - 350),
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
        posX = (2160 - 350),
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
        posX =  (2160 - 350),
        posY = (1080 - 150) / 2,
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
        posX = (2160 - 350),
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
        posX = (2160 - 350),
        posY = 900,
        text = "Quit",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 0.9
    }

    val rightTurnButton = Button(
        width = 150,
        height = 150,
        posX = 300,
        posY = 900,
        visual = ImageVisual("arrow-right.png")
    ).apply {
    }

    val leftTurnButton = Button(
        width = 150,
        height = 150,
        posX = 100,
        posY = 900,
        visual = ImageVisual("arrow-left.png")
    ).apply {
    }

   val gameBoard: HexagonGrid<HexagonView> = HexagonGrid<HexagonView>(
       posX = 2160/2,
       posY = 460,
       coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL
   ).apply {

       // Radius of axial hexagon grid
       val size = 4

       for (q in -size..size) {
           for (r in -size..size) {
               if (q + r <= size && q + r >= -size) {
                   val hexagon = HexagonView(
                       size = 70,
                       visual = CompoundVisual(
                           ColorVisual(Color(235, 230, 188)),
                           TextVisual(
                               text = "($q, $r)",
                               font = Font(15.0, fontStyle = Font.FontStyle.ITALIC, color = Color.WHITE)
                           )
                       )
                   )
                   this[q, r] = hexagon
               }
           }
       }
   }

    // background image with black overlay
    val blackOverlay = ColorVisual(color = Color.black).apply { transparency = 0.7 }
    val backgroundOverlay = CompoundVisual(children = listOf(ImageVisual("background.png"), blackOverlay))


    init {
        background = backgroundOverlay

        addComponents(
            gameBoard,
            undoButton,
            redoButton,
            rulesButton,
            saveButton,
            quitButton,
            rightTurnButton,
            leftTurnButton,

        )
    }
}