package slack

import sttp.client4.quick._
import upickle.default._

object PostRepository {
  private def sendAlertPost(post: Models.Post) = {
    basicRequest
      .post(
        uri"${sys.env("ALERT_WEBHOOK_URL")}"
      )
      .body(write(post))
      .send()
  }

  def sendAttachment(attachment: Models.Attachment) = {
    sendAlertPost(Models.Post(List(attachment)))
  }

  def sendAttachments(attachments: List[Models.Attachment]) = {
    sendAlertPost(Models.Post(attachments))
  }
}
