package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * PreGameMenuScene where the player can choose to play local or online and enter a name and GameID.
 */
class PreGameMenuScene : MenuScene(1920, 1080), Refreshable {
    val startButton = Button(
        width = 300,
        height = 150,
        posX = (1920 - 300) / 2,
        posY = 900,
        text = "Start",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        isDisabled = true
    }

    /**
     *  Label telling the player to select a game mode from the dropdown menu below.
     */
    private var gameModeLabel = Label(
        posX = (1920 - 500) / 2,
        posY = 200,
        width = 500,
        height = 40,
        text = "Choose a Game Mode!",
        font = Font(size = 40, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     *  Drop down Menu to select which game mode to play.
     */
    private val gameModeSelector = ComboBox<String>(
        width = 280,
        height = 40,
        posX = (1920 - 280) / 2,
        posY = 300,
        items = listOf("Local Game", "Host Network Game", "Join Network Game"),
        prompt = "  Game modes:",
        font = Font(size = 20, fontWeight = Font.FontWeight.SEMI_BOLD)
    ).apply {
        visual = ImageVisual("dropdown_background.png")
        scale = 1.3
    }


    /**
     *  Label telling the player to enter their name for the game they want to join.
     */
    private var enterNameLabel = Label(
        posX = (1920 - 400) / 2,
        posY = 440,
        width = 400,
        height = 40,
        text = "Enter your Name:",
        font = Font(size = 35, color = Color.GRAY, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     *  Text field for the player to enter their name.
     */
    private var playerNameField = TextField(
        posX = (1920 - 300) / 2,
        posY = 520,
        width = 300,
        height = 40,
        font = Font(size = 20)
    ).apply {
        isDisabled = true
        visual = ColorVisual.GRAY
    }

    /**
     *  Label telling the player to enter the GameID for the game they want to join.
     */
    private var enterGameIDLabel = Label(
        posX = (1920 - 400) / 2,
        posY = 600,
        width = 400,
        height = 40,
        text = "Enter the Game ID:",
        font = Font(size = 35, color = Color.GRAY, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     *  Text field for the player to enter the GameID.
     */
    private var gameIDField = TextField(
        posX = (1920 - 300) / 2,
        posY = 680,
        width = 300,
        height = 40,
        font = Font(size = 20)
    ).apply {
        isDisabled = true
        visual = ColorVisual.GRAY
    }

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

    init {
        addComponents(
            backgroundBox,
            gameModeLabel,
            enterNameLabel,
            enterGameIDLabel,
            playerNameField,
            gameIDField,
            startButton,
            gameModeSelector
        )
        background = ImageVisual("background.png")
        opacity = 0.4

        /* Adds a listener to the gameModeSelector dropdown menu which disables the Input fields for player name and
        *  game ID if the selected game mode is not join network game.
        */
        gameModeSelector.selectedItemProperty.addListener { _, value ->
            when (value) {
                "Local Game" -> {
                    playerNameField.text = ""
                    gameIDField.text = ""
                    enterNameLabel.font = Font(size = 35, color = Color.GRAY, fontWeight = Font.FontWeight.BOLD)
                    enterGameIDLabel.font = Font(size = 35, color = Color.GRAY, fontWeight = Font.FontWeight.BOLD)
                    playerNameField.isDisabled = true
                    gameIDField.isDisabled = true
                    playerNameField.visual = ColorVisual.GRAY
                    gameIDField.visual = ColorVisual.GRAY
                    startButton.isDisabled = false
                }

                "Host Network Game" -> {
                    playerNameField.text = ""
                    gameIDField.text = ""
                    enterNameLabel.font = Font(size = 35, color = Color.GRAY, fontWeight = Font.FontWeight.BOLD)
                    enterGameIDLabel.font = Font(size = 35, color = Color.GRAY, fontWeight = Font.FontWeight.BOLD)
                    playerNameField.isDisabled = true
                    gameIDField.isDisabled = true
                    playerNameField.visual = ColorVisual.GRAY
                    gameIDField.visual = ColorVisual.GRAY
                    startButton.isDisabled = false
                }

                "Join Network Game" -> {
                    enterNameLabel.font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
                    enterGameIDLabel.font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
                    playerNameField.isDisabled = false
                    gameIDField.isDisabled = false
                    playerNameField.visual = ColorVisual.WHITE
                    gameIDField.visual = ColorVisual.WHITE
                    startButton.isDisabled = true
                }
            }
        }

        playerNameField.onKeyTyped = {
            startButton.isDisabled = playerNameField.text.isBlank() || gameIDField.text.isBlank()
        }

        gameIDField.onKeyTyped = {
            startButton.isDisabled = playerNameField.text.isBlank() || gameIDField.text.isBlank()
        }
    }

}