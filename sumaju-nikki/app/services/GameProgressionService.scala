package services

import models.{Character, CharacterDao}

import scala.concurrent.Future

class GameProgressionService(val characterDao: CharacterDao) {
  def pendingUpdates(count: Int): Seq[Character] =
    characterDao.findPendingTurnUpdates(count)

  def performUpdate(character: Character): Future[Unit] = ???
}
