package slack

import upickle.default._

object Models {
  case class Attachment(
      fallback: String = "",
      author_name: String = "",
      author_icon: String = "",
      author_link: String = "",
      pretext: String = "",
      color: String = "",
      title: String = "",
      title_link: String = "",
      text: String = "",
      footer: String = "",
      footerIcon: String = ""
  ) derives ReadWriter

  case class Post(
      attachments: List[Attachment]
  ) derives ReadWriter

}
