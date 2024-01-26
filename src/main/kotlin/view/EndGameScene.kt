package view

import entity.Player
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import java.awt.Color


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

    private val p1Score = Label(
        width = (backgroundBox.posX + backgroundBox.width) - startButton.posX - 100,
        height = 150,
        posX = startButton.posX + 50,
        posY = 200,
        alignment = Alignment.CENTER_LEFT
    )
    private val p2Score = Label(
        width = (backgroundBox.posX + backgroundBox.width) - startButton.posX - 100,
        height = 150,
        posX = startButton.posX + 50,
        posY = 200 + 150 * 1,
        alignment = Alignment.CENTER_LEFT
    )
    private val p3Score = Label(
        width = (backgroundBox.posX + backgroundBox.width) - startButton.posX - 100,
        height = 150,
        posX = startButton.posX + 50,
        posY = 200 + 150 * 2,
        alignment = Alignment.CENTER_LEFT
    )
    private val p4Score = Label(
        width = (backgroundBox.posX + backgroundBox.width) - startButton.posX - 100,
        height = 150,
        posX = startButton.posX + 50,
        posY = 200 + 150 * 3,
        alignment = Alignment.CENTER_LEFT
    )
    private val scores = listOf(p1Score, p2Score, p3Score, p4Score)

    init {
        background = ImageVisual("background.png")
        opacity = 0.4

        for (score in scores) {
            score.isVisible = false
            score.font = Font(size = 35, color = Color.WHITE)
        }

        addComponents(
            backgroundBox,
            startButton,
            quitButton,
            p1Score,
            p2Score,
            p3Score,
            p4Score
        )

        onSceneHid = {
            for (score in scores) {
                score.isVisible = false
            }
        }
    }

    override fun refreshAfterEndGame(players: List<Player>) {
        val playerPoints = players.map {
            Triple(it, it.points, it.collectedGems.values.sum())
        }
        val sortedPlayers = playerPoints.sortedWith(
            compareBy<Triple<Player, Int, Int>>({ it.second }, { it.third }).reversed()
        )

        var rank = 1
        for ((i, currentPlayer) in sortedPlayers.withIndex()) {
            if (i > 0) {
                val previousPlayer = sortedPlayers[i - 1]

                if (previousPlayer.second != currentPlayer.second || previousPlayer.third != currentPlayer.third) {
                    rank += 1
                }
            }

            scores[i].apply {
                text = "$rank. ${currentPlayer.first.name} (${currentPlayer.second}P) (${currentPlayer.third}G)"
                isVisible = true
            }
        }
    }
}