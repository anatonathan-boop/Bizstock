package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale
import java.util.UUID

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val companyId: String = "company_default",
    val sku: String = "",
    val name: String = "",
    val category: String = "",
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val currentStock: Int = 0,
    val minStockAlert: Int = 5,
    val unit: String = "Unité",
    val description: String = "",
    val imageUri: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val margin: Double
        get() = sellingPrice - purchasePrice

    val marginPercent: Double
        get() = if (purchasePrice > 0) ((sellingPrice - purchasePrice) / purchasePrice) * 100 else 0.0

    val isLowStock: Boolean
        get() = currentStock in 1..minStockAlert

    val isOutOfStock: Boolean
        get() = currentStock <= 0

    // Indices métier pour l'affichage (sans sommes directes en devise)
    val priceIndex: Int
        get() = (sellingPrice / 10.0).toInt().coerceAtLeast(1)

    val costIndex: Int
        get() = (purchasePrice / 10.0).toInt().coerceAtLeast(1)

    val marginRatio: String
        get() = if (purchasePrice > 0) String.format(Locale.FRANCE, "%.2fx", sellingPrice / purchasePrice) else "1.00x"

    val performanceRank: String
        get() = when {
            marginPercent >= 75 -> "Rang A+"
            marginPercent >= 50 -> "Rang A"
            marginPercent >= 25 -> "Rang B"
            else -> "Rang C"
        }
}
