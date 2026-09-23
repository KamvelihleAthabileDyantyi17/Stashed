package com.example.stashed.utils
import java.util.Locale

object CurrencyUtils {
    fun format(amount: Double): String = "R %,.2f".format(Locale.ENGLISH, amount)
}