package models

import db.DbCtx

case class Creature(name: String, power: Int, hp: Int, level: Int, id: Long = -1)

case class CreatureHandlingSkill(creatureId: Long, studentId: Long, modifier: Int)

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

  def findCreatureHandlingModifier(creatureId: Long, studentId: Long): Int =
    run(
      query[CreatureHandlingSkill]
        .filter(s => s.creatureId == lift(creatureId) && s.studentId == lift(studentId))
        .map(_.modifier)
    ).head
}
