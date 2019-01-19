package owls
import play.api.libs.json.JsValue
import models.StudentRelationshipDao

class StudentLetterOwl(relationshipDao: StudentRelationshipDao) extends Owl {
  override def apply(studentId: Long, payload: JsValue): Either[String, String] = {
    val receiverId = (payload \ "receiverId").as[Long]
    relationshipDao.createLetter(studentId, receiverId)
    ???
  }
}
