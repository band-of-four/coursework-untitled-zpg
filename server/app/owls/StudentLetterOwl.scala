package owls
import play.api.libs.json.JsValue
import models.{StudentDao, StudentRelationshipDao}

class StudentLetterOwl(studentDao: StudentDao,
                       relationshipDao: StudentRelationshipDao) extends Owl {
  override def apply(studentId: Long, payload: JsValue): Either[String, String] = {
    val receiverName = (payload \ "receiver").as[String]
    studentDao.findByName(receiverName) match {
      case Some(receiver) if receiver.id != studentId =>
        relationshipDao.createLetter(studentId, receiver.id)
        Right("Сова взмыла в воздух и, хлопнув крыльями, скрылась за углом коридора...")
      case Some(sameStudent) =>
        Left("Ты хочешь отправить записку от " +
          s"${if (sameStudent.fem) "своей ученицы ей же?" else "своего ученика ему же?"} " +
          "Сова смотрит на тебя укоризненно, отказываясь куда-либо лететь.")
      case None =>
        Left(s"${receiverName}? Такое имя совам неизвестно.")
    }
  }
}
