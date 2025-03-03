import serverless.Lambda
import status.Usecase

@main def main = sys.env("ENV") match {
  case "local" => handler()
  case _ =>
    serverless.Lambda
      .Handler[serverless.Lambda.CloudWatchScheduledEventRequest](
        "handler",
        (event) => {
          handler()
          serverless.Lambda.Response(200, "ok")
        }
      )
}

def handler() = status.Usecase.check
