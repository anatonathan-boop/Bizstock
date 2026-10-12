package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val companyId: String = "company_default",
    val productId: String = "",
    val productName: String = "",
    val type: String = "AJUSTEMENT", // "ENTRÉE", "SORTIE", "AJUSTEMENT"
    val quantity: Int = 0,
    val reason: String = "Inventaire standard",
    val date: Long = System.currentTimeMillis(),
    val operator: String = "Admin BIZSTOCK"
)
