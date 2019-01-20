package models

import db.{DbCtx, Pagination}
import services.SuggestionService.{CreatureApproved, NoteApproved}
/* Entities */

case class Creature(name: String, power: Int, totalHp: Int, level: Int, isApproved: Boolean = false, id: Long = -1)

case class CreatureFight(studentId: Long, creatureId: Long, creatureHp: Int)

case class CreatureHandlingSkill(creatureId: Long, studentId: Long, modifier: Int)

/* DTOs */

case class OpponentCreature(id: Long, power: Int, level: Int, hp: Int, studentsSkill: Option[Int])

case class StudentCreatureHandlingSkill(creatureName: String, modifier: Int)

case class CreatureForApproval(id: Long, name: String, notes: Seq[NoteForApproval])

class CreatureDao(val db: DbCtx) {
  import db._

  def create(creature: Creature)(depsTransaction: Creature => Unit): Creature =
    transaction {
      val newCreature = creature.copy(id = run(query[Creature].insert(lift(creature)).returning(_.id)))
      depsTransaction(newCreature)
      newCreature
    }

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

  def removeFightWithStudentUpdatingSkill(studentId: Long, skillDelta: Short): Unit =
    run(infix"""SELECT creature_fights_end_updating_skill(${lift(studentId)}, ${lift(skillDelta)})""".as[Query[String]])

  def loadStudentSkills(studentId: Long, pagination: Pagination): Seq[StudentCreatureHandlingSkill] =
    run(
      query[CreatureHandlingSkill]
        .join(query[Creature]).on {
          case (chs, c) => c.id == chs.creatureId && chs.studentId == lift(studentId)
        }
        .map {
          case (chs, c) => StudentCreatureHandlingSkill(c.name, chs.modifier)
        }
        .sortBy(s => (s.modifier, s.creatureName))(Ord(Ord.desc, Ord.asc))
        .paginate(lift(pagination))
    )

  def loadFirstUnapproved(): Option[CreatureForApproval] =
    run(
      query[Creature]
        .filter(!_.isApproved)
        .sortBy(_.id)(Ord.asc)
        .take(1)
        .map(c => (c.id, c.name))
    ).headOption.map { case (id, name) =>
      val notes = run(
        query[Note]
          .filter(_.creatureId.exists(_ == lift(id)))
          .map(n => NoteForApproval(n.id, n.stage, n.textGender, n.text))
      )
      CreatureForApproval(id, name, notes)
    }
  def applyApproved(ac: CreatureApproved): Unit = {
    if (!ac.isApproved) {
      ac.notes.foreach {note =>
        run(query[Note].filter(_.id == lift(note.id)).delete) 
      }
      run(query[Creature].filter(_.id == lift(ac.id)).delete)
    } else {
      ac.notes.foreach { note =>
        if (note.isApproved)
          run(
            query[Note]
              .filter(_.id == lift(note.id))
              .update(_.text -> lift(note.text), _.isApproved -> true)
            )
        else run(query[Note].filter(_.id == lift(note.id)).delete)
      }
      run(
        query[Creature]
          .filter(_.id == lift(ac.id))
          .update(_.name -> lift(ac.name), _.isApproved -> true)
        )
    }
  }
}
