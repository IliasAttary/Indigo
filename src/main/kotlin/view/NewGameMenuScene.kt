package view

import tools.aqua.bgw.components.uicomponents.Button
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
class NewGameMenuScene : MenuScene(1920, 1080), Refreshable {

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
     *  A list of the player names in order.
     */
    private var playerNames = mutableListOf<String>()

    /**
     *  Variable saving the player count.
     */
    private var playerCount = 2

    /**
     *  Saves the player types for game initiation.
     *  The Player Types are as follows:
     *  0 = Player,
     *  1 = Random AI,
     *  2 = Smart AI.
     */
    private val playerTypes = mutableListOf(0, 0, 0, 0)

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
            playerTypes[0] = (playerTypes[0] + 1) % 3
            when (playerTypes[0]) {
                0 -> visual = ImageVisual("player_icon.png")
                1 -> visual = ImageVisual("random_ai_icon.png")
                2 -> visual = ImageVisual("smart_ai_icon.png")
            }
        }
    }

    /**
     *  Indicates the third players name.
     */
    val firstPlayerLabel = Label(
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
    val firstPlayerNameField = TextField(
        posX = (1920 - 400) / 2,
        posY = absolutePlayerFieldPos[0],
        width = 400,
        height = 40,
        font = Font(size = 20)
    ).apply {
        onKeyTyped = {
            startRoundButton.isDisabled = text.isBlank()
        }
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
            playerTypes[1] = (playerTypes[1] + 1) % 3
            when (playerTypes[1]) {
                0 -> visual = ImageVisual("player_icon.png")
                1 -> visual = ImageVisual("random_ai_icon.png")
                2 -> visual = ImageVisual("smart_ai_icon.png")
            }
        }
    }

    /**
     *  Indicates the third players name.
     */
    val secondPlayerLabel = Label(
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
    val secondPlayerNameField = TextField(
        posX = (1920 - 400) / 2,
        posY = absolutePlayerFieldPos[1],
        width = 400,
        height = 40,
        font = Font(size = 20)
    ).apply {
        onKeyTyped = {
            startRoundButton.isDisabled = text.isBlank()
        }
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
        onMouseClicked = {
            playerTypes[2] = (playerTypes[2] + 1) % 3
            when (playerTypes[2]) {
                0 -> visual = ImageVisual("player_icon.png")
                1 -> visual = ImageVisual("random_ai_icon.png")
                2 -> visual = ImageVisual("smart_ai_icon.png")
            }
        }
    }

    /**
     *  Indicates the third players name.
     */
    val thirdPlayerLabel = Label(
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
    val thirdPlayerNameField = TextField(
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
        }
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
        posX = thirdPlayerNameField.posX + 440,
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
        onMouseClicked = {
            playerTypes[3] = (playerTypes[3] + 1) % 3
            when (playerTypes[3]) {
                0 -> visual = ImageVisual("player_icon.png")
                1 -> visual = ImageVisual("random_ai_icon.png")
                2 -> visual = ImageVisual("smart_ai_icon.png")
            }
        }
    }

    /**
     *  Indicates the third players name.
     */
    val fourthPlayerLabel = Label(
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
    val fourthPlayerNameField = TextField(
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
        }
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
        posX = fourthPlayerNameField.posX + 440,
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
     *  List of the four player name input fields for getting the player order
     */
    private val playerNameFields = listOf(
        firstPlayerNameField,
        secondPlayerNameField,
        thirdPlayerNameField,
        fourthPlayerNameField
    )

    // Variables for finding the actual player fields in case the order was shuffled
    private var actualFirstPlayerField = firstPlayerNameField
    private var actualSecondPlayerField = secondPlayerNameField
    private var actualThirdPlayerField = thirdPlayerNameField
    private var actualFourthPlayerField = fourthPlayerNameField

    private var actualFieldsList = listOf<TextField>()

    /**
     *  Determine the actual player fields in order using their position
     */
    private fun determineActualFields() {
        //
        playerNameFields.forEach { field ->
            if (field.posY.toInt() == absolutePlayerFieldPos[0]) actualFirstPlayerField = field
            if (field.posY.toInt() == absolutePlayerFieldPos[1]) actualSecondPlayerField = field
            if (field.posY.toInt() == absolutePlayerFieldPos[2]) actualThirdPlayerField = field
            if (field.posY.toInt() == absolutePlayerFieldPos[3]) actualFourthPlayerField = field
        }
        actualFieldsList =
            listOf(actualFirstPlayerField, actualSecondPlayerField, actualThirdPlayerField, actualFourthPlayerField)
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
        onMouseClicked = {
            // randomize the player order by shuffling the input field positions
            randomizedPlayerFieldPos = absolutePlayerFieldPos.subList(0, playerCount).shuffled()

            // Determine the actual player fields
            determineActualFields()

            // apply new positions to fields
            actualFirstPlayerField.posY = randomizedPlayerFieldPos[0].toDouble()
            actualSecondPlayerField.posY = randomizedPlayerFieldPos[1].toDouble()
            if (playerCount >= 3) actualThirdPlayerField.posY = randomizedPlayerFieldPos[2].toDouble()
            if (playerCount == 4) actualFourthPlayerField.posY = randomizedPlayerFieldPos[3].toDouble()

            // Determine the actual player fields again, because their positions have changed
            determineActualFields()

            // Set player names list to the correct size
            for (i in 0 until playerCount) {
                if (playerNames.size < playerCount)
                    playerNames.add("")
                else if (playerNames.size > playerCount)
                    playerNames.remove(playerNames.last())
            }

            // Set the player names in the new order
            for (player in 0 until playerCount) {
                playerNames[player] = actualFieldsList[player].text.trim()
            }
        }
    }

    init {
        addComponents(
            backgroundBox,
            returnButton,
            setupPlayersLabel,
            playerTypeLabel,
            firstPlayerTypeButton,
            secondPlayerTypeButton,
            thirdPlayerTypeButton,
            fourthPlayerTypeButton,
            firstPlayerNameField,
            firstPlayerLabel,
            secondPlayerNameField,
            secondPlayerLabel,
            thirdPlayerNameField,
            thirdPlayerLabel,
            fourthPlayerNameField,
            fourthPlayerLabel,
            randomizePlayerOrderButton,
            startRoundButton,
            addThirdPlayerButton,
            removeThirdPlayerButton,
            addFourthPlayerButton,
            removeFourthPlayerButton
        )

        background = ImageVisual("background.png")
        opacity = 0.4

        // Add functionality to the add and remove third/fourth player buttons.
        addThirdPlayerButton.apply {
            onMouseClicked = {
                // Update the player count.
                playerCount = 3

                // Determine the actual fields
                determineActualFields()

                // Show the Button to remove the third player.
                removeThirdPlayerButton.isDisabled = false
                removeThirdPlayerButton.isVisible = true

                // Hide this Button.
                isVisible = false
                isDisabled = true

                // Show the third player field and label.
                thirdPlayerLabel.isVisible = true
                actualThirdPlayerField.isVisible = true
                actualThirdPlayerField.isDisabled = false

                // Show the button to add a fourth player.
                addFourthPlayerButton.isVisible = true
                addFourthPlayerButton.isDisabled = false
            }
        }

        removeThirdPlayerButton.apply {
            onMouseClicked = {
                // Update the player count.
                playerCount = 2

                // Determine the actual fields
                determineActualFields()

                // Clear the third players input and hide the field as well as label.
                thirdPlayerLabel.isVisible = false
                actualThirdPlayerField.text = ""
                actualThirdPlayerField.isVisible = false
                actualThirdPlayerField.isDisabled = true

                // Hide this Button.
                isVisible = false
                isDisabled = true

                // Show add third player button again.
                addThirdPlayerButton.isVisible = true
                addThirdPlayerButton.isDisabled = false

                // Hide the add fourth player Button.
                addFourthPlayerButton.isVisible = false
                addFourthPlayerButton.isDisabled = true


            }
        }

        addFourthPlayerButton.apply {
            onMouseClicked = {
                // Update the player count
                playerCount = 4

                // Determine the actual fields
                determineActualFields()

                // Show the Input field and label for the fourth player
                fourthPlayerLabel.isVisible = true
                actualFourthPlayerField.isVisible = true
                actualFourthPlayerField.isDisabled = false

                // Hide this Button
                isVisible = false
                isDisabled = true

                // Show the button to remove the added fourth player
                removeFourthPlayerButton.isVisible = true
                removeFourthPlayerButton.isDisabled = false

                // Hide the button to remove the third player, as only the last added player should be removable
                removeThirdPlayerButton.isVisible = false
                removeThirdPlayerButton.isDisabled = true
            }
        }

        removeFourthPlayerButton.apply {
            onMouseClicked = {
                // Update the player count.
                playerCount = 3

                // Determine the actual fields
                determineActualFields()

                // Clear the fourth players input and hide the field as well as the label.
                actualFourthPlayerField.text = ""
                actualFourthPlayerField.isVisible = false
                actualFourthPlayerField.isDisabled = true
                fourthPlayerLabel.isVisible = false

                // Hide this Button.
                isVisible = false
                isDisabled = true

                // Show the remove third player button again.
                removeThirdPlayerButton.isVisible = true
                removeThirdPlayerButton.isDisabled = false

                // Show the add fourth player button again.
                addFourthPlayerButton.isVisible = true
                addFourthPlayerButton.isDisabled = false
            }
        }
    }
}