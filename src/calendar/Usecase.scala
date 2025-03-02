package calendar

import upickle.default._
import java.time.LocalDate

object Usecase {
  def getTodayVRCEvents = {
    val calendarId = sys.env("GOOGLE_CALENDAR_ID")

    val events = EventRepository
      .findByDay(calendarId, LocalDate.now())
      // // テスト用に指定の日付を設定
      // .findByDay(calendarId, LocalDate.of(2025, 3, 1))
      .filter(event => event.getSummary.contains("[VRC:"))
      .map(event => {
        val summaryText = event.getSummary.split(":")(1).split("\\]")
        val vrcStatus = summaryText(0)
        val eventName = summaryText(1)

        (vrcStatus, eventName, event.getStart().getDateTime())
      })

    events
  }
}
