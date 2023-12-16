package view

import service.RootService
import tools.aqua.bgw.core.MenuScene

/**
 * The scene shown after finished game.
 * Shows Player names and their Points.
 * Possesses two Button StartNewGame and Quit
 *
 * @param rootService the current Root
 */
class EndGameMenuScene(private val rootService: RootService): MenuScene(), Refreshable {
}