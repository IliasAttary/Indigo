package view

import entity.*
import service.ConnectionState
import service.RootService
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.*
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color

/**
 * Main Game Scene for Indigo.
 */
class MainGameScene(private val rootService: RootService) : BoardGameScene(2160, 1080), Refreshable {

    /**
     *  Button to undo action.
     */
    private val undoButton = Button(
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
        onMouseClicked = {
            rootService.playerService.undo()
        }
    }

    /**
     *  Button to redo action.
     */
    private val redoButton = Button(
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
        onMouseClicked = {
            rootService.playerService.redo()
        }
    }

    /**
     *  Button to show the rules.
     */
    val rulesButton = Button(
        width = 300,
        height = 150,
        posX = (2160 - 350),
        posY = (1080 - 150) / 2,
        text = "Rules",
        font = Font(size = 40),
        visual = ImageVisual("button_background.png")
    ).apply {
        visual.borderRadius = BorderRadius(15)
        visual.backgroundRadius = BackgroundRadius(15)
        scale = 0.9
    }

    /**
     *  Button to save the game.
     */
    private val saveButton = Button(
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
        onMouseClicked = {
            rootService.gameService.save()
        }
    }

    /**
     * Button to quit the game.
     */
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

    /**
     *  Shows the first players color
     */
    private val firstPlayerColor = Label(
        posX = 30,
        posY = 130,
        width = 60,
        height = 60
    )

