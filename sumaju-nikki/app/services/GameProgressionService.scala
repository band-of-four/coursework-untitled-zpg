package services

import gamelogic.Travel
import models.{Character, CharacterDao, LessonDao, RoomDao}
import models.Character.{StageClub, StageFight, StageTravel}

class GameProgressionService(val characterDao: CharacterDao,
                             val roomDao: RoomDao,
                             val lessonDao: LessonDao) {
  def pendingUpdates(count: Int): Seq[Character] =
    characterDao.findPendingTurnUpdates(count)

  def performUpdate(character: Character): Unit = {
    character.stage match {
      case StageTravel => finishTravelling(character)
      case _ => ???
    }
  }

  def finishTravelling(character: Character): Unit = {
    val nearbyRooms = roomDao.preloadInRadius(character.currentRoom, Travel.TravelRadius)
    val attendance = lessonDao.buildAttendanceMap(character.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
    val nextRoom = Travel.pickNextRoom(character, nearbyRooms, attendance)
  }
}
