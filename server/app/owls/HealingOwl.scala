package owls
import play.api.libs.json.JsValue
import models.{StudentDao}
import game.Stats

class HealingOwl(studentDao: StudentDao) extends Owl {
  val power = 20
  override def apply(studentId: Long, payload: JsValue): Either[String, String] = {
    studentDao.findForUser(studentId) match {
      case Some(s) if s.hp == Stats.MaxHpPerLevel(s.level) =>
        Left("Мы, совы, не умеем лечить здоровых.")
      case Some(s) if Stats.MaxHpPerLevel(s.level) - s.hp <= power =>
        studentDao.updateHp(studentId, Stats.MaxHpPerLevel(s.level) - s.hp)
        Right("Готовченко")
      case Some(s) =>
        studentDao.updateHp(studentId, power)
        Right("Готовченко")
      case _ =>
        Left("Что-то пошло не так. Удачи в бою")
    }
  }
}

