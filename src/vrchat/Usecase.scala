package vrchat

import upickle.default._
import java.time.LocalDate

object Usecase {
  def setStatus(status: Status, statusDescription: String) = {
    val userID = sys.env("VRC_USER_ID")
    var token = util.VRCCookie.getObject()

    val user =
      try {
        UserRepository.findByUserID(token, userID)
      } catch {
        // 失敗したら再ログインしてcookieを更新する
        case e: Exception =>
          val newToken = AuthRepository.login()
          util.VRCCookie.putObject(newToken)
          token = newToken
          UserRepository.findByUserID(token, userID)
      }

    val finalStatus = if (status == null) {
      user.status
    } else {
      status
    }

    UserRepository.updateStatus(token, userID, finalStatus, statusDescription)
  }
}
