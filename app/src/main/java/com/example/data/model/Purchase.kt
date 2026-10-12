package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val companyId: String = "company_default",
    val orderNumber: String = "",
    val supplierName: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "REÇU", // REÇU, EN_COURS, COMMANDÉ
    val date: Long = System.currentTimeMillis(),
    val itemsSummary: String = "",
    val itemsCount: Int = 1,
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val orderVolumeIndex: Int
        get() = (totalAmount / 30.0).toInt().coerceAtLeast(1)
}
