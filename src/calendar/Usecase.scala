package calendar

import upickle.default._
import java.time.LocalDate
import java.time.ZoneId
object Usecase {
  def getTodayVRCEvents = {
    val calendarId = sys.env("GOOGLE_CALENDAR_ID")

    val events = EventRepository
      .findByDay(calendarId, LocalDate.now(ZoneId.of("Asia/Tokyo")))
      // // テスト用に指定の日付を設定
      // .findByDay(calendarId, LocalDate.of(2025, 3, 1))
      .filter(event => event.getSummary.contains("[vrc:"))
      // 開始時刻が00:00:00以前のイベントは除外
      .filter(event =>
        event.getStart().getDateTime().getValue() > LocalDate.now(ZoneId.of("Asia/Tokyo")).atStartOfDay(ZoneId.of("Asia/Tokyo")).toInstant().toEpochMilli()
      )
      .map(event => {
        val summaryText = event.getSummary.split(":")(1).split("\\]")
        val vrcStatus = summaryText(0)
        val eventName = summaryText(1)

        (
          vrcStatus,
          eventName,
          event.getStart().getDateTime(),
          event.getEnd().getDateTime()
        )
      })

    events
  }
}
