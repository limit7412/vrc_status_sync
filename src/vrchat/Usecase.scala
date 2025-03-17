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

    val finalStatusDescription =
      if (
        user.statusDescription.contains("OyasumiVR")
        || user.statusDescription.contains("＃") // ステータスはエスケープされる
      ) {
        user.statusDescription
      } else if (status == null && statusDescription == "") {
        user.status match {
          case Status.JOIN_ME => "何でも歓迎"
          case Status.ACTIVE  => "予定ないよ"
          case Status.ASK_ME  => "事情につき"
          case Status.BUSY    => "取り込み中"
          case _              => ""
        }
      } else {
        statusDescription
      }

    UserRepository.updateStatus(
      token,
      userID,
      finalStatus,
      finalStatusDescription
    )
  }
}
