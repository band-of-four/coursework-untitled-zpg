package owls
import play.api.libs.json.JsValue

class StageSkipOwl extends Owl {
  override def apply(studentId: Long, payload: JsValue): Either[String, String] = ???
}
