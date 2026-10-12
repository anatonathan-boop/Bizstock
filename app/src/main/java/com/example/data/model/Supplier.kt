package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val companyId: String = "company_default",
    val name: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val category: String = "Général",
    val activeOrders: Int = 0,
    val rating: Float = 4.8f,
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val reliabilityIndex: Int
        get() = (rating * 20).toInt().coerceIn(10, 100)

    val partnerGrade: String
        get() = when {
            rating >= 4.8f -> "Partenaire Certifié A+"
            rating >= 4.5f -> "Partenaire Validé A"
            else -> "Partenaire Standard"
        }
}
