package com.example.stashed.utils

import java.util.Calendar

object DateUtils {

    /** Returns (startOfMonth, endOfMonth) epoch millis for the current calendar month. */
    fun currentMonthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return start to end
    }

    /** Format epoch millis as "DD MMM YYYY" */
    fun formatDate(millis: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        val months = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        return "${cal.get(Calendar.DAY_OF_MONTH)} ${months[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }

    /** Format epoch millis as "MMM YYYY" */
    fun formatMonthYear(millis: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        val months = arrayOf("January","February","March","April","May","June",
            "July","August","September","October","November","December")
        return "${months[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }
}
