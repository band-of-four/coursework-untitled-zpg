package services

import models.{Character, CharacterDao}

class GameProgressionService(val characterDao: CharacterDao) {
  def pendingUpdates(count: Int): Seq[Character] =
    characterDao.findPendingTurnUpdates(count)

  def performUpdate(character: Character): Unit = {
  }
}
