package services

import models.{Character, CharacterDao, RoomDao}

class GameProgressionService(val characterDao: CharacterDao,
                             val roomDao: RoomDao) {
  def pendingUpdates(count: Int): Seq[Character] =
    characterDao.findPendingTurnUpdates(count)

  def performUpdate(character: Character): Unit = {
  }
}
