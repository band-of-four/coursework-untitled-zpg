package owls
import play.api.libs.json.JsValue
import models.StudentLetterDao

class StudentLetterOwl(letterDao: StudentLetterDao) extends Owl { // TODO
  override def apply(studentId: Long, payload: JsValue): Unit = {
    val receiverId = (payload \ "receiverId").as[Long]
    letterDao.insert(studentId, receiverId)
  }
}
