import serverless.Lambda
import status.Usecase

@main def main = sys.env("ENV") match {
  case "local" => handler()
  case _ =>
    serverless.Lambda
      .Handler[serverless.Lambda.CloudWatchScheduledEventRequest](
        "handler",
        (event) => {
          try {
            handler()
            serverless.Lambda.Response(200, "ok")
          } catch {
            case e: Exception =>
              val message = "エラーみたい…確認してみよっか"
              val attachment = slack.Models.Attachment(
                fallback = message,
                pretext = s"<@${sys.env("SLACK_ID")}> ${message}",
                color = "#EB4646",
                title = e.getMessage,
                text = e.getStackTrace().mkString("\n"),
                footer = s"github_notifications_slack (${sys.env("ENV")})"
              )
              slack.PostRepository.sendAttachment(attachment)

              serverless.Lambda.Response(500, "error")
          }
        }
      )
}

def handler() = status.Usecase.check
