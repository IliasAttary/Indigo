package view

import service.RootService
import tools.aqua.bgw.core.MenuScene

/**
 * MenuScene to choose a Gamestyle.
 * Possesses one Button Start
 *
 * @param rootService the current Root
 */
class PreGameMenuScene(private val rootService: RootService): MenuScene(), Refreshable {
}