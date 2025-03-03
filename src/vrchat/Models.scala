package vrchat

import upickle.default._

object Models {
  case class User(
      id: String,
      displayName: String,
      username: String,
      bio: Option[String] = None,
      currentAvatarImageUrl: Option[String] = None,
      currentAvatarThumbnailImageUrl: Option[String] = None,
      status: Status,
      statusDescription: String
  ) derives ReadWriter
}

enum Status(val value: String) {
  case JOIN_ME extends Status("join me")
  case ACTIVE extends Status("active")
  case ASK_ME extends Status("ask me")
  case BUSY extends Status("busy")
  case OFFLINE extends Status("offline")
}

object Status {
  implicit val rw: ReadWriter[Status] = readwriter[String].bimap[Status](
    status => status.value,
    str => Status.values.find(_.value == str).getOrElse(Status.OFFLINE)
  )
}
