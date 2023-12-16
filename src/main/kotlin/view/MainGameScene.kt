package view

import service.RootService
import tools.aqua.bgw.core.BoardGameScene

/**
 * All actual gameplay happens in this Scene
 *
 * @param rootService the current Root
 */

class MainGameScene(private val rootService: RootService): BoardGameScene(), Refreshable {
}