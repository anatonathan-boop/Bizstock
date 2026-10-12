package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.StockMovement
import com.example.data.model.Supplier
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [
        Product::class,
        Customer::class,
        Supplier::class,
        Sale::class,
        Purchase::class,
        StockMovement::class
    ],
    version = 5,
    exportSchema = false
)
abstract class BizStockDatabase : RoomDatabase() {
    abstract fun bizStockDao(): BizStockDao

    companion object {
        @Volatile
        private var INSTANCE: BizStockDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BizStockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BizStockDatabase::class.java,
                    "bizstock_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
