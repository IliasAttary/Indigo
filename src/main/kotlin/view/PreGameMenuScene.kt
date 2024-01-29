package view

import service.RootService
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
class PreGameMenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {
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
    private val gameModeSelector = ComboBox(
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
     *  Variable for saving the game mode
     */
    var gameMode = GameMode.LOCAL

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
    ).apply {
        isVisible = false
    }

    /**
     *  Text field for the player to enter their name.
     */
    var playerNameField = TextField(
        posX = (1920 - 300) / 2,
        posY = 520,
        width = 300,
        height = 40,
        font = Font(size = 20)
    ).apply {
        isDisabled = true
        isVisible = false
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
    ).apply {
        isVisible = false
    }

    /**
     *  Text field for the player to enter the GameID.
     */
    var gameIDField = TextField(
        posX = (1920 - 300) / 2,
        posY = 680,
        width = 300,
        height = 40,
        font = Font(size = 20)
    ).apply {
        isDisabled = true
        isVisible = false
        visual = ColorVisual.GRAY
    }

    /**
     * The player type when we join a network game
     */
    private var joinPlayerType = 0.also {
        rootService.networkService.useAI = false
        rootService.networkService.useSmartAI = false
    }

    /**
     * The button to switch the player type
     */
    private val joinPlayerTypeButton = Button(
        posX = 670,
        posY = 460,
        width = 100,
        height = 100,
        visual = ImageVisual("player_icon.png")
    ).apply {
        onMouseClicked = {
            joinPlayerType = (joinPlayerType + 1) % 3
            when (joinPlayerType) {
                0 -> {
                    rootService.networkService.useAI = false
                    rootService.networkService.useSmartAI = false
                    visual = ImageVisual("player_icon.png")
                }

                1 -> {
                    rootService.networkService.useAI = true
                    rootService.networkService.useSmartAI = false
                    visual = ImageVisual("random_ai_icon.png")
                }

                2 -> {
                    rootService.networkService.useAI = true
                    rootService.networkService.useSmartAI = true
                    visual = ImageVisual("smart_ai_icon.png")
                }
            }

            checkDisableStart()
        }
    }.apply {
        isDisabled = true
        isVisible = false
    }

    /**
     *  Drop down Menu to select the AI speed
     */
    private val joinAiSpeedSelector = ComboBox(
        width = 300 / 1.3,
        height = 40,
        posX = (1920 - 300) / 2 + (300 - 300 / 1.3) / 2,
        posY = 760,
        items = listOf("1000 ms", "3000 ms", "5000 ms", "7000 ms", "10000 ms"),
        prompt = "  Select AI Speed:",
        font = Font(size = 20, fontWeight = Font.FontWeight.SEMI_BOLD)
    ).apply {
        visual = ImageVisual("dropdown_background.png")
        scale = 1.3
        rootService.networkService.aiMoveMilliseconds = 10_000 // Set some default value
        selectedItemProperty.addListener { _, newValue ->
            if (newValue != null) {
                rootService.networkService.aiMoveMilliseconds = newValue.replace(Regex("\\D+"), "").toInt()
            }

            checkDisableStart()
        }
        isDisabled = true
        isVisible = false
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
            gameModeSelector,
            joinPlayerTypeButton,
            joinAiSpeedSelector,
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
                    enterNameLabel.isVisible = false
                    enterGameIDLabel.isVisible = false
                    playerNameField.isVisible = false
                    gameIDField.isVisible = false
                    joinPlayerTypeButton.apply {
                        isDisabled = true
                        isVisible = false
                    }
                    joinAiSpeedSelector.apply {
                        isDisabled = true
                        isVisible = false
                    }
                    playerNameField.visual = ColorVisual.GRAY
                    gameIDField.visual = ColorVisual.GRAY
                    startButton.isDisabled = false
                    gameMode = GameMode.LOCAL
                }

                "Host Network Game" -> {
                    playerNameField.text = ""
                    gameIDField.text = ""
                    enterNameLabel.font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
                    enterGameIDLabel.font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
                    playerNameField.isDisabled = false
                    gameIDField.isDisabled = false
                    enterNameLabel.isVisible = true
                    enterGameIDLabel.isVisible = true
                    playerNameField.isVisible = true
                    gameIDField.isVisible = true
                    joinPlayerTypeButton.apply {
                        isDisabled = true
                        isVisible = false
                    }
                    joinAiSpeedSelector.apply {
                        isDisabled = true
                        isVisible = false
                    }
                    playerNameField.visual = ColorVisual.WHITE
                    gameIDField.visual = ColorVisual.WHITE
                    startButton.isDisabled = true
                    gameMode = GameMode.HOST
                }

                "Join Network Game" -> {
                    enterNameLabel.font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
                    enterGameIDLabel.font = Font(size = 35, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
                    playerNameField.isDisabled = false
                    gameIDField.isDisabled = false
                    enterNameLabel.isVisible = true
                    enterGameIDLabel.isVisible = true
                    playerNameField.isVisible = true
                    gameIDField.isVisible = true
                    joinPlayerTypeButton.apply {
                        isDisabled = false
                        isVisible = true
                    }
                    joinAiSpeedSelector.apply {
                        isDisabled = false
                        isVisible = true
                    }
                    playerNameField.visual = ColorVisual.WHITE
                    gameIDField.visual = ColorVisual.WHITE
                    startButton.isDisabled = true
                    gameMode = GameMode.JOIN
                }
            }
        }

        playerNameField.onKeyTyped = {
            checkDisableStart()
        }

        gameIDField.onKeyTyped = {
            checkDisableStart()
        }

        onSceneShown = {
            // Reset AI in sceneShown as we still need those values after sceneHid
            rootService.networkService.useAI = false
            rootService.networkService.useSmartAI = false
        }

        // Reset all values and fields when the scene is hidden
        onSceneHid = {
            playerNameField.apply {
                text = ""
                isVisible = false
                isDisabled = true
            }

            enterNameLabel.isVisible = false
            enterGameIDLabel.isVisible = false

            gameIDField.apply {
                text = ""
                isVisible = false
                isDisabled = true
            }

            gameModeSelector.apply {
                selectedItem = null
            }

            joinPlayerType = 0
            joinPlayerTypeButton.apply {
                isVisible = false
                isDisabled = true
                visual = ImageVisual("player_icon.png")
            }

            joinAiSpeedSelector.apply {
                isVisible = false
                isDisabled = true
                selectedItem = null
            }

            gameMode = GameMode.LOCAL
        }

    }

    /**
     * Disables the start button if necessary
     */
    private fun checkDisableStart() {
        startButton.isDisabled = playerNameField.text.isBlank()
                || (gameMode == GameMode.HOST && gameIDField.text.isBlank())
                || (gameMode == GameMode.JOIN &&
                (gameIDField.text.isBlank() || (joinPlayerType != 0 && joinAiSpeedSelector.selectedItem == null)))
    }

}