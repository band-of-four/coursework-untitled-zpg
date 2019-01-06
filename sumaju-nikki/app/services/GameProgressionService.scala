package services

import game.{Fight, Travel}
import models._
import models.Student.{StageFight, StageStudy, StageTravel}
import game.Fight.{FightChance, FightTurnDuration}
import game.Travel._
import game.Study.StudyDuration
import utils.RandomEvent

class GameProgressionService(val studentDao: StudentDao,
                             val roomDao: RoomDao,
                             val lessonDao: LessonDao,
                             val creatureDao: CreatureDao,
                             val fightDao: FightDao) {
  def pendingUpdates(count: Int): Seq[Student] =
    studentDao.findPendingTurnUpdates(count)

  def performUpdate(student: Student): Unit =
    student.stage match {
      case StageTravel =>
        if (RandomEvent(FightChance)) startFighting(student)
        else enterNextRoom(student)
      case StageFight => continueFighting(student)
      case StageStudy => finishStudying(student)
    }

  def finishStudying(student: Student): Unit = ???

  def startFighting(student: Student): Unit = {
    val creature = creatureDao.findNearRoom(student.currentRoom)
    fightDao.create(CreatureFight(student.id, creature.id, creature.hp))
    studentDao.updateStage(student.id, student.currentRoom, StageFight, FightTurnDuration)
  }

  def continueFighting(student: Student): Unit = {
    val fight = fightDao.find(student)
    val studentAttack = Fight.studentAttack(
      student,
      creatureHandlingMod = creatureDao.findCreatureHandlingModifier(fight.creatureId, fight.studentId)
    )
    fight.creature_hp -= studentAttack
    if (fight.creature_hp <= 0)
      ???
    val creatureAttack = (1 - (r.nextFloat(LuckyRange) + s.luckSpell.power) *
      (fight.creature.power - s.defenceSpell.power)
    s.hp -= creatureAttack
    if (s.hp <= 0)
      ???
  }

  def enterNextRoom(student: Student): Unit = {
    val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, TravelRadius)
    val attendance = lessonDao.buildAttendanceMap(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
    Travel.pickDestination(student, nearbyRooms, attendance) match {
      case AttendClass(newRoom) =>
        studentDao.updateStage(student.id, newRoom, StageStudy, StudyDuration)
      case VisitClub(newRoom) =>
        ???
      case ContinueTravelling(newRoom) =>
        studentDao.updateStage(student.id, newRoom, StageTravel, TravelDuration)
    }
  }
}
