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
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 *  Menu scene from where a game can be created.
 */
class NewGameMenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    /**
     * The game mode
     */
    var gameMode: GameMode = GameMode.LOCAL

    /**
     *  Contains the absolute Field positions for the first to fourth player name fields.
     *  Is never modified
     */
    private val absolutePlayerFieldPos = listOf(250, 350, 450, 550)

    /**
     *  Contains the Field positions for the player name fields up to playerCount.
     *  Is used to randomize player order.
     */
    private var randomizedPlayerFieldPos = listOf<Int>()

    /**
     *  Variable saving the player count.
     */
    var playerCount = 2

    /**
     *  Saves the available player types for cycling the type buttons.
     *  The Player Types are as follows:
     *  0 = Player,
     *  1 = Random AI,
     *  2 = Smart AI.
     */
    private val availableTypes = mutableListOf(0, 0, 0, 0)

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
     * Tells the Host to set up their players.
     */
    private val setupPlayersLabel = Label(
        posX = (1920 - 500) / 2,
        posY = 100,
        width = 500,
        height = 50,
        text = "Pick your Players!",
        font = Font(size = 40, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     *  Tells the host to set the player types.
     */
    private val playerTypeLabel = Label(
        posX = setupPlayersLabel.posX - 500,
        posY = 100,
        width = 500,
        height = 50,
        text = "Type",
        font = Font(size = 40, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     *  Tells the host to set the player colors.
     */
    private val playerColorLabel = Label(
        posX = setupPlayersLabel.posX + 490,
        posY = 100,
        width = 400,
        height = 50,
        text = "Color",
        font = Font(size = 40, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     *  Switches the player type for first player in the following order:
     *  "Player" -> "Random AI" -> "Smart AI" -> "Player"
     */
    private val firstPlayerTypeButton = Button(
        posX = (1920 - 400) / 2 - 350,
        posY = absolutePlayerFieldPos[0] - 30,
        width = 100,
        height = 100,
        visual = ImageVisual("player_icon.png")
    ).apply {
        onMouseClicked = {
            availableTypes[0] = (availableTypes[0] + 1) % 3
            when (availableTypes[0]) {
                0 -> {
                    visual = ImageVisual("player_icon.png")
                    this.name = "player"
                }

                1 -> {
                    visual = ImageVisual("random_ai_icon.png")
                    this.name = "random"
                }

                2 -> {
                    visual = ImageVisual("smart_ai_icon.png")
                    this.name = "smart"
                }
            }
            determineActualValues()
            checkInputs()
        }
    }

    /**
     *  Indicates the third players name.
     */
    private val firstPlayerLabel = Label(
        posX = (1920 - 400) / 2 - 250,
        posY = absolutePlayerFieldPos[0] - 5,
        width = 250,
        height = 40,
        text = "1. Player:",
        font = Font(size = 40, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     *  Input field for the first player to enter their name.
     */
    private val firstPlayerNameField = TextField(
        posX = (1920 - 400) / 2,
        posY = absolutePlayerFieldPos[0],
        width = 400,
        height = 40,
        font = Font(size = 20)
    ).apply {
        onKeyTyped = {
            startRoundButton.isDisabled = text.isBlank()
            determineActualValues()
        }
    }

    /**
     *  white color for the first player.
     */
    private val firstPlayerWhiteColor = Button(
        posX = playerColorLabel.posX + 50,
        posY = firstPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
    ).apply {
        name = "white"
    }

    /**
     *  red color for the first player.
     */
    private val firstPlayerRedColor = Button(
        posX = firstPlayerWhiteColor.posX + 80,
        posY = firstPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_red.png"),
    ).apply {
        name = "red"
    }

    /**
     *  blue color for the first player.
     */
    private val firstPlayerBlueColor = Button(
        posX = firstPlayerRedColor.posX + 80,
        posY = firstPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_blue.png"),
    ).apply {
        name = "blue"
    }

    /**
     *  purple color for the first player.
     */
    private val firstPlayerPurpleColor = Button(
        posX = firstPlayerBlueColor.posX + 80,
        posY = firstPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_purple.png"),
    ).apply {
        name = "purple"
    }

    /**
     *  Switches the player type for first player in the following order:
     *  "Player" -> "Random AI" -> "Smart AI" -> "Player"
     */
    private val secondPlayerTypeButton = Button(
        posX = (1920 - 400) / 2 - 350,
        posY = absolutePlayerFieldPos[1] - 30,
        width = 100,
        height = 100,
        visual = ImageVisual("player_icon.png")
    ).apply {
        onMouseClicked = {
            availableTypes[1] = (availableTypes[1] + 1) % 3
            when (availableTypes[1]) {
                0 -> {
                    visual = ImageVisual("player_icon.png")
                    this.name = "player"
                }

                1 -> {
                    visual = ImageVisual("random_ai_icon.png")
                    this.name = "random"
                }

                2 -> {
                    visual = ImageVisual("smart_ai_icon.png")
                    this.name = "smart"
                }
            }
            determineActualValues()
            checkInputs()
        }
    }

    /**
     *  Indicates the third players name.
     */
    private val secondPlayerLabel = Label(
        posX = (1920 - 400) / 2 - 250,
        posY = absolutePlayerFieldPos[1] - 5,
        width = 250,
        height = 40,
        text = "2. Player:",
        font = Font(size = 40, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    )

    /**
     *  Input field for the second player to enter their name.
     */
    private val secondPlayerNameField = TextField(
        posX = (1920 - 400) / 2,
        posY = absolutePlayerFieldPos[1],
        width = 400,
        height = 40,
        font = Font(size = 20)
    ).apply {
        onKeyTyped = {
            startRoundButton.isDisabled = text.isBlank()
            determineActualValues()
        }
    }

    /**
     *  white color for the second player.
     */
    private val secondPlayerWhiteColor = Button(
        posX = playerColorLabel.posX + 50,
        posY = secondPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
    ).apply {
        name = "white"
    }

    /**
     *  red color for the second player.
     */
    private val secondPlayerRedColor = Button(
        posX = firstPlayerWhiteColor.posX + 80,
        posY = secondPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_red.png"),
    ).apply {
        name = "red"
    }

    /**
     *  blue color for the second player.
     */
    private val secondPlayerBlueColor = Button(
        posX = firstPlayerRedColor.posX + 80,
        posY = secondPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_blue.png"),
    ).apply {
        name = "blue"
    }

    /**
     *  purple color for the second player.
     */
    private val secondPlayerPurpleColor = Button(
        posX = firstPlayerBlueColor.posX + 80,
        posY = secondPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_purple.png"),
    ).apply {
        name = "purple"
    }

    /**
     *  Switches the player type for first player in the following order:
     *  "Player" -> "Random AI" -> "Smart AI" -> "Player"
     */
    private val thirdPlayerTypeButton = Button(
        posX = (1920 - 400) / 2 - 350,
        posY = absolutePlayerFieldPos[2] - 30,
        width = 100,
        height = 100,
        visual = ImageVisual("player_icon.png")
    ).apply {
        isVisible = false
        isDisabled = true
        onMouseClicked = {
            availableTypes[2] = (availableTypes[2] + 1) % 3
            when (availableTypes[2]) {
                0 -> {
                    visual = ImageVisual("player_icon.png")
                    this.name = "player"
                }

                1 -> {
                    visual = ImageVisual("random_ai_icon.png")
                    this.name = "random"
                }

                2 -> {
                    visual = ImageVisual("smart_ai_icon.png")
                    this.name = "smart"
                }
            }
            determineActualValues()
            checkInputs()
        }
    }

    /**
     *  Indicates the third players name.
     */
    private val thirdPlayerLabel = Label(
        posX = (1920 - 400) / 2 - 250,
        posY = absolutePlayerFieldPos[2] - 5,
        width = 250,
        height = 40,
        text = "3. Player:",
        font = Font(size = 40, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        isVisible = false
    }

    /**
     *  Input field for the third player to enter their name.
     */
    private val thirdPlayerNameField = TextField(
        posX = (1920 - 400) / 2,
        posY = absolutePlayerFieldPos[2],
        width = 400,
        height = 40,
        font = Font(size = 20)
    ).apply {
        isDisabled = true
        isVisible = false
        onKeyTyped = {
            startRoundButton.isDisabled = text.isBlank()
            determineActualValues()
        }
    }

    /**
     *  white color for the third player.
     */
    private val thirdPlayerWhiteColor = Button(
        posX = playerColorLabel.posX + 50,
        posY = thirdPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
    ).apply {
        name = "white"
        isVisible = false
        isDisabled = true
    }

    /**
     *  red color for the third player.
     */
    private val thirdPlayerRedColor = Button(
        posX = firstPlayerWhiteColor.posX + 80,
        posY = thirdPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_red.png"),
    ).apply {
        name = "red"
        isVisible = false
        isDisabled = true
    }

    /**
     *  blue color for the third player.
     */
    private val thirdPlayerBlueColor = Button(
        posX = firstPlayerRedColor.posX + 80,
        posY = thirdPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_blue.png"),
    ).apply {
        name = "blue"
        isVisible = false
        isDisabled = true
    }

    /**
     *  purple color for the third player.
     */
    private val thirdPlayerPurpleColor = Button(
        posX = firstPlayerBlueColor.posX + 80,
        posY = thirdPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_purple.png"),
    ).apply {
        name = "purple"
        isVisible = false
        isDisabled = true
    }

    /**
     *  Adds a third Player.
     */
    private val addThirdPlayerButton = Button(
        posX = thirdPlayerNameField.posX + 190,
        posY = absolutePlayerFieldPos[2],
        width = 50,
        height = 50,
        visual = ImageVisual("add_player.png")
    )

    /**
     *  Removes the third Player.
     */
    private val removeThirdPlayerButton = Button(
        posX = thirdPlayerNameField.posX + 420,
        posY = absolutePlayerFieldPos[2] - 5,
        width = 50,
        height = 50,
        visual = ImageVisual("remove_player.png")
    ).apply {
        isVisible = false
        isDisabled = true
    }

    /**
     *  Switches the player type for first player in the following order:
     *  "Player" -> "Random AI" -> "Smart AI" -> "Player"
     */
    private val fourthPlayerTypeButton = Button(
        posX = (1920 - 400) / 2 - 350,
        posY = absolutePlayerFieldPos[3] - 30,
        width = 100,
        height = 100,
        visual = ImageVisual("player_icon.png")
    ).apply {
        isVisible = false
        isDisabled = true
        name = "player"
        onMouseClicked = {
            // Cycle the player type
            availableTypes[3] = (availableTypes[3] + 1) % 3
            when (availableTypes[3]) {
                0 -> {
                    visual = ImageVisual("player_icon.png")
                    this.name = "player"
                }

                1 -> {
                    visual = ImageVisual("random_ai_icon.png")
                    this.name = "random"
                }

                2 -> {
                    visual = ImageVisual("smart_ai_icon.png")
                    this.name = "smart"
                }
            }
            determineActualValues()
            checkInputs()
        }
    }

    /**
     *  Indicates the third players name.
     */
    private val fourthPlayerLabel = Label(
        posX = (1920 - 400) / 2 - 250,
        posY = absolutePlayerFieldPos[3] - 5,
        width = 250,
        height = 40,
        text = "4. Player:",
        font = Font(size = 40, color = Color.WHITE, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        isVisible = false
    }

    /**
     *  Input field for the fourth player to enter their name.
     */
    private val fourthPlayerNameField = TextField(
        posX = (1920 - 400) / 2,
        posY = absolutePlayerFieldPos[3],
        width = 400,
        height = 40,
        font = Font(size = 20)
    ).apply {
        isDisabled = true
        isVisible = false
        onKeyTyped = {
            startRoundButton.isDisabled = text.isBlank()
            determineActualValues()
        }
    }

    /**
     *  white color for the fourth player.
     */
    private val fourthPlayerWhiteColor = Button(
        posX = playerColorLabel.posX + 50,
        posY = fourthPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
    ).apply {
        name = "white"
        isVisible = false
        isDisabled = true
    }

    /**
     *  red color for the fourth player.
     */
    private val fourthPlayerRedColor = Button(
        posX = firstPlayerWhiteColor.posX + 80,
        posY = fourthPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_red.png"),
    ).apply {
        name = "red"
        isVisible = false
        isDisabled = true
    }

    /**
     *  blue color for the fourth player.
     */
    private val fourthPlayerBlueColor = Button(
        posX = firstPlayerRedColor.posX + 80,
        posY = fourthPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_blue.png"),
    ).apply {
        name = "blue"
        isVisible = false
        isDisabled = true
    }

    /**
     *  purple color for the fourth player.
     */
    private val fourthPlayerPurpleColor = Button(
        posX = firstPlayerBlueColor.posX + 80,
        posY = fourthPlayerNameField.posY - 10,
        width = 60,
        height = 60,
        visual = ImageVisual("color_purple.png")
    ).apply {
        name = "purple"
        isVisible = false
        isDisabled = true
    }

    /**
     *  Adds a fourth Player.
     */
    private val addFourthPlayerButton = Button(
        posX = fourthPlayerNameField.posX + 190,
        posY = absolutePlayerFieldPos[3],
        width = 50,
        height = 50,
        visual = ImageVisual("add_player.png")
    ).apply {
        isVisible = false
        isDisabled = true
    }

    /**
     *  Removes the fourth player
     */
    private val removeFourthPlayerButton = Button(
        posX = fourthPlayerNameField.posX + 420,
        posY = absolutePlayerFieldPos[3] - 5,
        width = 50,
        height = 50,
        visual = ImageVisual("remove_player.png")
    ).apply {
        isVisible = false
        isDisabled = true
    }

    /**
     *  Returns to the preGameMenuScene when pressed.
     */
    val returnButton = Button(
        posX = 1300,
        posY = 910,
        width = 300,
        height = 150,
        text = "Return",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
    }

    /**
     *  Starts a round of Indigo with the specified Players.
     */
    val startRoundButton = Button(
        posX = (1920 - 300) / 2,
        posY = 910,
        width = 300,
        height = 150,
        text = "Start Round",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        isDisabled = true
    }

    /**
     *  Drop down Menu to select which game mode to play.
     */
    private val gameModeSelector = ComboBox(
        width = 260,
        height = 40,
        posX = firstPlayerLabel.posX - 60,
        posY = fourthPlayerNameField.posY + 120,
        items = listOf("Shared Gates", "Solo"),
        prompt = "  Select Mode:",
        font = Font(size = 20, fontWeight = Font.FontWeight.SEMI_BOLD)
    ).apply {
        visual = ImageVisual("dropdown_background.png")
        scale = 1.3
        isVisible = false
        isDisabled = true
    }

    /**
     *  Drop down Menu to select the AI speed.
     */
    private val aiSpeedSelector = ComboBox(
        width = 260,
        height = 40,
        posX = playerColorLabel.posX + 10,
        posY = fourthPlayerNameField.posY + 120,
        items = listOf("1000 ms", "3000 ms", "5000 ms", "7000 ms", "10000 ms"),
        prompt = "  Select AI Speed:",
        font = Font(size = 20, fontWeight = Font.FontWeight.SEMI_BOLD)
    ).apply {
        visual = ImageVisual("dropdown_background.png")
        scale = 1.3
    }

    /**
     *  Shows the Game ID in a Network game
     */
    val gameIDLabel = Label(
        posX = setupPlayersLabel.posX,
        posY = 0,
        width = 700,
        height = setupPlayersLabel.height,
        font = setupPlayersLabel.font,
        text = "Game ID: "
    ).apply {
        isVisible = false
    }

    /**
     *  List containing the first-fourth players type buttons.
     */
    private val playerTypeButtons =
        listOf(firstPlayerTypeButton, secondPlayerTypeButton, thirdPlayerTypeButton, fourthPlayerTypeButton)

    /**
     *  List containing the actual first-fourth player types in case the order was shuffled.
     */
    private var actualPlayerTypeButtons =
        mutableListOf(firstPlayerTypeButton, secondPlayerTypeButton, thirdPlayerTypeButton, fourthPlayerTypeButton)

    /**
     * All the player labels in a list, from first to last
     */
    private var playerLabels = listOf(firstPlayerLabel, secondPlayerLabel, thirdPlayerLabel, fourthPlayerLabel)

    /**
     *  List of the four player name input fields for getting the player order.
     */
    private val playerNameFields = listOf(
        firstPlayerNameField,
        secondPlayerNameField,
        thirdPlayerNameField,
        fourthPlayerNameField
    )

    /**
     *  First Player Color Buttons to determine the actual color buttons later.
     */
    private val firstPlayerColors =
        listOf(firstPlayerWhiteColor, firstPlayerRedColor, firstPlayerBlueColor, firstPlayerPurpleColor)

    /**
     *  Second Player Color Buttons to determine the actual color buttons later.
     */
    private val secondPlayerColors =
        listOf(secondPlayerWhiteColor, secondPlayerRedColor, secondPlayerBlueColor, secondPlayerPurpleColor)

    /**
     *  Third Player Color Buttons to determine the actual color buttons later.
     */
    private val thirdPlayerColors =
        listOf(thirdPlayerWhiteColor, thirdPlayerRedColor, thirdPlayerBlueColor, thirdPlayerPurpleColor)

    /**
     *  Fourth Player Color Buttons to determine the actual color buttons later.
     */
    private val fourthPlayerColors =
        listOf(fourthPlayerWhiteColor, fourthPlayerRedColor, fourthPlayerBlueColor, fourthPlayerPurpleColor)

    /**
     *  List of each of the four players colors.
     */
    private val playerColorButtons = listOf(
        firstPlayerColors,
        secondPlayerColors,
        thirdPlayerColors,
        fourthPlayerColors
    )

    /**
     *  List containing each of the actual first-fourth players color Buttons
     */
    private var actualPlayerColorButtons = mutableListOf(
        firstPlayerColors,
        secondPlayerColors,
        thirdPlayerColors,
        fourthPlayerColors
    )

    /**
     *  List of the player types in the correct order
     */
    var actualPlayerTypes = mutableListOf("", "", "", "")

    /**
     *  List of the player names in the correct order
     */
    var actualPlayerNames = mutableListOf("", "", "", "")

    /**
     *  List of the actual players colors first initialized empty
     */
    val actualPlayerColors = mutableListOf("", "", "", "")

    /**
     *  Variable for saving the selected game mode to be given to the IndigoApplication
     */
    var sharedGates = false

    /**
     *  Variable for saving the selected AI speed to be given to the IndigoApplication
     */
    var aiSpeed = 0

    /**
     *  List containing the actual first-fourth player name fields, in case the order was shuffled
     */
    private val actualNameFieldsList =
        mutableListOf(firstPlayerNameField, secondPlayerNameField, thirdPlayerNameField, fourthPlayerNameField)

    /**
     *  Determine the actual player types, fields and colors in order using their position,
     *  as well as sharedGates and aiSpeed
     */
    private fun determineActualValues() {

        // find the correct first-fourth player types
        playerTypeButtons.forEach { type ->
            if (type.posY.toInt() == absolutePlayerFieldPos[0] - 30) actualPlayerTypeButtons[0] = type
            if (type.posY.toInt() == absolutePlayerFieldPos[1] - 30) actualPlayerTypeButtons[1] = type
            if (type.posY.toInt() == absolutePlayerFieldPos[2] - 30) actualPlayerTypeButtons[2] = type
            if (type.posY.toInt() == absolutePlayerFieldPos[3] - 30) actualPlayerTypeButtons[3] = type
        }

        // Update the actualPlayerTypes list
        actualPlayerTypeButtons.forEach { typeButton ->
            // Find the index of the specified Type
            val indexOfType = actualPlayerTypeButtons.indexOf(typeButton)
            // Set the selected type in the actualPlayerType list
            actualPlayerTypes[indexOfType] = typeButton.name
        }

        // find our which player name field is now first-fourth and update the actualPlayerNames list
        playerNameFields.forEach { field ->
            if (field.posY.toInt() == absolutePlayerFieldPos[0]) {
                actualNameFieldsList[0] = field
                actualPlayerNames[0] = field.text
            }
            if (field.posY.toInt() == absolutePlayerFieldPos[1]) {
                actualNameFieldsList[1] = field
                actualPlayerNames[1] = field.text
            }
            if (field.posY.toInt() == absolutePlayerFieldPos[2]) {
                actualNameFieldsList[2] = field
                actualPlayerNames[2] = field.text
            }
            if (field.posY.toInt() == absolutePlayerFieldPos[3]) {
                actualNameFieldsList[3] = field
                actualPlayerNames[3] = field.text
            }
        }

        // find out which player colors are now first-fourth
        playerColorButtons.forEach { row ->
            if (row[0].posY.toInt() == absolutePlayerFieldPos[0] - 10) actualPlayerColorButtons[0] = row
            if (row[0].posY.toInt() == absolutePlayerFieldPos[1] - 10) actualPlayerColorButtons[1] = row
            if (row[0].posY.toInt() == absolutePlayerFieldPos[2] - 10) actualPlayerColorButtons[2] = row
            if (row[0].posY.toInt() == absolutePlayerFieldPos[3] - 10) actualPlayerColorButtons[3] = row
        }

        var indexOfColor: Int
        // Update the actualPlayerColors list
        actualPlayerColorButtons.forEach { row ->
            row.forEach { button ->
                if (button.scale == 1.3) {
                    indexOfColor = actualPlayerColorButtons.indexOf(row)
                    actualPlayerColors[indexOfColor] = button.name
                }
            }
        }

        if (!gameModeSelector.selectedItem.isNullOrBlank())
            sharedGates = gameModeSelector.selectedItem == "Shared Gates"

        val tempSpeed = aiSpeedSelector.selectedItem
        if (!tempSpeed.isNullOrBlank()) {
            aiSpeed = tempSpeed.dropLast(3).toInt()
        }
    }

    /**
     *  Checks if the entered player names and colors are valid, if not disables the start round button
     */
    private fun checkInputs() {
        var falseInput = false
        var otherPlayerColors: List<String>
        for (i in 0 until playerCount) {
            // If any name field contains the same text as another name field mark the inputs as faulty
            if (playerNameFields.any { it != playerNameFields[i] && it.text == playerNameFields[i].text })
                falseInput = true
            // variable to compare all other colors with the currently looked at color
            otherPlayerColors = actualPlayerColors.minus(actualPlayerColors[i])
            // If any two players select the same colors mark the inputs as faulty
            if (otherPlayerColors.any { it == actualPlayerColors[i] })
                falseInput = true
            // If any name fields is empty or any player did not select a color, mark the inputs as faulty
            if (playerNameFields[i].text.isBlank()) falseInput = true
            if (actualPlayerColors[i].isBlank()) falseInput = true
            if (playerCount == 3 &&
                (gameModeSelector.selectedItem.isNullOrBlank() ||
                        gameModeSelector.selectedItem == "  Select Mode:")
            ) falseInput = true
            if ((actualPlayerTypes[i] == "random" || actualPlayerTypes[i] == "smart") &&
                aiSpeedSelector.selectedItem.isNullOrBlank()
            )
                falseInput = true
        }

        // Disable the start round button if the inputs are faulty
        startRoundButton.isDisabled = falseInput || playerCount < 2
    }

    /**
     *  Scales the color buttons and adds the correct color to the list of actualPlayerColorButtons
     */
    private fun chooseColor(row: List<Button>, clickedButton: Button) {
        // Scale all buttons back to 1.0
        row.forEach { button ->
            button.scale = 1.0
        }
        // make the clicked button bigger
        clickedButton.scale = 1.3
        // Find the index of the specified row
        val indexOfRow = actualPlayerColorButtons.indexOf(row)
        // Set the selected color in the actualPlayerColors list
        actualPlayerColors[indexOfRow] = clickedButton.name

    }

    /**
     *  Disables all Buttons and name fields for a joining player so only the host can edit them
     */
    private fun disableAll() {
        playerLabels.forEach { label ->
            label.isVisible = false
        }
        actualNameFieldsList.forEach { field ->
            field.isDisabled = true
            field.isVisible = false
        }
        actualPlayerColorButtons.forEach { colorList ->
            colorList.forEach { colorButton ->
                colorButton.isDisabled = true
                colorButton.isVisible = false
            }
        }
        actualPlayerTypeButtons.forEach { button ->
            button.isDisabled = true
            button.isVisible = false
        }
        addThirdPlayerButton.apply {
            isDisabled = true
            isVisible = false
        }
        addFourthPlayerButton.apply {
            isDisabled = true
            isVisible = false
        }
        gameModeSelector.apply {
            isDisabled = true
            isVisible = false
        }
        aiSpeedSelector.isDisabled = true
        startRoundButton.apply {
            isDisabled = true
            isVisible = false
        }
        randomizePlayerOrderButton.apply {
            isDisabled = true
            isVisible = false
        }
    }

    /**
     *  Disables some text fields and buttons if you are the host
     */
    private fun disableHost() {
        playerLabels.drop(1).forEach { label ->
            label.isVisible = false
        }
        actualNameFieldsList[0].apply {
            text = rootService.networkService.playerName ?: "Ich"
            isDisabled = true
        }
        actualNameFieldsList.drop(1).forEach { field ->
            field.isDisabled = true
            field.isVisible = false
        }
        actualPlayerColorButtons.drop(1).forEach { colorList ->
            colorList.forEach { colorButton ->
                colorButton.isVisible = false
            }
        }
        actualPlayerTypeButtons.drop(1).forEach { button ->
            button.isDisabled = true
            button.isVisible = false
        }
        addThirdPlayerButton.apply {
            isDisabled = true
            isVisible = false
        }
        addFourthPlayerButton.apply {
            isDisabled = true
            isVisible = false
        }
        gameModeSelector.apply {
            isVisible = false
        }
    }

    /**
     *  Randomizes the player Order when pressed.
     */
    private val randomizePlayerOrderButton = Button(
        posX = 500,
        posY = 910,
        width = 150,
        height = 150,
        visual = ImageVisual("randomize_players.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        onMouseClicked = onMouseClicked@{
            if (playerCount <= 1) {
                return@onMouseClicked
            }

            val oldPlayerPos = absolutePlayerFieldPos.subList(0, playerCount).sorted()
            // randomize the player order by shuffling the input field positions
            randomizedPlayerFieldPos = oldPlayerPos
            while (randomizedPlayerFieldPos == oldPlayerPos) {
                randomizedPlayerFieldPos = absolutePlayerFieldPos.subList(0, playerCount).shuffled()
            }

            determineActualValues()

            // apply new positions to type, fields and colors
            actualPlayerTypeButtons[0].posY = randomizedPlayerFieldPos[0].toDouble() - 30
            actualNameFieldsList[0].posY = randomizedPlayerFieldPos[0].toDouble()
            actualPlayerColorButtons[0].forEach { color ->
                color.posY = randomizedPlayerFieldPos[0].toDouble() - 10
            }
            actualPlayerTypeButtons[1].posY = randomizedPlayerFieldPos[1].toDouble() - 30
            actualNameFieldsList[1].posY = randomizedPlayerFieldPos[1].toDouble()
            actualPlayerColorButtons[1].forEach { color ->
                color.posY = randomizedPlayerFieldPos[1].toDouble() - 10
            }
            if (playerCount >= 3) {
                actualPlayerTypeButtons[2].posY = randomizedPlayerFieldPos[2].toDouble() - 30
                actualNameFieldsList[2].posY = randomizedPlayerFieldPos[2].toDouble()
                actualPlayerColorButtons[2].forEach { color ->
                    color.posY = randomizedPlayerFieldPos[2].toDouble() - 10
                }
            }
            if (playerCount == 4) {
                actualPlayerTypeButtons[3].posY = randomizedPlayerFieldPos[3].toDouble() - 30
                actualNameFieldsList[3].posY = randomizedPlayerFieldPos[3].toDouble()
                actualPlayerColorButtons[3].forEach { color ->
                    color.posY = randomizedPlayerFieldPos[3].toDouble() - 10
                }
            }
            // Determine the actual player fields again, because their positions have changed
            determineActualValues()
        }
    }

    override fun refreshAfterJoiningGame(playerNames: List<String>) {
        for ((i, name) in playerNames.withIndex()) {
            actualNameFieldsList[i].apply {
                text = name
                isVisible = true
            }
            playerLabels[i].isVisible = true
        }

        playerLabels[playerNames.size].isVisible = true
        actualNameFieldsList[playerNames.size].apply {
            text = rootService.networkService.playerName ?: "Ich"
            isVisible = true
        }

        actualPlayerTypeButtons[playerNames.size].apply {
            if (rootService.networkService.useAI!!) {
                if (rootService.networkService.useSmartAI!!) {
                    visual = ImageVisual("smart_ai_icon.png")
                    name = "smart"
                    aiSpeedSelector.isVisible = true
                } else {
                    visual = ImageVisual("random_ai_icon.png")
                    name = "random"
                    aiSpeedSelector.isVisible = true
                }
            } else {
                visual = ImageVisual("player_icon.png")
                name = "player"
                aiSpeedSelector.isVisible = false
            }
            isVisible = true
        }

        aiSpeedSelector.selectedItem = rootService.networkService.aiMoveMilliseconds?.let { "$it ms" }
        playerCount = playerNames.size + 1
    }

    override fun refreshAfterJoinPlayer(playerName: String) {
        playerLabels[playerCount].isVisible = true
        actualNameFieldsList[playerCount].apply {
            text = playerName
            isVisible = true
        }

        if (gameMode == GameMode.HOST) {
            actualPlayerColorButtons[playerCount].forEach { button ->
                button.isVisible = true
                button.isDisabled = false
            }
        }

        playerCount++

        gameModeSelector.isVisible = playerCount == 3
        gameModeSelector.isDisabled = playerCount != 3

        determineActualValues()
        checkInputs()
    }

    override fun refreshAfterDisconnect(playerName: String) {
        // Find the player that disconnected
        var playerPos = -1

        for (i in 0 until playerCount) {
            if (actualNameFieldsList[i].text == playerName) {
                playerPos = i
                break
            }
        }

        if (playerPos == -1) {
            error("player not found")
        }

        // Rest of players go one position up
        for (i in playerPos + 1 until playerCount) {
            val newI = i - 1

            actualNameFieldsList[newI].text = actualNameFieldsList[i].text
            actualPlayerTypeButtons[newI].apply {
                visual = actualPlayerTypeButtons[i].visual
                name = actualPlayerTypeButtons[i].name
                isVisible = actualPlayerTypeButtons[i].isVisible
            }
            availableTypes[newI] = availableTypes[i]
            actualPlayerColorButtons[newI].apply {
                val oldPlayerButtons = actualPlayerColorButtons[i]

                for ((j, button) in actualPlayerTypeButtons.withIndex()) {
                    button.scale = oldPlayerButtons[j].scale
                    button.name = oldPlayerButtons[j].name
                }
            }
        }

        // Reset the name in the field and list
        playerLabels[playerCount - 1].isVisible = false
        actualNameFieldsList[playerCount - 1].apply {
            isVisible = false
            text = ""
        }

        availableTypes[playerCount - 1] = 0
        actualPlayerTypeButtons[playerCount - 1].apply {
            isVisible = false
        }

        // Reset the color and resize the color buttons
        actualPlayerColorButtons[playerCount - 1].forEach { button ->
            button.isVisible = false
            button.scale = 1.0
        }

        // Decrease the playerCount and determine the actual values again
        playerCount--
        determineActualValues()
        checkInputs()
    }

    init {
        addComponents(
            backgroundBox,
            returnButton,
            setupPlayersLabel,
            playerTypeLabel,
            playerColorLabel,
            firstPlayerTypeButton,
            firstPlayerLabel,
            firstPlayerNameField,
            firstPlayerWhiteColor,
            firstPlayerRedColor,
            firstPlayerBlueColor,
            firstPlayerPurpleColor,
            secondPlayerTypeButton,
            secondPlayerLabel,
            secondPlayerNameField,
            secondPlayerWhiteColor,
            secondPlayerRedColor,
            secondPlayerBlueColor,
            secondPlayerPurpleColor,
            thirdPlayerTypeButton,
            thirdPlayerLabel,
            thirdPlayerNameField,
            thirdPlayerWhiteColor,
            thirdPlayerRedColor,
            thirdPlayerBlueColor,
            thirdPlayerPurpleColor,
            fourthPlayerTypeButton,
            fourthPlayerLabel,
            fourthPlayerNameField,
            fourthPlayerWhiteColor,
            fourthPlayerRedColor,
            fourthPlayerBlueColor,
            fourthPlayerPurpleColor,
            randomizePlayerOrderButton,
            startRoundButton,
            addThirdPlayerButton,
            removeThirdPlayerButton,
            addFourthPlayerButton,
            removeFourthPlayerButton,
            gameModeSelector,
            aiSpeedSelector,
            gameIDLabel
        )

        background = ImageVisual("background.png")
        opacity = 0.4

        onSceneShown = {
            playerLabels.take(2).forEach { it.isVisible = true }
            actualPlayerTypeButtons.take(2).forEach {
                it.isVisible = true
                it.isDisabled = false
            }
            actualNameFieldsList.take(2).forEach {
                it.isVisible = true
                it.isDisabled = false
            }
            actualPlayerColorButtons.take(2).forEach {
                it.forEach { button ->
                    button.isVisible = true
                    button.isDisabled = false
                }
            }
            addThirdPlayerButton.apply {
                isVisible = true
                isDisabled = false
            }

            if (gameMode == GameMode.JOIN) {
                playerCount = 0
                disableAll()
            } else if (gameMode == GameMode.HOST) {
                playerCount = 1
                disableHost()
            }
        }

        // Clears all the fields and resets the player counter
        onSceneHid = {
            playerCount = 2

            for (i in 0..3) {
                // Reset the types, names, colors and field inputs
                availableTypes[i] = 0
                actualPlayerTypes[i] = ""
                actualPlayerNames[i] = ""
                actualPlayerColors[i] = ""
                actualNameFieldsList[i].text = ""
                // Reset the Type Buttons to player and hide third/fourth player button
                actualPlayerTypeButtons[i].apply {
                    name = "player"
                    visual = ImageVisual("player_icon.png")
                    if (i > 1) {
                        isVisible = false
                        isDisabled = true
                    }
                }
                // Hide third/fourth player
                if (i > 1) {
                    actualNameFieldsList[i].isVisible = false
                    actualNameFieldsList[i].isDisabled = true
                }

                // Reset the Color Button scale and hide the third/fourth player buttons
                actualPlayerColorButtons[i].forEach { button ->
                    button.scale = 1.0
                    if (i > 1) {
                        button.isVisible = false
                        button.isDisabled = true
                    }
                }
            }
            addThirdPlayerButton.isVisible = true
            addThirdPlayerButton.isDisabled = false
            addFourthPlayerButton.isVisible = false
            addFourthPlayerButton.isDisabled = true
            removeThirdPlayerButton.isVisible = false
            removeThirdPlayerButton.isDisabled = true
            removeFourthPlayerButton.isVisible = false
            removeFourthPlayerButton.isDisabled = true
            
            thirdPlayerLabel.isVisible = false
            fourthPlayerLabel.isVisible = false

            gameModeSelector.isVisible = false
            gameModeSelector.isDisabled = true
            gameModeSelector.selectedItem = ""
        }

        // Check player names while writing them
        firstPlayerNameField.onKeyTyped = {
            determineActualValues()
            checkInputs()
        }
        secondPlayerNameField.onKeyTyped = {
            determineActualValues()
            checkInputs()
        }
        thirdPlayerNameField.onKeyTyped = {
            determineActualValues()
            checkInputs()
        }
        fourthPlayerNameField.onKeyTyped = {
            determineActualValues()
            checkInputs()
        }

        /*
         *   Define the onMouseClicked behaviour for all Color Buttons iteratively.
         *   When a Color button is clicked, it puts the clicked color into the list of player colors and makes
         *   the button bigger than the other ones
         */
        playerColorButtons.forEach { row ->
            row.forEach { button ->
                button.onMouseClicked = {
                    chooseColor(row, button)
                    checkInputs()
                }
            }
        }

        // Check inputs when clicking on the game mode or AI selectors
        gameModeSelector.selectedItemProperty.addListener { _, _ ->
            determineActualValues()
            checkInputs()
        }
        aiSpeedSelector.selectedItemProperty.addListener { _, _ ->
            determineActualValues()
            checkInputs()
        }

        // Add functionality to the add and remove third/fourth player buttons.
        addThirdPlayerButton.apply {
            onMouseClicked = {
                // Update the player count.
                playerCount = 3

                // Determine the actual fields
                determineActualValues()

                // Show the Button to remove the third player.
                removeThirdPlayerButton.isDisabled = false
                removeThirdPlayerButton.isVisible = true

                // Show the player colors
                actualPlayerColorButtons[2].forEach { colorButton ->
                    colorButton.isVisible = true
                    colorButton.isDisabled = false
                }

                // show the game mode selector
                gameModeSelector.isVisible = true
                gameModeSelector.isDisabled = false

                // Hide this Button.
                isVisible = false
                isDisabled = true

                // Show the third player type, field and label.
                actualPlayerTypeButtons[2].isVisible = true
                actualPlayerTypeButtons[2].isDisabled = false
                thirdPlayerLabel.isVisible = true
                actualNameFieldsList[2].isVisible = true
                actualNameFieldsList[2].isDisabled = false

                // Show the button to add a fourth player.
                addFourthPlayerButton.isVisible = true
                addFourthPlayerButton.isDisabled = false
                checkInputs()
            }
        }

        removeThirdPlayerButton.apply {
            onMouseClicked = {
                // Update the player count.
                playerCount = 2

                // Determine the actual fields
                determineActualValues()

                // Clear the third players input and hide the field as well as label and type.
                thirdPlayerLabel.isVisible = false
                actualNameFieldsList[2].text = ""
                actualNameFieldsList[2].isVisible = false
                actualNameFieldsList[2].isDisabled = true
                actualPlayerTypeButtons[2].isVisible = false
                actualPlayerTypeButtons[2].isDisabled = true

                // Hide this Button.
                isVisible = false
                isDisabled = true

                // Hide the player colors
                actualPlayerColorButtons[2].forEach { colorButton ->
                    colorButton.isVisible = false
                    colorButton.isDisabled = true
                    colorButton.scale = 1.0
                }
                // Clear the third player color
                actualPlayerColors[2] = ""

                // Hide the game mode selector and reset the selection
                gameModeSelector.isVisible = false
                gameModeSelector.isDisabled = true
                gameModeSelector.selectedItem = "  Select Mode:"

                // Show add third player button again.
                addThirdPlayerButton.isVisible = true
                addThirdPlayerButton.isDisabled = false

                // Hide the add fourth player Button.
                addFourthPlayerButton.isVisible = false
                addFourthPlayerButton.isDisabled = true

                checkInputs()
            }
        }

        addFourthPlayerButton.apply {
            onMouseClicked = {
                // Update the player count
                playerCount = 4

                // Determine the actual fields
                determineActualValues()

                // Show the type, field and label for the fourth player
                actualPlayerTypeButtons[3].isVisible = true
                actualPlayerTypeButtons[3].isDisabled = false
                fourthPlayerLabel.isVisible = true
                actualNameFieldsList[3].isVisible = true
                actualNameFieldsList[3].isDisabled = false

                // Show the player colors
                actualPlayerColorButtons[3].forEach { colorButton ->
                    colorButton.isVisible = true
                    colorButton.isDisabled = false
                }

                // Hide the game mode selector and reset the selection
                gameModeSelector.isVisible = false
                gameModeSelector.isDisabled = true
                gameModeSelector.selectedItem = "  Select Mode:"

                // Hide this Button
                isVisible = false
                isDisabled = true

                // Show the button to remove the added fourth player
                removeFourthPlayerButton.isVisible = true
                removeFourthPlayerButton.isDisabled = false

                // Hide the button to remove the third player, as only the last added player should be removable
                removeThirdPlayerButton.isVisible = false
                removeThirdPlayerButton.isDisabled = true

                checkInputs()
            }
        }

        removeFourthPlayerButton.apply {
            onMouseClicked = {
                // Update the player count.
                playerCount = 3

                // Determine the actual fields
                determineActualValues()

                // Clear the fourth players input and hide the field as well as the label and type.
                actualNameFieldsList[3].text = ""
                actualNameFieldsList[3].isVisible = false
                actualNameFieldsList[3].isDisabled = true
                fourthPlayerLabel.isVisible = false
                actualPlayerTypeButtons[3].isVisible = false
                actualPlayerTypeButtons[3].isDisabled = true

                // Hide this Button.
                isVisible = false
                isDisabled = true

                // Hide the player colors
                actualPlayerColorButtons[3].forEach { colorButton ->
                    colorButton.isVisible = false
                    colorButton.isDisabled = true
                    colorButton.scale = 1.0
                }
                // Clear the fourth player color
                actualPlayerColors[3] = ""

                // Show the game mode selector
                gameModeSelector.isVisible = true
                gameModeSelector.isDisabled = false

                // Show the remove third player button again.
                removeThirdPlayerButton.isVisible = true
                removeThirdPlayerButton.isDisabled = false

                // Show the add fourth player button again.
                addFourthPlayerButton.isVisible = true
                addFourthPlayerButton.isDisabled = false

                checkInputs()
            }
        }
    }
}