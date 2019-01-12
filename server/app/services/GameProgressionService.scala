package services

import game.{Fight, Heal, Travel}
import models._
import game.Fight._
import game.Travel._
import game.Study.StudyDuration
import utils.RandomEvent

class GameProgressionService(studentDao: StudentDao,
                             roomDao: RoomDao,
                             lessonDao: LessonDao,
                             creatureDao: CreatureDao,
                             spellDao: SpellDao,
                             noteDao: NoteDao) {
  def pendingUpdates(count: Int): Seq[StudentForUpdate] =
    studentDao.findPendingTurnUpdates(count)

  def performUpdate(student: StudentForUpdate): Unit =
    student.stage match {
      case Student.Stage.Travel =>
        if (RandomEvent(FightChance)) startFighting(student)
        else enterNextRoom(student)
      case Student.Stage.Fight => continueFighting(student)
      case Student.Stage.Lesson => finishStudying(student)
    }

  def startFighting(student: StudentForUpdate): Unit = {
    val opponent = creatureDao.findNearRoom(student.currentRoom)
    creatureDao.createFight(student.id, opponent)
    val stageNoteId = noteDao.findIdForFightWith(opponent.id)

    studentDao.setStage(student.id, student.currentRoom, stageNoteId, FightTurnDuration)
  }

  def continueFighting(student: StudentForUpdate): Unit = ???
//    val opponent = creatureDao.findInFight(student)
//    val turnOutcome = Fight.computeTurn(student, opponent, spells = spellDao.findLearned(student.id))
//
//    turnOutcome match {
//      case StudentWon =>
//        creatureDao.removeFight(student)
//      case StudentLost =>
//        val infirmary = roomDao.findClosest(Room.Kind.Infirmary, student.currentRoom)
//        studentDao.updateAfterLostFight(student, Heal.duration(student), infirmary)
//        creatureDao.removeFight(student)
//      case FightContinues(studentHp, creatureHp) =>
//        creatureDao.updateInFight(student, creatureHp)
//        studentDao.updateInFight(student, studentHp, FightTurnDuration)
//    }

  def finishStudying(student: StudentForUpdate): Unit = ???

  def enterNextRoom(student: StudentForUpdate): Unit = ???
//    val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, TravelRadius)
//    val attendance = lessonDao.buildAttendanceMap(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
//    Travel.pickDestination(student, nearbyRooms, attendance) match {
//      case AttendClass(newRoom) =>
//        studentDao.updateStage(student.id, newRoom, Student.Stage.Lesson, StudyDuration)
//      case VisitClub(newRoom) =>
//        ???
//      case ContinueTravelling(newRoom) =>
//        studentDao.updateStage(student.id, newRoom, Student.Stage.Travel, TravelDuration)
//    }
}
