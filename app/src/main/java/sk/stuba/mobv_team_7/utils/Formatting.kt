package sk.stuba.mobv_team_7.utils

import sk.stuba.mobv_team_7.http.DATE_FORMAT_POST
import sk.stuba.mobv_team_7.http.DATE_FORMAT_RESPONSE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_RESPONSE)

class FormattingUtils {

    companion object {
        fun _fancyDateFormatting(fromDate: String): String {
            var fromDateTime = LocalDateTime.parse(fromDate, formatter)
            var now = LocalDateTime.now()

            // fix negative units bug
            if (now.isBefore(fromDateTime)) {
                var tmp = fromDateTime
                fromDateTime = now
                now = tmp
            }

            val months = fromDateTime.until(now, ChronoUnit.MONTHS)
            if (months >= 12.toLong()) {
                val years = fromDateTime.until(now, ChronoUnit.YEARS)
                var s = "year"
                if (years > 1) {
                    s += "s"
                }
                return "$years $s ago"
            }
            if (months == 0.toLong()) {
                val days = fromDateTime.until(now, ChronoUnit.DAYS)
                if (days in 1..28) {
                    var s = "day"
                    if (days > 1) {
                        s += "s"
                    }
                    return "$days $s ago"
                }
                if (days == 0.toLong()) {
                    val hours = fromDateTime.until(now, ChronoUnit.HOURS)
                    if (hours in 1..23) {
                        var s = "hour"
                        if (hours > 1) {
                            s += "s"
                        }
                        return "$hours $s ago"
                    }
                    if (hours == 0.toLong()) {
                        val minutes = fromDateTime.until(now, ChronoUnit.MINUTES)
                        var s = "minute"
                        if (minutes > 1) {
                            s += "s"
                        }
                        return "$minutes $s ago"
                    }
                }
            } else {
                return "$months month(s) ago"
            }

            return "... one time, next time"
        }

        fun dateFormatting(fromDate: String, wannaBeFancyFlag: Boolean): String {
            return if (wannaBeFancyFlag) {
                _fancyDateFormatting(fromDate)
            } else {
                LocalDateTime.parse(fromDate, formatter).toLocalDate().format(DateTimeFormatter.ofPattern(
                    DATE_FORMAT_POST))
            }

        }
    }
}