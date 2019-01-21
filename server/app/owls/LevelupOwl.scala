package owls
import play.api.libs.json.JsValue
import models.{StudentDao}

class LevelUpOwl(studentDao: StudentDao) extends Owl {
  override def apply(studentId: Long, payload: JsValue): Either[String, String] = {
    studentDao.findForUser(studentId) match {
      case Some(s) =>
        studentDao.levelUp(studentId)
        Right("Поздравляем с закрытой сессией!")
      case _ =>
        Left("Что-то пошло не так, вы не переведены")
    }
  }
}
