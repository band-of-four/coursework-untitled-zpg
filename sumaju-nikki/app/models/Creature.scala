package models

import db.DbCtx
import scala.util.Random

case class Creature(name: String, power: Int, hp: Int, level: Int)

class CreatureDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Creature]("creatures"))
  
  // Get creature for battle with student before room entering
  def getRandomCreatureByLevel(level: Int): Creature =
    Random.shuffle(run(schema.filter(_.level == lift(level)))).head

}
