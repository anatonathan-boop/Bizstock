package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

data class SaleItem(
    val productId: String,
    val productName: String,
    val productSku: String,
    val quantity: Int,
    val unitPrice: Double = 0.0,
    val unitPriceIndex: Int = 1,
    val totalLineIndex: Int = 1
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val companyId: String = "company_default",
    val invoiceNumber: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val totalAmount: Double = 0.0,
    val paymentStatus: String = "PAYÉE", // PAYÉE, EN_ATTENTE, ANNULÉE
    val paymentMethod: String = "Comptant", // Comptant, Carte Pro, Virement, Chèque
    val date: Long = System.currentTimeMillis(),
    val itemsSummary: String = "",
    val itemsCount: Int = 1,
    val itemsJson: String = "",
    val notes: String = "",
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val volumeIndex: Int
        get() = (totalAmount / 20.0).toInt().coerceAtLeast(1)

    val totalIndex: Int
        get() = (totalAmount / 10.0).toInt().coerceAtLeast(1)

    // Helper to decode itemsJson or parse itemsSummary for invoice display
    fun getParsedItems(): List<SaleItem> {
        if (itemsJson.isNotBlank()) {
            try {
                // Format: id|name|sku|qty|unitPrice|unitIndex|totalIndex;;...
                return itemsJson.split(";;").mapNotNull { line ->
                    val parts = line.split("|")
                    if (parts.size >= 7) {
                        SaleItem(
                            productId = parts[0],
                            productName = parts[1],
                            productSku = parts[2],
                            quantity = parts[3].toIntOrNull() ?: 1,
                            unitPrice = parts[4].toDoubleOrNull() ?: 0.0,
                            unitPriceIndex = parts[5].toIntOrNull() ?: 1,
                            totalLineIndex = parts[6].toIntOrNull() ?: 1
                        )
                    } else null
                }
            } catch (_: Exception) { }
        }

        // Fallback for seed data: synthesize from itemsSummary
        return listOf(
            SaleItem(
                productId = "seed",
                productName = if (itemsSummary.isNotBlank()) itemsSummary else "Articles vendus",
                productSku = "REF-VENTE",
                quantity = itemsCount.coerceAtLeast(1),
                unitPrice = if (itemsCount > 0) totalAmount / itemsCount else totalAmount,
                unitPriceIndex = if (itemsCount > 0) (totalAmount / (10.0 * itemsCount)).toInt().coerceAtLeast(1) else (totalAmount / 10.0).toInt().coerceAtLeast(1),
                totalLineIndex = totalIndex
            )
        )
    }
}
