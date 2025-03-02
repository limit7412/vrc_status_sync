package holiday

import sttp.client4.quick._
import upickle.default._
import sttp.model.Method
import sttp.model.Uri

// https://s-proj.com/utils/holiday.html
object CheckHolidayRepository {
  def get = {
    var response =
      basicRequest
        .method(Method.GET, uri"https://s-proj.com/utils/checkHoliday.php")
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(s"failed get checkHoliday api: ${e}")
        throw new Error(e)
      }
    }

    body match {
      case "holiday" => true
      case "else"    => false
      case "error" => {
        System.err.println(s"checkHoliday api return error")
        throw new Error("checkHoliday api return error")
      }
    }
  }
}
