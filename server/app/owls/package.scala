import play.api.libs.json.JsValue

package object owls {
  abstract class Owl {
    def apply(studentId: Long, payload: JsValue): Either[String, String]
  }
}
