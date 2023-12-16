package view

import service.RootService
import tools.aqua.bgw.core.MenuScene

/**
 * MenuScene to configure all Players, pick a Gateway Setting for three players
 * and edit Ai Style and Speed
 *
 * @param rootService the current Root
 */

class NewGameMenuScene(private val rootService: RootService): MenuScene(), Refreshable {
}