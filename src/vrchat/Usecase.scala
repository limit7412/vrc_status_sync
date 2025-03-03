package vrchat

import upickle.default._
import java.time.LocalDate

object Usecase {
  def setStatus(status: Status, statusDescription: String) = {
    val userID = sys.env("VRC_USER_ID")

    val finalStatus = if (status == null) {
      val user = UserRepository.findByUserID(userID)
      user.status
    } else {
      status
    }

    UserRepository.updateStatus(userID, finalStatus, statusDescription)
  }
}
