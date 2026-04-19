package com.example.stashed.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    private val randFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

    /** Format a Double as "R 1,234.56" */
    fun format(amount: Double): String = randFormat.format(amount)

    /** Short format removing symbol for compact display: "1 234.56" */
    fun formatShort(amount: Double): String {
        return String.format(Locale.US, "%.2f", amount)
    }
}
