package view

import entity.Player
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.components.uicomponents.Label



/**
 * End Game Scene for Indigo.
 */
class EndGameScene : MenuScene(1920, 1080), Refreshable {

    /**
     * Disabled Button to have a slightly opaque black box as an additional background.
     */
    private val backgroundBox = Button(
        posX = 300,
        posY = 0,
        width = 1320,
        height = 1080,
        visual = ImageVisual("blackbox_background.png"),
    ).apply {
        isDisabled = true
        opacity = 0.8
    }

    /**
     * Button to start another Game
     */
    val startButton = Button(
        width = 400,
        height = 150,
        posX = (1920 / 2) - 400,
        posY = 900,
        text = "Start New Game",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
    }

    /**
     * Button to close the application
     */
    val quitButton = Button(
        width = 300,
        height = 150,
        posX = (1920 / 2) + 100,
        posY = 900,
        text = "Quit",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
    }

    val score = mutableListOf<String>()
    val p1Score = Label(width = 400, height = 80, posX = 1080/2, posY = 1920/2)
    val p2Score = Label(width = 400, height = 80, posX = 1080/2, posY = 1920/3)
    val p3Score = Label(width = 400, height = 200, posX = 1080/2, posY = 1920/4)
    val p4Score = Label(width = 400, height = 100, posX = 1080/2, posY = 1920/5)

    init {
        background = ImageVisual("background.png")
        opacity = 0.4

        addComponents(
            backgroundBox,
            startButton,
            quitButton,
            p1Score,
            p2Score,
            p3Score,
            p4Score
        )
    }

    override fun refreshAfterEndGame(players: List<Player>) {
        players.sortedBy {it.points}
        for (i in 0..players.size)
            score.add("${players[i]} ${players[i].points} PT")

        p1Score.text = score[0]
        p2Score.text = score[1]
        p3Score.text = score[2]

        if (players.size > 3) {
            p4Score.text = score[3]
        }
    }
}