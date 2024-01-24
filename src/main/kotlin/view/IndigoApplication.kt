package view

import entity.Color
import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * The main application class for the Indigo board game.
 *
 * This class manages various scenes, including the main game scene, launch menu scene,
 * rules scene, pre-game menu scene,new game menu scene, and end game menu scene.
 * It also handles the initialization and interactions between these scenes.
 *
 * @property rootService The root service for accessing the current game and other logic.
 * @property mainGameScene Main scene where the game is played.
 * @property launchMenuScene Menu scene that gets displayed right after launching the application.
 * @property rules Menu scene that shows the rules.
 * @property preGameMenuScene Menu scene that gets displayed after the player clicks on New Game.
 * @property newGameMenuScene Menu scene where the players can enter their name and configure the game mode,
 *           as well as AI Style.
 * @property endGameMenuScene Menu scene where the players see their points and who won the game.
 */
class IndigoApplication : BoardGameApplication("Indigo"), Refreshable {

    /**
     * rootService for accessing the current game and other logic.
     */
    private val rootService = RootService()

    /**
     *  Main scene where the game is played.
     */
    private val mainGameScene = MainGameScene(rootService)

    /**
     * Menu scene that gets displayed right after launching the application.
     */
    private val launchMenuScene = LaunchMenuScene(rootService)

    /**
     * Menu Scene that shows the rules
     */
    private val rules = RulesScene()

    /**
     * Menu Scene that gets displayed after the player clicks on New Game.
     */
    private val preGameMenuScene = PreGameMenuScene(rootService)

    /**
     * Menu scene where the players can enter their name and configure the game mode, as well as AI Style.
     */
    private val newGameMenuScene = NewGameMenuScene(rootService)

    /**
     * Menu scene where the players see their points and who won the game.
     */
    private val endGameMenuScene = EndGameScene()

    init {
        rootService.addRefreshables(
            this,
            launchMenuScene,
            preGameMenuScene,
            newGameMenuScene,
            mainGameScene,
            endGameMenuScene
        )

        // open the launchMenuScene
        this.showMenuScene(launchMenuScene)

        preGameMenuScene.startButton.onMouseClicked = {
            if (preGameMenuScene.gameMode == GameMode.HOST) {
                val gameID = preGameMenuScene.gameIDField.text.trim().ifEmpty { null }
                val playerName = preGameMenuScene.playerNameField.text.trim()
                rootService.networkService.hostGame("game23d", playerName, gameID)
            } else if (preGameMenuScene.gameMode == GameMode.JOIN) {
                val gameID = preGameMenuScene.gameIDField.text.trim()
                val playerName = preGameMenuScene.playerNameField.text.trim()
                rootService.networkService.joinGame("game23d", playerName, gameID)
            }

            newGameMenuScene.gameMode = preGameMenuScene.gameMode
            this.showMenuScene(newGameMenuScene)
        }
        newGameMenuScene.returnButton.onMouseClicked = {
            if (newGameMenuScene.gameMode != GameMode.LOCAL) {
                rootService.networkService.disconnect()
            }

            this.showMenuScene(preGameMenuScene)
        }
        newGameMenuScene.startRoundButton.onMouseClicked = {
            this.hideMenuScene()

            // Find the correct player values and start a new game
            val playerList = mutableListOf<Player>()
            val aiSpeed = newGameMenuScene.aiSpeed
            val sharedGates = newGameMenuScene.sharedGates
            for (i in 0 until newGameMenuScene.playerCount) {
                val isSmartAi =  newGameMenuScene.actualPlayerTypes[i] == "smart"
                val isAi = isSmartAi || newGameMenuScene.actualPlayerTypes[i] == "random"
                val color = when (newGameMenuScene.actualPlayerColors[i]) {
                    "white" -> Color.WHITE
                    "red" -> Color.RED
                    "blue" -> Color.BLUE
                    "purple" -> Color.PURPLE
                    else -> error("unexpected color")
                }

                playerList.add(
                    Player(
                        newGameMenuScene.actualPlayerNames[i],
                        color,
                        isAi,
                        isSmartAi,
                        heldTile = null
                    )
                )
            }

            when (newGameMenuScene.gameMode) {
                GameMode.LOCAL -> {
                    rootService.gameService.startGame(playerList, aiSpeed, sharedGates = sharedGates)
                }

                GameMode.HOST -> {
                    rootService.networkService.startNewHostedGame(
                        players = playerList,
                        sharedGates = sharedGates,
                        aiMoveMilliseconds = aiSpeed,
                    )
                }

                else -> {
                    error("Cannot start in Join mode")
                }
            }

            this.showGameScene(mainGameScene)
        }
        launchMenuScene.newGameButton.onMouseClicked = { this.showMenuScene(preGameMenuScene) }
        launchMenuScene.loadGameButton.onMouseClicked = {
            this.hideMenuScene()
            rootService.gameService.load()
            this.showGameScene(mainGameScene)
        }

        mainGameScene.rulesButton.onMouseClicked = { this.showMenuScene(rules) }
        mainGameScene.quitButton.onMouseClicked = { exit() }

        rules.returnButton.onMouseClicked = { this.hideMenuScene() }

        endGameMenuScene.quitButton.onMouseClicked = { exit() }
        endGameMenuScene.startButton.onMouseClicked = { this.showMenuScene(preGameMenuScene) }
    }

    override fun refreshAfterNewGame() {
        this.hideMenuScene()
        this.showGameScene(mainGameScene)
    }

    override fun refreshAfterEndGame(players: List<Player>) {
        this.showMenuScene(endGameMenuScene)
    }

}