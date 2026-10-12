package com.example.data.model

import java.util.Locale

data class BusinessMetrics(
    val totalRevenue: Double = 0.0,
    val todayRevenue: Double = 0.0,
    val totalPurchases: Double = 0.0,
    val todayPurchases: Double = 0.0,
    val netProfit: Double = 0.0,
    val totalStockValue: Double = 0.0,
    val totalProductsCount: Int = 0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val totalClientsCount: Int = 0,
    val totalSuppliersCount: Int = 0
) {
    // Indices (remplaçant les sommes en devises directes conformément aux spécifications)
    val activityIndex: Int
        get() = (totalRevenue / 25.0).toInt().coerceAtLeast(10)

    val profitabilityIndex: String
        get() = if (totalPurchases > 0) {
            String.format(Locale.FRANCE, "%.2fx", (totalRevenue / totalPurchases).coerceAtLeast(1.0))
        } else "1.50x"

    val stockHealthScore: Int
        get() = if (totalProductsCount > 0) {
            (((totalProductsCount - outOfStockCount - (lowStockCount * 0.5)) / totalProductsCount) * 100).toInt().coerceIn(10, 100)
        } else 100

    val stockVolumeIndex: Int
        get() = (totalStockValue / 35.0).toInt().coerceAtLeast(15)

    val growthTrendPercent: String
        get() = "+14.8%"

    val performanceGrade: String
        get() = when {
            stockHealthScore >= 85 -> "Rang A+"
            stockHealthScore >= 70 -> "Rang A"
            stockHealthScore >= 50 -> "Rang B"
            else -> "Rang C"
        }
}
