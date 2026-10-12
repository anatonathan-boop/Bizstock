package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val companyId: String = "company_default",
    val name: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val balance: Double = 0.0,
    val totalOrders: Int = 0,
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val activityIndex: Int
        get() = (totalOrders * 10 + (balance / 50.0)).toInt().coerceAtLeast(5)

    val creditIndex: Int
        get() = (balance / 40.0).toInt()

    val tier: String
        get() = when {
            totalOrders >= 15 -> "Rang Platine"
            totalOrders >= 8 -> "Rang Or"
            else -> "Rang Argent"
        }
}