    /**
     *  Shows the name of the first player
     */
    private val firstPlayerNameLabel = Label(
        posX = firstPlayerColor.posX + 50,
        posY = firstPlayerColor.posY,
        width = 300,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  Shows the current player
     */
    private val currentPlayerIndicator = Label(
        posX = (firstPlayerColor.posX - 10),
        posY = (firstPlayerColor.posY - 10),
        width = 600,
        height = 80,
        visual = ImageVisual("currentPlayerIndicator.png")
    )

    /**
     *  shows the heldTile of the first player.
     */
    private var firstPlayerHeldTileView = HexagonView(
        posX = firstPlayerColor.posX + 500,
        posY = firstPlayerColor.posY - 10,
        size = 40,
        visual = ColorVisual(Color(235, 230, 188))
    ).apply {
        rotation = 30.0
    }

    /**
     *  Shows the first players color
     */
    private val secondPlayerColor = Label(
        posX = 30,
        posY = firstPlayerColor.posY + 80,
        width = 60,
        height = 60
    )

    /**
     *  Shows the name of the second player
     */
    private val secondPlayerNameLabel = Label(
        posX = secondPlayerColor.posX + 50,
        posY = secondPlayerColor.posY,
        width = 300,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  shows the heldTile of the second player.
     */
    private var secondPlayerHeldTileView = HexagonView(
        posX = firstPlayerHeldTileView.posX,
        posY = secondPlayerColor.posY - 10,
        size = firstPlayerHeldTileView.size,
        visual = ColorVisual(Color(235, 230, 188))
    ).apply {
        rotation = 30.0
    }

    /**
     *  Shows the first players color
     */
    private val thirdPlayerColor = Label(
        posX = 30,
        posY = secondPlayerColor.posY + 80,
        width = 60,
        height = 60
    )

    /**
     *  Shows the name of the third player
     */
    private val thirdPlayerNameLabel = Label(
        posX = thirdPlayerColor.posX + 50,
        posY = thirdPlayerColor.posY,
        width = 300,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  shows the heldTile of the third player.
     */
    private var thirdPlayerHeldTileView = HexagonView(
        posX = secondPlayerHeldTileView.posX,
        posY = thirdPlayerColor.posY - 10,
        size = firstPlayerHeldTileView.size,
        visual = ColorVisual(Color(235, 230, 188))
    ).apply {
        rotation = 30.0
        isVisible = false
    }

    /**
     *  Shows the first players color
     */
    private val fourthPlayerColor = Label(
        posX = 30,
        posY = thirdPlayerColor.posY + 80,
        width = 60,
        height = 60
    )

    /**
     *  Shows the name of the fourth player
     */
    private val fourthPlayerNameLabel = Label(
        posX = fourthPlayerColor.posX + 50,
        posY = fourthPlayerColor.posY,
        width = 300,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  shows the heldTile of the fourth player.
     */
    private var fourthPlayerHeldTileView = HexagonView(
        posX = thirdPlayerHeldTileView.posX,
        posY = fourthPlayerColor.posY - 10,
        size = firstPlayerHeldTileView.size,
        visual = ColorVisual(Color(235, 230, 188))
    ).apply {
        rotation = 30.0
        isVisible = false
    }

    /**
     *  List of all the players HeldTileView elements
     */
    private val playerHeldTileList =
        listOf(firstPlayerHeldTileView, secondPlayerHeldTileView, thirdPlayerHeldTileView, fourthPlayerHeldTileView)

    /**
     *  Blue Gem indicates how many sapphires the players collected
     */
    private val blueGemIndicator = Label(
        posX = 150,
        posY = 30,
        width = 60,
        height = 60,
        visual = ImageVisual("gem_blue.png")
    )

    /**
     *  green Gem indicates how many emeralds the players collected
     */
    private val greenGemIndicator = Label(
        posX = blueGemIndicator.posX + 150,
        posY = 30,
        width = 60,
        height = 60,
        visual = ImageVisual("gem_green.png")
    )

    /**
     *  yellow Gem indicates how many ambers the players collected
     */
    private val yellowGemIndicator = Label(
        posX = greenGemIndicator.posX + 150,
        posY = 30,
        width = 60,
        height = 60,
        visual = ImageVisual("gem_yellow.png")
    )

    /**
     *  Label for displaying the first players collected Sapphires
     */
    private val firstPlayerSapphires = Label(
        posX = blueGemIndicator.posX,
        posY = firstPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    )

    /**
     *  Label for displaying the first players collected Emeralds
     */
    private val firstPlayerEmeralds = Label(
        posX = greenGemIndicator.posX,
        posY = firstPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    )

    /**
     *  Label for displaying the first players collected Ambers
     */
    private val firstPlayerAmbers = Label(
        posX = yellowGemIndicator.posX,
        posY = firstPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    )

    /**
     *  Label for displaying the second players collected Sapphires
     */
    private val secondPlayerSapphires = Label(
        posX = blueGemIndicator.posX,
        posY = secondPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    )

    /**
     *  Label for displaying the second players collected Emeralds
     */
    private val secondPlayerEmeralds = Label(
        posX = greenGemIndicator.posX,
        posY = secondPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    )

    /**
     *  Label for displaying the second players collected Ambers
     */
    private val secondPlayerAmbers = Label(
        posX = yellowGemIndicator.posX,
        posY = secondPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    )

    /**
     *  Label for displaying the third players collected Sapphires
     */
    private val thirdPlayerSapphires = Label(
        posX = blueGemIndicator.posX,
        posY = thirdPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  Label for displaying the third players collected Emeralds
     */
    private val thirdPlayerEmeralds = Label(
        posX = greenGemIndicator.posX,
        posY = thirdPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  Label for displaying the third players collected Ambers
     */
    private val thirdPlayerAmbers = Label(
        posX = yellowGemIndicator.posX,
        posY = thirdPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  Label for displaying the fourth players collected Sapphires
     */
    private val fourthPlayerSapphires = Label(
        posX = blueGemIndicator.posX,
        posY = fourthPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  Label for displaying the fourth players collected Emeralds
     */
    private val fourthPlayerEmeralds = Label(
        posX = greenGemIndicator.posX,
        posY = fourthPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  Label for displaying the fourth players collected Ambers
     */
    private val fourthPlayerAmbers = Label(
        posX = yellowGemIndicator.posX,
        posY = fourthPlayerColor.posY,
        width = 60,
        height = 60,
        font = Font(
            size = 30.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  Rotates the currentPlayerHeldTileView 60 Degrees to the right.
     */
    private val rightTurnButton = Button(
        width = 150,
        height = 150,
        posX = 300,
        posY = 900,
        visual = ImageVisual("arrow-right.png")
    ).apply {
        onMouseClicked = onMouseClicked@{
            if (rootService.networkService.connectionState !in listOf(ConnectionState.DISCONNECTED, ConnectionState.PLAYING_MY_TURN)) {
                return@onMouseClicked
            }

            val game = rootService.currentGame
            checkNotNull(game)
            val currenTile = game.playerAtTurn.heldTile ?: return@onMouseClicked
            currenTile.rotation = (currenTile.rotation + 1) % 6
            updateHeldTiles()
            checkTiles()
            markForbiddenTiles = true
        }
    }

    /**
     *  Rotates the currentPlayerHeldTileView 60 Degrees to the left.
     */
    private val leftTurnButton = Button(
        width = 150,
        height = 150,
        posX = 100,
        posY = 900,
        visual = ImageVisual("arrow-left.png")
    ).apply {
        onMouseClicked = onMouseClicked@{
            if (rootService.networkService.connectionState !in listOf(ConnectionState.DISCONNECTED, ConnectionState.PLAYING_MY_TURN)) {
                return@onMouseClicked
            }

            val game = rootService.currentGame
            checkNotNull(game)
            val currenTile = game.playerAtTurn.heldTile ?: return@onMouseClicked
            when (currenTile.rotation) {
                0 -> currenTile.rotation = 5
                1 -> currenTile.rotation = 0
                2 -> currenTile.rotation = 1
                3 -> currenTile.rotation = 2
                4 -> currenTile.rotation = 3
                5 -> currenTile.rotation = 4
            }
            updateHeldTiles()
            checkTiles()
            markForbiddenTiles = true
        }
    }

    /**
     *  Map of tiles to link the view layer with the service layer.
     */
    private var tileMap: BidirectionalMap<Tile, HexagonView> = BidirectionalMap()

    /**
     *  View element representing the game board.
     */
    private val gameBoard: HexagonGrid<HexagonView> = HexagonGrid<HexagonView>(
        posX = 1180,
        posY = 450,
        coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL
    ).apply {
        rotation = 30.0
    }

    /**
     *  Background Image for the board
     */
    private var boardBackground = Label(
        posX = firstPlayerHeldTileView.posX + 123,
        posY = firstPlayerHeldTileView.posY - 122,
        width = 1110,
        height = 1090,
        visual = ImageVisual("board_background.png"),
        alignment = Alignment.CENTER
    )//.apply { isVisible = false }

    // background image with black overlay
    private val blackOverlay = ColorVisual(color = Color.black).apply { transparency = 0.7 }

    private val backgroundOverlay = CompoundVisual(children = listOf(ImageVisual("background.png"), blackOverlay))

    /**
     *  shows the heldTile of the current player.
     */
    private var currentPlayerHeldTileView = HexagonView(
        posX = 160,
        posY = 700,
        size = 120,
        visual = ColorVisual(Color(235, 230, 188))
    ).apply {
        rotation = 30.0
    }

    /**
     *  Displays the first gates color one
     */
    private var gateOneOne = Label(
        posX = 1375,
        posY = 45,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "1_1"
    )

    /**
     *  Displays the first gates color two
     */
    private var gateOneTwo = Label(
        posX = 1480,
        posY = 104,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "1_2"
    )

    /**
     *  Displays the second gates color one
     */
    private var gateTwoOne = Label(
        posX = 1678,
        posY = 452,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "2_1"
    )

    /**
     *  Displays the second gates color two
     */
    private var gateTwoTwo = Label(
        posX = 1678,
        posY = 570,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "2_2"
    )

    /**
     *  Displays the third gates color one
     */
    private var gateThreeOne = Label(
        posX = 1477,
        posY = 920,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "3_1"
    )

    /**
     *  Displays the third gates color two
     */
    private var gateThreeTwo = Label(
        posX = 1374,
        posY = 979,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "3_2"
    )

    /**
     *  Displays the fourth gates color one
     */
    private var gateFourOne = Label(
        posX = 975,
        posY = 980,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "4_1"
    )

    /**
     *  Displays the fourth gates color two
     */
    private var gateFourTwo = Label(
        posX = 870,
        posY = 921,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "4_2"
    )

    /**
     *  Displays the fifth gates color one
     */
    private var gateFiveOne = Label(
        posX = 675,
        posY = gateTwoOne.posY,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "5_1"
    )

    /**
     *  Displays the fifth gates color two
     */
    private var gateFiveTwo = Label(
        posX = 675,
        posY = gateTwoTwo.posY,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "5_2"
    )

    /**
     *  Displays the sixth gates color one
     */
    private var gateSixOne = Label(
        posX = gateFourTwo.posX + 2,
        posY = gateOneTwo.posY + 2,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "6_1"
    )

    /**
     *  Displays the sixth gates color two
     */
    private var gateSixTwo = Label(
        posX = gateFourOne.posX,
        posY = gateOneOne.posY,
        width = 60,
        height = 60,
        visual = ImageVisual("color_white.png"),
        text = "6_2"
    )


    /**
     *  Variable for storing whether the illegal Tiles should be shown or not.
     */
    private var markForbiddenTiles = false

    /**
     *  Variable for storing whether the first players name or gem count should be shown
     */
    private var showFirstPlayerName = false

    /**
     *  Variable for storing whether the second players name or gem count should be shown
     */
    private var showSecondPlayerName = false

    /**
     *  Variable for storing whether the third players name or gem count should be shown
     */
    private var showThirdPlayerName = false

    /**
     *  Variable for storing whether the fourth players name or gem count should be shown
     */
    private var showFourthPlayerName = false

    /**
     *  List of the all the player gem labels
     */
    private val playerGemLabels = listOf(
        listOf(firstPlayerSapphires, firstPlayerEmeralds, firstPlayerAmbers),
        listOf(secondPlayerSapphires, secondPlayerEmeralds, secondPlayerAmbers),
        listOf(thirdPlayerSapphires, thirdPlayerEmeralds, thirdPlayerAmbers),
        listOf(fourthPlayerSapphires, fourthPlayerEmeralds, fourthPlayerAmbers),
    )

    /**
     * List of gems on board
     */
    private val boardGems: MutableList<TokenView> = mutableListOf()

    /**
     *  Determines the Tile on the specified coordinates is a Treasure tile and returns the corresponding View for it,
     *  if not returns a single colored HexagonView
     */
    private fun getInitialTileView(q: Int, r: Int): HexagonView {
        val hexagon: HexagonView
        if (q == 0 && r == 0) {
            hexagon = HexagonView(
                size = 68,
                visual = ImageVisual("middleTreasureTile.png")
            )
        } else if (q == 0 && r == -4) {
            hexagon = HexagonView(
                size = 68,
                visual = ImageVisual("treasureTile.png")
            ).apply { rotation = 240.0 }
        } else if (q == 0 && r == 4) {
            hexagon = HexagonView(
                size = 68,
                visual = ImageVisual("treasureTile.png")
            ).apply { rotation = 60.0 }
        } else if (q == 4 && r == -4) {
            hexagon = HexagonView(
                size = 68,
                visual = ImageVisual("treasureTile.png")
            ).apply { rotation = 300.0 }
        } else if (q == 4 && r == 0) {
            hexagon = HexagonView(
                size = 68,
                visual = ImageVisual("treasureTile.png")
            )
        } else if (q == -4 && r == 0) {
            hexagon = HexagonView(
                size = 68,
                visual = ImageVisual("treasureTile.png")
            ).apply { rotation = 180.0 }
        } else if (q == -4 && r == 4) {
            hexagon = HexagonView(
                size = 68,
                visual = ImageVisual("treasureTile.png")
            ).apply { rotation = 120.0 }
        } else {
            // default one colored view
            hexagon = HexagonView(
                size = 68,
                visual = CompoundVisual(
                    ColorVisual(Color(235, 230, 188)),
                    TextVisual(
                        text = "($q, $r)",
                        font = Font(15.0, fontStyle = Font.FontStyle.ITALIC, color = Color.BLACK),
                    )
                )
            ).apply {
                onMouseClicked = {
                    if (rootService.networkService.connectionState in listOf(ConnectionState.DISCONNECTED, ConnectionState.PLAYING_MY_TURN)) {
                        if (rootService.playerService.checkPlacement(AxialPos(q, r))) {
                            rootService.playerService.placeTile(AxialPos(q, r))
                        }
                    }
                }

                onMouseEntered = {
                    if (rootService.networkService.connectionState in listOf(ConnectionState.DISCONNECTED, ConnectionState.PLAYING_MY_TURN)) {
                        if (rootService.playerService.checkPlacement(AxialPos(q, r)))
                            scale = 1.2
                    }
                }

                onMouseExited = {
                    scale = 1.0
                }
            }
        }
        return hexagon
    }

    /**
     *  Updates the heldTileViews for all players to the currently held Tile.
     */
    private fun updateHeldTiles() {
        val game = rootService.currentGame
        checkNotNull(game)
        var player: Player
        var heldTile: RouteTile?
        var imagePath: String

        // Get current player count
        val playerCount = game.currentPlayers.size

        // Update the heldTileView for all players in the List
        for (i in 0 until playerCount) {
            player = game.currentPlayers[i]
            heldTile = game.currentPlayers[i].heldTile
            if (heldTile != null) {
                imagePath = "${heldTile.tileType.toString().lowercase()}_${heldTile.rotation}.png"
                playerHeldTileList[i].visual = ImageVisual(imagePath)
            } else {
                playerHeldTileList[i].visual = ColorVisual(Color.BLACK)
            }

            // Update the heldTileView for the currently active player
            if (player == game.playerAtTurn) {
                if (heldTile != null) {
                    imagePath = "${heldTile.tileType.toString().lowercase()}_${heldTile.rotation}.png"
                    currentPlayerHeldTileView.visual = ImageVisual(imagePath)
                } else
                    currentPlayerHeldTileView.visual = ColorVisual(Color.BLACK)
            }
        }
    }

    /**
     *  Updates the counters for all player gems
     */
    private fun updatePlayerGems() {

        // Variables for storing the gems and their counts
        var collectedGems: MutableMap<Gem, Int>
        var collectedSapphires: Int?
        var collectedEmeralds: Int?
        var collectedAmbers: Int?

        val game = rootService.currentGame
        checkNotNull(game)
        val playerCount = game.currentPlayers.size
        for (i in 0 until playerCount) {
            // Get the currently inspected Players collectedGems
            collectedGems = game.currentPlayers[i].collectedGems
            // Get the distinct gem Types and ensure they are not null
            collectedSapphires = collectedGems[Gem.SAPPHIRE]
            collectedEmeralds = collectedGems[Gem.EMERALD]
            collectedAmbers = collectedGems[Gem.AMBER]
            checkNotNull(collectedSapphires)
            checkNotNull(collectedEmeralds)
            checkNotNull(collectedAmbers)
            // Update the players gem counts
            playerGemLabels[i][0].text = collectedSapphires.toString()
            playerGemLabels[i][1].text = collectedEmeralds.toString()
            playerGemLabels[i][2].text = collectedAmbers.toString()
        }
    }

    /**
     *  Marks all Tiles that are not available for placement by making them opaque.
     */
    private fun checkTiles() {
        val size = 4
        for (q in -size..size) {
            for (r in -size..size) {
                if (q + r <= size && q + r >= -size) {
                    // Mark all tiles where the currently held tile can not be placed
                    if (!rootService.playerService.checkPlacement(AxialPos(q, r))) {
                        gameBoard[q, r]?.apply {
                            opacity = 0.5
                        }
                    } else
                    // Unmark all other tiles
                        gameBoard[q, r]?.apply {
                            opacity = 1.0
                        }
                }
            }
        }
    }

    /**
     *  Changes the opacity for all marked tiles back to 1.0
     */
    private fun unmarkTiles() {
        val size = 4
        for (q in -size..size) {
            for (r in -size..size) {
                if (q + r <= size && q + r >= -size) {
                    // unmark all tiles
                    gameBoard[q, r]?.apply {
                        opacity = 1.0
                    }
                }
            }
        }
    }

    /**
     *  Fills the game board when creating a new game
     */
    private fun initializeGameBoard() {
        val game = rootService.currentGame
        checkNotNull(game)

        gameBoard.apply {
            // Radius of axial hexagon grid
            val size = 4
            var hexagon: HexagonView
            var currentBoardTile: Tile?
            var currentHexagonView: HexagonView?
            // Initialize empty game board with treasure tiles on it
            for (q in -size..size) {
                for (r in -size..size) {
                    if (q + r <= size && q + r >= -size) {
                        // Set the hexagon view element with the right attributes
                        hexagon = getInitialTileView(q, r)
                        // Overwrite the current View in the grid
                        this[q, r] = hexagon
                        // Get the current Board tile and View Element
                        currentBoardTile = game.currentBoard[AxialPos(q, r)]
                        currentHexagonView = this[q, r]
                        // update the tileMap with the Tile and View Element if a tile exists on AxialPos(q,r)
                        checkNotNull(currentHexagonView)
                        if (currentBoardTile != null)
                            tileMap.add(currentBoardTile, currentHexagonView)
                    }
                }
            }
        }

        updateBoardGems()
    }

    /**
     * Updates the gems on the boards
     */
    private fun updateBoardGems() {
        val gemSize = 30
        val tileSize = 68

        val game = rootService.currentGame
        checkNotNull(game)

        removeComponents(*boardGems.toTypedArray())
        boardGems.clear()

        fun gemToImageVisual(gem: Gem): ImageVisual {
            return when (gem) {
                Gem.AMBER -> ImageVisual("gem_yellow.png")
                Gem.EMERALD -> ImageVisual("gem_green.png")
                Gem.SAPPHIRE -> ImageVisual("gem_blue.png")
            }
        }

        for ((pos, tile) in game.currentBoard) {
            val boardTile = gameBoard[pos.q, pos.r] ?: continue
            val tilePosX = gameBoard.actualPosX + boardTile.actualPosX + boardTile.actualWidth / 2
            val tilePosY = gameBoard.actualPosY + boardTile.actualPosY + boardTile.actualHeight / 2
            val tileCenterPos = Coordinate(tilePosX, tilePosY).rotated(
                30.0,
                Coordinate(
                    gameBoard.actualPosX + gameBoard.actualWidth / 2,
                    gameBoard.actualPosY + gameBoard.actualHeight / 2,
                )
            )

            fun edgeToPos(edge: Int, treasureTileCircle: Boolean, angle: Double = 60.0, angleOffset: Double = 0.0): Coordinate {
                val length = if (treasureTileCircle) tileSize / 2.0 - 5 else tileSize / 2.0 + 10
                val edge0Vector = Coordinate(if (treasureTileCircle) 3 else 0, length)
                val vector = edge0Vector.rotated(edge * angle + angleOffset)
                return tileCenterPos - vector
            }

            if (tile is TreasureTile) {
                val gems = tile.gems
                val gemPositions = tile.gemPositions

                if (gemPositions != null) {
                    for ((edge, gem) in gemPositions) {
                        val isCircle = (pos == AxialPos(0, -4) && edge == 3)
                                || (pos == AxialPos(4, -4) && edge == 4)
                                || (pos == AxialPos(4, 0) && edge == 5)
                                || (pos == AxialPos(0, 4) && edge == 0)
                                || (pos == AxialPos(-4, 4) && edge == 1)
                                || (pos == AxialPos(-4, 0) && edge == 2)
                        val gemPosition = edgeToPos(edge, isCircle) - Coordinate(gemSize / 2, gemSize / 2)
                        val boardGem = TokenView(
                            posX = gemPosition.xCoord,
                            posY = gemPosition.yCoord,
                            width = gemSize,
                            height = gemSize,
                            visual = gemToImageVisual(gem),
                        )
                        boardGems.add(boardGem)
                    }
                } else if (!gems.isNullOrEmpty()) {
                    val firstGem = gems[0]
                    val firstGemCoord = tileCenterPos - Coordinate(gemSize / 2, gemSize / 2)
                    val firstBoardGem = TokenView(
                        posX = firstGemCoord.xCoord,
                        posY = firstGemCoord.yCoord,
                        width = gemSize,
                        height = gemSize,
                        visual = gemToImageVisual(firstGem),
                    )
                    boardGems.add(firstBoardGem)

                    for (i in 1..gems.lastIndex) {
                        val gemPosition = edgeToPos(i-1, false, angle = 360.0 / 5, angleOffset = 0.0) -
                                Coordinate(gemSize / 2, gemSize / 2)
                        val boardGem = TokenView(
                            posX = gemPosition.xCoord,
                            posY = gemPosition.yCoord,
                            width = gemSize,
                            height = gemSize,
                            visual = gemToImageVisual(gems[i]),
                        )
                        boardGems.add(boardGem)
                    }
                }
            } else if (tile is RouteTile) {
                for ((edge, gem) in tile.gemPositions) {
                    val gemPosition = edgeToPos(edge, false) - Coordinate(gemSize / 2, gemSize / 2)
                    val boardGem = TokenView(
                        posX = gemPosition.xCoord,
                        posY = gemPosition.yCoord,
                        width = gemSize,
                        height = gemSize,
                        visual = gemToImageVisual(gem),
                    )
                    boardGems.add(boardGem)
                }
            }
        }

        addComponents(*boardGems.toTypedArray())
    }

    /**
     *  Sets the corresponding gate colors for each game mode.
     */
    private fun initializeGateColors() {
        val game = rootService.currentGame
        checkNotNull(game)

        val playerCount = game.currentPlayers.size

        if (playerCount == 2) {
            // Set the first gate colors
            gateOneOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateOneTwo.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            // Set the Second gate colors
            gateTwoOne.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            gateTwoTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the third gate colors
            gateThreeOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateThreeTwo.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            // Set the fourth gate colors
            gateFourOne.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            gateFourTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the fifth gate colors
            gateFiveOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateFiveTwo.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            // Set the sixth gate colors
            gateSixOne.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            gateSixTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
        }
        if (playerCount == 3 && game.sharedGates) {
            // Set the first gate colors
            gateOneOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateOneTwo.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            // Set the Second gate colors
            gateTwoOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateTwoTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the third gate colors
            gateThreeOne.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            gateThreeTwo.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            // Set the fourth gate colors
            gateFourOne.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            gateFourTwo.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            // Set the fifth gate colors
            gateFiveOne.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            gateFiveTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the sixth gate colors
            gateSixOne.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            gateSixTwo.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")

        }
        if (playerCount == 3 && !game.sharedGates) {
            // Set the first gate colors
            gateOneOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateOneTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the Second gate colors
            gateTwoOne.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            gateTwoTwo.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            // Set the third gate colors
            gateThreeOne.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            gateThreeTwo.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            // Set the fourth gate colors
            gateFourOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateFourTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the fifth gate colors
            gateFiveOne.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            gateFiveTwo.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            // Set the sixth gate colors
            gateSixOne.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            gateSixTwo.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")

        }
        if (playerCount == 4) {
            // Set the first gate colors
            gateOneOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateOneTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the Second gate colors
            gateTwoOne.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            gateTwoTwo.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            // Set the third gate colors
            gateThreeOne.visual = ImageVisual("color_${game.currentPlayers[0].color.toString().lowercase()}.png")
            gateThreeTwo.visual = ImageVisual("color_${game.currentPlayers[3].color.toString().lowercase()}.png")
            // Set the fourth gate colors
            gateFourOne.visual = ImageVisual("color_${game.currentPlayers[3].color.toString().lowercase()}.png")
            gateFourTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the fifth gate colors
            gateFiveOne.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            gateFiveTwo.visual = ImageVisual("color_${game.currentPlayers[1].color.toString().lowercase()}.png")
            // Set the sixth gate colors
            gateSixOne.visual = ImageVisual("color_${game.currentPlayers[2].color.toString().lowercase()}.png")
            gateSixTwo.visual = ImageVisual("color_${game.currentPlayers[3].color.toString().lowercase()}.png")

        }
    }

    override fun refreshAfterNewGame() {
        val game = rootService.currentGame
        checkNotNull(game)

        // Set the player Colors and if playerCount = 3/4 show the third/fourth player colors and heldTileView
        firstPlayerColor.apply {
            visual = ImageVisual("color_${(game.currentPlayers[0].color).toString().lowercase()}.png")
        }
        firstPlayerNameLabel.text = game.currentPlayers[0].name
        secondPlayerColor.apply {
            visual = ImageVisual("color_${(game.currentPlayers[1].color).toString().lowercase()}.png")
        }
        secondPlayerNameLabel.text = game.currentPlayers[1].name
        if (game.currentPlayers.size > 2) {
            thirdPlayerColor.apply {
                visual = ImageVisual("color_${(game.currentPlayers[2].color).toString().lowercase()}.png")
            }
            thirdPlayerNameLabel.text = game.currentPlayers[2].name
            thirdPlayerSapphires.isVisible = true
            thirdPlayerEmeralds.isVisible = true
            thirdPlayerAmbers.isVisible = true
            thirdPlayerHeldTileView.isVisible = true
        }
        if (game.currentPlayers.size == 4) {
            fourthPlayerColor.apply {
                visual = ImageVisual("color_${(game.currentPlayers[3].color).toString().lowercase()}.png")
            }
            fourthPlayerNameLabel.text = game.currentPlayers[3].name
            fourthPlayerSapphires.isVisible = true
            fourthPlayerEmeralds.isVisible = true
            fourthPlayerAmbers.isVisible = true
            fourthPlayerHeldTileView.isVisible = true
        }
        // Initialize the board, show the heldTiles of all players and set the gem count to 0
        initializeGameBoard()
        initializeGateColors()
        updateHeldTiles()
        updatePlayerGems()
    }

    override fun refreshAfterPlaceTile(position: AxialPos) {
        val game = rootService.currentGame
        checkNotNull(game)

        // Get the GUI gameBoard and update the Tiles visual there
        val gameBoardTile = gameBoard[position.q, position.r]
        checkNotNull(gameBoardTile)
        val placedTile = game.currentBoard[position] as? RouteTile
        checkNotNull(placedTile)
        gameBoardTile.visual = ImageVisual("${placedTile.tileType.toString().lowercase()}_${placedTile.rotation}.png")
        // Add the newly placed tile to the tileMap with its corresponding View Element
        tileMap.add(placedTile, gameBoardTile)
        updateBoardGems()
        updatePlayerGems()
        unmarkTiles()
    }

    override fun refreshAfterChangePlayer() {
        val game = rootService.currentGame
        checkNotNull(game)
        // list of Y positions
        val posYList =
            listOf(
                firstPlayerColor.posY - 10,
                secondPlayerColor.posY - 10,
                thirdPlayerColor.posY - 10,
                fourthPlayerColor.posY - 10
            )
        // find current pos
        val playerCount = game.currentPlayers.size
        var currentPlayerPos = 0
        for (i in 0 until playerCount) {
            if (posYList[i] == currentPlayerIndicator.posY)
                currentPlayerPos = i
        }
        // Update the indicators position
        currentPlayerIndicator.posY = posYList[(currentPlayerPos + 1) % playerCount]

        // Update held tiles
        updateHeldTiles()

        // Set the currentPlayerHeldTile rotation back to 30
        currentPlayerHeldTileView.rotation = 30.0
    }


    init {
        background = backgroundOverlay

        addComponents(
            boardBackground,
            gameBoard,
            undoButton,
            redoButton,
            rulesButton,
            saveButton,
            quitButton,
            rightTurnButton,
            leftTurnButton,
            currentPlayerHeldTileView,
            blueGemIndicator,
            greenGemIndicator,
            yellowGemIndicator,
            currentPlayerIndicator,
            firstPlayerColor,
            secondPlayerColor,
            thirdPlayerColor,
            fourthPlayerColor,
            firstPlayerSapphires,
            firstPlayerEmeralds,
            firstPlayerAmbers,
            secondPlayerSapphires,
            secondPlayerEmeralds,
            secondPlayerAmbers,
            thirdPlayerSapphires,
            thirdPlayerEmeralds,
            thirdPlayerAmbers,
            fourthPlayerSapphires,
            fourthPlayerEmeralds,
            fourthPlayerAmbers,
            firstPlayerHeldTileView,
            secondPlayerHeldTileView,
            thirdPlayerHeldTileView,
            fourthPlayerHeldTileView,
            firstPlayerNameLabel,
            secondPlayerNameLabel,
            thirdPlayerNameLabel,
            fourthPlayerNameLabel,
            gateOneOne,
            gateOneTwo,
            gateTwoOne,
            gateTwoTwo,
            gateThreeOne,
            gateThreeTwo,
            gateFourOne,
            gateFourTwo,
            gateFiveOne,
            gateFiveTwo,
            gateSixOne,
            gateSixTwo
        )

        onSceneShown = {
            val isNetworkGame = rootService.networkService.connectionState != ConnectionState.DISCONNECTED
            saveButton.apply {
                isVisible = !isNetworkGame
                isDisabled = isNetworkGame
            }
            undoButton.apply {
                isVisible = !isNetworkGame
                isDisabled = isNetworkGame
            }
            redoButton.apply {
                isVisible = !isNetworkGame
                isDisabled = isNetworkGame
            }
        }

        firstPlayerColor.apply {
            onMouseClicked = {
                showFirstPlayerName = !showFirstPlayerName
                if (showFirstPlayerName) {
                    firstPlayerNameLabel.isVisible = true
                    firstPlayerSapphires.isVisible = false
                    firstPlayerEmeralds.isVisible = false
                    firstPlayerAmbers.isVisible = false
                } else {
                    firstPlayerNameLabel.isVisible = false
                    firstPlayerSapphires.isVisible = true
                    firstPlayerEmeralds.isVisible = true
                    firstPlayerAmbers.isVisible = true
                }

            }
        }

        secondPlayerColor.apply {
            onMouseClicked = {
                showSecondPlayerName = !showSecondPlayerName
                if (showSecondPlayerName) {
                    secondPlayerNameLabel.isVisible = true
                    secondPlayerSapphires.isVisible = false
                    secondPlayerEmeralds.isVisible = false
                    secondPlayerAmbers.isVisible = false
                } else {
                    secondPlayerNameLabel.isVisible = false
                    secondPlayerSapphires.isVisible = true
                    secondPlayerEmeralds.isVisible = true
                    secondPlayerAmbers.isVisible = true
                }

            }
        }

        thirdPlayerColor.apply {
            onMouseClicked = {
                showThirdPlayerName = !showThirdPlayerName
                if (showThirdPlayerName) {
                    thirdPlayerNameLabel.isVisible = true
                    thirdPlayerSapphires.isVisible = false
                    thirdPlayerEmeralds.isVisible = false
                    thirdPlayerAmbers.isVisible = false
                } else {
                    thirdPlayerNameLabel.isVisible = false
                    thirdPlayerSapphires.isVisible = true
                    thirdPlayerEmeralds.isVisible = true
                    thirdPlayerAmbers.isVisible = true
                }

            }
        }

        fourthPlayerColor.apply {
            onMouseClicked = {
                showFourthPlayerName = !showFourthPlayerName
                if (showFourthPlayerName) {
                    fourthPlayerNameLabel.isVisible = true
                    fourthPlayerSapphires.isVisible = false
                    fourthPlayerEmeralds.isVisible = false
                    fourthPlayerAmbers.isVisible = false
                } else {
                    fourthPlayerNameLabel.isVisible = false
                    fourthPlayerSapphires.isVisible = true
                    fourthPlayerEmeralds.isVisible = true
                    fourthPlayerAmbers.isVisible = true
                }

            }
        }

        currentPlayerHeldTileView.apply {
            onMouseClicked = {
                markForbiddenTiles = !markForbiddenTiles
                if (markForbiddenTiles)
                    checkTiles()
                else
                    unmarkTiles()
            }
        }
    }
}

