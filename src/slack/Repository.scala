package slack

import sttp.client4.quick._
import upickle.default._

object PostRepository {
  private def sendPost(post: Models.Post) = {
    basicRequest
      .post(
        uri"${sys.env("WEBHOOK_URL")}"
      )
      .body(write(post))
      .send()
  }

  def sendAttachment(attachment: Models.Attachment) = {
    sendPost(Models.Post(List(attachment)))
  }

  def sendAttachments(attachments: List[Models.Attachment]) = {
    sendPost(Models.Post(attachments))
  }
}
