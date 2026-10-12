package com.example.data.repository

import com.example.data.cloud.BizStockCloudService
import com.example.data.local.BizStockDao
import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.StockMovement
import com.example.data.model.Supplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class BizStockRepository(
    private val dao: BizStockDao,
    private val cloudService: BizStockCloudService? = null
) {
    val allProducts: Flow<List<Product>> = dao.getAllProducts()
    val allCustomers: Flow<List<Customer>> = dao.getAllCustomers()
    val allSuppliers: Flow<List<Supplier>> = dao.getAllSuppliers()
    val allSales: Flow<List<Sale>> = dao.getAllSales()
    val allPurchases: Flow<List<Purchase>> = dao.getAllPurchases()
    val allMovements: Flow<List<StockMovement>> = dao.getAllMovements()

    suspend fun clearLocalData() = withContext(Dispatchers.IO) {
        dao.clearAllProducts()
        dao.clearAllCustomers()
        dao.clearAllSuppliers()
        dao.clearAllSales()
        dao.clearAllPurchases()
        dao.clearAllMovements()
    }

    suspend fun addProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val toSave = product.copy(syncStatus = "SYNCED", lastSyncedAt = System.currentTimeMillis())
            dao.insertProduct(toSave)

            // Record initial stock movement if quantity > 0
            if (product.currentStock > 0) {
                val movement = StockMovement(
                    id = UUID.randomUUID().toString(),
                    companyId = product.companyId,
                    productId = product.id,
                    productName = product.name,
                    type = "ENTRÉE",
                    quantity = product.currentStock,
                    reason = "Stock initial à la création du produit",
                    date = System.currentTimeMillis()
                )
                dao.insertMovement(movement)
                cloudService?.syncMovementToCloud(movement)
            }
            cloudService?.syncProductToCloud(toSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val toSave = product.copy(syncStatus = "SYNCED", lastSyncedAt = System.currentTimeMillis())
            dao.updateProduct(toSave)
            cloudService?.syncProductToCloud(toSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.deleteProduct(product)
            cloudService?.deleteProductFromCloud(product.companyId, product.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun adjustStock(productId: String, productName: String, quantityChange: Int, reason: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val current = dao.getProductById(productId) ?: return@withContext Result.failure(Exception("Produit introuvable"))
                val newStock = (current.currentStock + quantityChange).coerceAtLeast(0)
                dao.updateStock(productId, newStock, System.currentTimeMillis())

                val type = if (quantityChange >= 0) "ENTRÉE" else "SORTIE"
                val movement = StockMovement(
                    id = UUID.randomUUID().toString(),
                    companyId = current.companyId,
                    productId = productId,
                    productName = productName,
                    type = type,
                    quantity = kotlin.math.abs(quantityChange),
                    reason = reason.ifBlank { if (quantityChange >= 0) "Réception manuelle" else "Sortie manuelle" },
                    date = System.currentTimeMillis()
                )
                dao.insertMovement(movement)
                cloudService?.syncMovementToCloud(movement)
                cloudService?.syncProductToCloud(current.copy(currentStock = newStock))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun addCustomer(customer: Customer): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val toSave = customer.copy(syncStatus = "SYNCED", lastSyncedAt = System.currentTimeMillis())
            dao.insertCustomer(toSave)
            cloudService?.syncCustomerToCloud(toSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSupplier(supplier: Supplier): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val toSave = supplier.copy(syncStatus = "SYNCED", lastSyncedAt = System.currentTimeMillis())
            dao.insertSupplier(toSave)
            cloudService?.syncSupplierToCloud(toSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSale(sale: Sale): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val toSave = sale.copy(syncStatus = "SYNCED", lastSyncedAt = System.currentTimeMillis())
            dao.insertSale(toSave)
            cloudService?.syncSaleToCloud(toSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordSale(sale: Sale, items: List<SaleItem>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val toSave = sale.copy(syncStatus = "SYNCED", lastSyncedAt = System.currentTimeMillis())
            dao.insertSale(toSave)
            cloudService?.syncSaleToCloud(toSave)

            for (item in items) {
                val product = dao.getProductById(item.productId)
                if (product != null) {
                    val newStock = (product.currentStock - item.quantity).coerceAtLeast(0)
                    dao.updateStock(product.id, newStock, System.currentTimeMillis())

                    val movement = StockMovement(
                        id = UUID.randomUUID().toString(),
                        companyId = sale.companyId,
                        productId = product.id,
                        productName = product.name,
                        type = "SORTIE",
                        quantity = item.quantity,
                        reason = "Vente #${sale.invoiceNumber} • ${sale.customerName}",
                        date = sale.date
                    )
                    dao.insertMovement(movement)
                    cloudService?.syncMovementToCloud(movement)
                    cloudService?.syncProductToCloud(product.copy(currentStock = newStock))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addPurchase(purchase: Purchase): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val toSave = purchase.copy(syncStatus = "SYNCED", lastSyncedAt = System.currentTimeMillis())
            dao.insertPurchase(toSave)
            cloudService?.syncPurchaseToCloud(toSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncWithCloud(companyId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (cloudService != null && cloudService.getCurrentUser() != null) {
                // Fetch cloud products and merge into local room
                val cloudProds = cloudService.fetchAllCloudProducts(companyId)
                if (cloudProds.isNotEmpty()) {
                    dao.insertProducts(cloudProds)
                }

                // Push any unsynced local data to cloud
                val products = dao.getAllProducts().firstOrNull() ?: emptyList()
                products.forEach { cloudService.syncProductToCloud(it) }

                val sales = dao.getAllSales().firstOrNull() ?: emptyList()
                sales.forEach { cloudService.syncSaleToCloud(it) }
            }
            val now = System.currentTimeMillis()
            dao.markAllProductsSynced(now)
            Result.success("Synchronisation BIZSTOCK Cloud réussie")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
