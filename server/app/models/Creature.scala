package models

import db.DbCtx

/* Entities */

case class Creature(name: String, power: Int, totalHp: Int, level: Int, id: Long = -1)

case class CreatureFight(studentId: Long, creatureId: Long, creatureHp: Int)

case class CreatureHandlingSkill(creatureId: Long, studentId: Long, modifier: Int)

/* DTOs */

case class OpponentCreature(id: Long, power: Int, level: Int, hp: Int, studentsSkill: Option[Int])

class CreatureDao(val db: DbCtx) {
  import db._

  def findNearRoom(roomNumber: Long): Creature =
    run(
      query[Creature]
        .join(query[Room])
        .on((c, r) => c.level == r.level && r.number == lift(roomNumber))
        .map(_._1)
        .randomSort
        .take(1)
    ).head

  def createFightWith(studentId: Long, creature: Creature): Unit =
    run(
      query[CreatureFight]
        .insert(lift(CreatureFight(studentId, creature.id, creature.totalHp)))
    )

  def removeFightWith(studentId: Long): Unit =
    run(query[CreatureFight].filter(_.studentId == lift(studentId)).delete)

  def updateInFightWith(studentId: Long, creature: OpponentCreature): Unit =
    run(
      query[CreatureFight]
        .filter(_.studentId == lift(studentId))
        .update(_.creatureHp -> lift(creature.hp))
    )

  def findInFightWith(studentId: Long): OpponentCreature =
    run(
      query[Creature]
        .join(query[CreatureFight]).on {
          case (c, fights) => c.id == fights.creatureId && fights.studentId == lift(studentId)
        }
        .leftJoin(query[CreatureHandlingSkill]).on {
          case ((c, _), skills) => c.id == skills.creatureId && skills.studentId == lift(studentId)
        }
        .map {
          case ((c, fight), skill) => OpponentCreature(c.id, c.power, c.level, fight.creatureHp, skill.map(_.modifier))
        }
    ).head
}
