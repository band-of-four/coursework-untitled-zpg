package models

import db.DbCtx
import scala.util.Random

case class Spell(name: String, power: Int, spell_type: String)

class SpellDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Spell]("spells"))
  
  // Learn random spell from the library
  def getRandomSpell(): Spell = 
    Random.shuffle(run(schema)).head

}
