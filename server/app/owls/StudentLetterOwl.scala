package owls
import play.api.libs.json.JsValue
import models.StudentRelationshipDao

class StudentLetterOwl(relationshipDao: StudentRelationshipDao) extends Owl {
  override def apply(studentId: Long, payload: JsValue): Unit = {
    val receiverId = (payload \ "receiverId").as[Long]
    relationshipDao.insert(studentId, receiverId)
  }
}
