package models

import db.DbCtx
import scala.util.Random

case class Creature(name: String, power: Int, hp: Int, level: Int, id: Long = -1)

class CreatureDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Creature]("creatures"))

  def findNearRoom(roomNumber: Long): Creature =
    run(
      schema
        .join(querySchema[Room]("rooms"))
        .on((c, r) => c.level == r.level && r.number == lift(roomNumber))
        .map(_._1)
        .randomSort
        .take(1)
    ).head
}
