package view

import entity.AxialPos
import entity.Tile
import service.RootService
import tools.aqua.bgw.components.container.HexagonGrid
import tools.aqua.bgw.components.gamecomponentviews.HexagonView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.style.BackgroundRadius
import tools.aqua.bgw.style.BorderRadius
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
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
     *  Shows the first players color
     */
    private val secondPlayerColor = Label(
        posX = 30,
        posY = firstPlayerColor.posY + 80,
        width = 60,
        height = 60
    )

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
     *  Shows the first players color
     */
    private val fourthPlayerColor = Label(
        posX = 30,
        posY = thirdPlayerColor.posY + 80,
        width = 60,
        height = 60
    )

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
        text = "1",
        font = Font(
            size = 20.0,
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
        text = "2",
        font = Font(
            size = 20.0,
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
        text = "3",
        font = Font(
            size = 20.0,
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
        text = "1",
        font = Font(
            size = 20.0,
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
        text = "2",
        font = Font(
            size = 20.0,
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
        text = "3",
        font = Font(
            size = 20.0,
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
        text = "1",
        font = Font(
            size = 20.0,
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
        text = "2",
        font = Font(
            size = 20.0,
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
        text = "3",
        font = Font(
            size = 20.0,
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
        text = "1",
        font = Font(
            size = 20.0,
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
        text = "2",
        font = Font(
            size = 20.0,
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
        text = "3",
        font = Font(
            size = 20.0,
            fontStyle = Font.FontStyle.ITALIC,
            color = Color.WHITE,
            fontWeight = Font.FontWeight.BOLD
        )
    ).apply {
        isVisible = false
    }

    /**
     *  Rotates the heldTileView 60 Degrees to the right.
     */
    private val rightTurnButton = Button(
        width = 150,
        height = 150,
        posX = 300,
        posY = 900,
        visual = ImageVisual("arrow-right.png")
    ).apply {
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game)
            heldTileView.rotation += 60
            val currenTile = game.playerAtTurn.heldTile
            checkNotNull(currenTile)
            currenTile.rotation = (currenTile.rotation + 1) % 6
        }
    }

    /**
     *  Rotates the heldTileView 60 Degrees to the left.
     */
    private val leftTurnButton = Button(
        width = 150,
        height = 150,
        posX = 100,
        posY = 900,
        visual = ImageVisual("arrow-left.png")
    ).apply {
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game)
            heldTileView.rotation -= 60
            val currenTile = game.playerAtTurn.heldTile
            checkNotNull(currenTile)
            currenTile.rotation = (currenTile.rotation - 1) % 6
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
        posX = 2160 / 2,
        posY = 450,
        coordinateSystem = HexagonGrid.CoordinateSystem.AXIAL
    ).apply {
        rotation = 30.0
    }

    // background image with black overlay
    private val blackOverlay = ColorVisual(color = Color.black).apply { transparency = 0.7 }

    private val backgroundOverlay = CompoundVisual(children = listOf(ImageVisual("background.png"), blackOverlay))

    /**
     *  shows the heldTile of the current player.
     */
    private var heldTileView = HexagonView(
        posX = 160,
        posY = 700,
        size = 120,
        visual = ColorVisual(Color(235, 230, 188))
    ).apply {
        rotation = 30.0
        isDraggable = true
    }


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
                    refreshAfterPlaceTile(AxialPos(q, r))
                }

                onMouseEntered = {
                    scale = 1.2
                }

                onMouseExited = {
                    scale = 1.0
                }
            }
        }
        return hexagon
    }

    /**
     *  Updates the heldTileView to the current players held tile.
     */
    private fun updateHeldTile() {
        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.playerAtTurn
        println("before: ${player.heldTile?.tileType.toString().lowercase()}.png")
        heldTileView.visual = ImageVisual("${player.heldTile?.tileType.toString().lowercase()}.png")
        println("after: ${player.heldTile?.tileType.toString().lowercase()}.png")
    }

    /**
     *  Updates the counters for all player gems
     */
    private fun updatePlayerGems() {

    }

    /**
     *  Marks all Tiles that are not available for placement by making them opaque.
     */
    private fun checkTiles() {
        val size = 4
        for (q in -size..size) {
            for (r in -size..size) {
                if (q + r <= size && q + r >= -size) {
                    if (!rootService.playerService.checkPlacement(AxialPos(q, r))) {
                        gameBoard[q, r].apply {
                            opacity = 0.5
                        }
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
                    gameBoard[q, r].apply {
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
                        hexagon = getInitialTileView(q, r)
                        this[q, r] = hexagon
                        currentBoardTile = game.currentBoard[AxialPos(q, r)]
                        currentHexagonView = this[q, r]
                        checkNotNull(currentHexagonView)
                        if (currentBoardTile != null)
                            tileMap.add(currentBoardTile, currentHexagonView)
                    }
                }
            }
        }
    }

    override fun refreshAfterNewGame() {
        val game = rootService.currentGame
        checkNotNull(game)

        firstPlayerColor.apply {
            visual = ImageVisual("color_${(game.currentPlayers[0].color).toString().lowercase()}.png")
        }
        secondPlayerColor.apply {
            visual = ImageVisual("color_${(game.currentPlayers[1].color).toString().lowercase()}.png")
        }
        if (game.currentPlayers.size > 2) {
            thirdPlayerColor.apply {
                visual = ImageVisual("color_${(game.currentPlayers[2].color).toString().lowercase()}.png")
            }
            thirdPlayerSapphires.isVisible = true
            thirdPlayerEmeralds.isVisible = true
            thirdPlayerAmbers.isVisible = true
        }
        if (game.currentPlayers.size == 4) {
            fourthPlayerColor.apply {
                visual = ImageVisual("color_${(game.currentPlayers[3].color).toString().lowercase()}.png")
            }
            fourthPlayerSapphires.isVisible = true
            fourthPlayerEmeralds.isVisible = true
            fourthPlayerAmbers.isVisible = true
        }


        initializeGameBoard()
        // TESTING
        rootService.playerService.drawTile()
        updateHeldTile()
    }

    override fun refreshAfterPlaceTile(position: AxialPos) {
        val game = rootService.currentGame
        checkNotNull(game)
        val heldTileVisual = heldTileView.visual
        var placedTile: Tile?
        // TODO: DRAW TILE ENTFERNEN NACH MERGE
        rootService.playerService.drawTile()
        updateHeldTile()
        if (rootService.playerService.checkPlacement(position)) {
            // Place the tile on the service layer game board
            rootService.playerService.placeTile(position)

            // Get the GUI gameBoard and update the Tiles visual there
            val gameBoardTile = gameBoard[position.q, position.r]
            checkNotNull(gameBoardTile)
            gameBoardTile.visual = heldTileVisual
            placedTile = game.currentBoard[position]
            checkNotNull(placedTile)
            // Add the newly placed tile to the tileMap with its corresponding View Element
            tileMap.add(placedTile, gameBoardTile)
            rootService.playerService.drawTile()
            updateHeldTile()
        }
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
    }


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
            heldTileView,
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
            fourthPlayerAmbers
        )

        heldTileView.apply {
            onMouseEntered = {
                checkTiles()
            }
            onMouseExited = {
                unmarkTiles()
            }
        }
    }
}

