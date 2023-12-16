package view

import service.RootService
import tools.aqua.bgw.core.MenuScene

/**
 * MenuScene to choose a Gamestyle.
 * Possesses one Button Start
 */
class PreGameMenuScene(private val rootService: RootService): MenuScene(), Refreshable {
}