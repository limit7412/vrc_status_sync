import serverless.Lambda
import notify.Usecase

@main def main = sys.env("ENV") match {
  case "local" => handler("1970-01-01T00:00:00Z")
  case _ =>
    serverless.Lambda
      .Handler[serverless.Lambda.CloudWatchScheduledEventRequest](
        "handler",
        (event) => {
          handler(event.time)
          serverless.Lambda.Response(200, "ok")
        }
      )
}

def handler(time: String) = notify.Usecase.check
