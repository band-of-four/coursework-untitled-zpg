package models

import db.{DbCtx, Pagination}

/* Entities */

case class Creature(name: String, power: Int, totalHp: Int, level: Int, isApproved: Boolean = false, id: Long = -1)

case class CreatureFight(studentId: Long, creatureId: Long, creatureHp: Int)

case class CreatureHandlingSkill(creatureId: Long, studentId: Long, modifier: Int)

/* DTOs */

case class OpponentCreature(id: Long, power: Int, level: Int, hp: Int, studentsSkill: Option[Int])

case class StudentCreatureHandlingSkill(creatureName: String, modifier: Int)

class CreatureDao(val db: DbCtx) {
  import db._

  def findNearRoom(roomNumber: Long): Creature =
    run(
      query[Creature]
        .filter(_.isApproved)
        .join(query[Room])
        .on((c, r) => c.level == r.level && r.number == lift(roomNumber))
        .map(_._1)
        .takeRandom
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

  def loadStudentSkills(studentId: Long)(implicit pagination: Pagination): Seq[StudentCreatureHandlingSkill] =
    run(
      query[CreatureHandlingSkill]
        .join(query[Creature]).on {
          case (chs, c) => c.id == chs.creatureId && chs.studentId == lift(studentId)
        }
        .map {
          case (chs, c) => StudentCreatureHandlingSkill(c.name, chs.modifier)
        }
        .sortBy(s => (s.modifier, s.creatureName))(Ord(Ord.desc, Ord.asc))
        .paginate
    )
}
