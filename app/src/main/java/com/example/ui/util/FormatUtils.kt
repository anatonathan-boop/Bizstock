package com.example.ui.util

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val frenchFormat = NumberFormat.getInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    fun formatFcfa(amount: Double): String {
        val rounded = amount.toLong()
        return "${frenchFormat.format(rounded)} FCFA"
    }

    fun formatNumber(number: Number): String {
        return frenchFormat.format(number.toLong())
    }
}
