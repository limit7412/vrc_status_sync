package vrchat

import upickle.default._
import java.time.LocalDate

object Usecase {
  def setStatus(status: Status, statusDescription: String) = {
    val userID = sys.env("VRC_USER_ID")

    val user = UserRepository.findByUserID(userID)
    println("user: " + user.toString())

    val res = UserRepository.updateStatus(userID, status, statusDescription)

    println("res: " + res.toString())
  }
}
