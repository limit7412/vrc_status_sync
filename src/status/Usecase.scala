package status

import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Usecase {
  def check = {
    val events = calendar.Usecase.getTodayVRCEvents

    events.length > 0 match {
      case true => {
        // 現在時刻を取得
        val now = Instant.now().toEpochMilli()

        // 現在時刻に最も近いイベントを取得
        // 1. 現在進行中のイベント（開始時間 <= 現在 <= 終了時間）
        // 2. これから始まるイベントの中で最も近いもの
        val event = events
          .find(e => {
            val startTime = e._3.getValue()
            val endTime = e._4.getValue()
            startTime <= now && now <= endTime
          })
          .orElse(
            events
              .filter(e => e._3.getValue() > now)
              .minByOption(e => e._3.getValue() - now)
          )
          .getOrElse(events(0)) // イベントが見つからない場合は最初のイベントを使用

        val currentStatus = event._1
        val currentEventName = event._2
        val currentStartTime = event._3
        val currentEndTime = event._4

        val isEventActive =
          currentStartTime.getValue() <= now && now <= currentEndTime.getValue()

        // イベント期間中以外はnullを設定
        val newStatus = if (isEventActive) {
          vrchat.Status.values
            .find(_.value == currentStatus)
            .getOrElse(vrchat.Status.ACTIVE)
        } else {
          null
        }

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val startDateTime = LocalDateTime.ofInstant(
          Instant.ofEpochMilli(currentStartTime.getValue()),
          ZoneId.systemDefault()
        )
        val formattedStartTime = startDateTime.format(timeFormatter)

        val newStatusDescription = s"${formattedStartTime}～ ${currentEventName}"

        vrchat.Usecase.setStatus(newStatus, newStatusDescription)
      }
      case false => {
        vrchat.Usecase.setStatus(null, "予定無いよ")
      }
    }
  }
}
