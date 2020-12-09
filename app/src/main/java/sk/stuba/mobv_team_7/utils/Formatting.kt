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
            val fromDateTime = LocalDateTime.parse(fromDate, formatter)
            val now = LocalDateTime.now()

            val months = fromDateTime.until(now, ChronoUnit.MONTHS)
            if (months >= 12.toLong()) {
                val years = fromDateTime.until(now, ChronoUnit.YEARS)
                return "$years y"
            }
            if (months == 0.toLong()) {
                val days = fromDateTime.until(now, ChronoUnit.DAYS)
                if (days in 1..28) {
                    return "$days d"
                }
                if (days == 0.toLong()) {
                    val hours = fromDateTime.until(now, ChronoUnit.HOURS)
                    if (hours in 1..23) {
                        return "$hours h"
                    }
                    if (hours == 0.toLong()) {
                        val minutes = fromDateTime.until(now, ChronoUnit.MINUTES)
                        return "$minutes m"
                    }
                }
            } else {
                return "$months months"
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