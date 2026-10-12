package com.example.data.cloud

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Company
import com.example.data.model.Customer
import com.example.data.model.Membership
import com.example.data.model.Product
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.StockMovement
import com.example.data.model.Supplier
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "BizStockCloudService"

class BizStockCloudService(
    private val context: Context,
    val db: FirebaseFirestore
) {
    val auth: FirebaseAuth = Firebase.auth

    constructor(context: Context) : this(
        context,
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user ?: return Result.failure(Exception("Compte introuvable"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user ?: return Result.failure(Exception("Échec de création du compte"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setupUserAndCompany(user: FirebaseUser, requestedCompanyName: String = ""): Pair<UserProfile, Company> {
        val userId = user.uid
        val email = user.email ?: ""
        val displayName = user.displayName ?: email.substringBefore("@")
        val companyId = "comp_${userId.take(8)}"

        val userDocRef = db.collection("users").document(userId)
        val userSnapshot = try { userDocRef.get().await() } catch (e: Exception) { null }
        val activeCompanyId = userSnapshot?.getString("currentCompanyId") ?: companyId

        val companyDocRef = db.collection("companies").document(activeCompanyId)
        val companySnapshot = try { companyDocRef.get().await() } catch (e: Exception) { null }

        val finalCompanyName = if (companySnapshot != null && companySnapshot.exists()) {
            companySnapshot.getString("name") ?: (requestedCompanyName.ifBlank { "BIZSTOCK Entreprise" })
        } else {
            requestedCompanyName.ifBlank { "BIZSTOCK Entreprise" }
        }

        val userProfile = UserProfile(
            id = userId,
            email = email,
            displayName = displayName,
            currentCompanyId = activeCompanyId,
            createdAt = System.currentTimeMillis()
        )

        val company = Company(
            id = activeCompanyId,
            name = finalCompanyName,
            ownerId = userId,
            plan = "PRO",
            createdAt = System.currentTimeMillis()
        )

        val membershipId = "${userId}_${activeCompanyId}"
        val membership = Membership(
            id = membershipId,
            userId = userId,
            companyId = activeCompanyId,
            role = "OWNER",
            active = true
        )

        try {
            userDocRef.set(
                mapOf(
                    "id" to userProfile.id,
                    "email" to userProfile.email,
                    "displayName" to userProfile.displayName,
                    "currentCompanyId" to userProfile.currentCompanyId,
                    "createdAt" to userProfile.createdAt
                ),
                SetOptions.merge()
            ).await()

            companyDocRef.set(
                mapOf(
                    "id" to company.id,
                    "name" to company.name,
                    "ownerId" to company.ownerId,
                    "plan" to company.plan,
                    "createdAt" to company.createdAt
                ),
                SetOptions.merge()
            ).await()

            db.collection("memberships").document(membershipId).set(
                mapOf(
                    "id" to membership.id,
                    "userId" to membership.userId,
                    "companyId" to membership.companyId,
                    "role" to membership.role,
                    "active" to membership.active,
                    "joinedAt" to membership.joinedAt
                ),
                SetOptions.merge()
            ).await()
        } catch (e: Exception) {
            Log.w(TAG, "Error ensuring user/company setup in Firestore: ${e.message}")
        }

        return Pair(userProfile, company)
    }

    // Real-time Cloud Listeners for products
    fun listenProducts(companyId: String): Flow<List<Product>> = callbackFlow {
        val query = db.collection("companies").document(companyId).collection("products")
        val registration: ListenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to cloud products", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        Product(
                            id = doc.getString("id") ?: doc.id,
                            companyId = doc.getString("companyId") ?: companyId,
                            sku = doc.getString("sku") ?: "",
                            name = doc.getString("name") ?: "",
                            category = doc.getString("category") ?: "Général",
                            purchasePrice = doc.getDouble("purchasePrice") ?: 0.0,
                            sellingPrice = doc.getDouble("sellingPrice") ?: 0.0,
                            currentStock = doc.getLong("currentStock")?.toInt() ?: 0,
                            minStockAlert = doc.getLong("minStockAlert")?.toInt() ?: 5,
                            unit = doc.getString("unit") ?: "Unité",
                            description = doc.getString("description") ?: "",
                            imageUri = doc.getString("imageUri") ?: "",
                            syncStatus = "SYNCED",
                            lastSyncedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(list)
            }
        }
        awaitClose { registration.remove() }
    }

    // Real-time Cloud Listeners for sales
    fun listenSales(companyId: String): Flow<List<Sale>> = callbackFlow {
        val query = db.collection("companies").document(companyId).collection("sales")
        val registration: ListenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to cloud sales", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        Sale(
                            id = doc.getString("id") ?: doc.id,
                            companyId = doc.getString("companyId") ?: companyId,
                            invoiceNumber = doc.getString("invoiceNumber") ?: "",
                            customerName = doc.getString("customerName") ?: "",
                            customerPhone = doc.getString("customerPhone") ?: "",
                            customerEmail = doc.getString("customerEmail") ?: "",
                            totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                            paymentStatus = doc.getString("paymentStatus") ?: "PAYÉE",
                            paymentMethod = doc.getString("paymentMethod") ?: "ESPECES",
                            date = doc.getLong("date") ?: System.currentTimeMillis(),
                            itemsSummary = doc.getString("itemsSummary") ?: "",
                            itemsCount = doc.getLong("itemsCount")?.toInt() ?: 1,
                            itemsJson = doc.getString("itemsJson") ?: "[]",
                            notes = doc.getString("notes") ?: "",
                            syncStatus = "SYNCED",
                            lastSyncedAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(list)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun syncProductToCloud(product: Product): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Utilisateur non connecté"))
        return try {
            val docRef = db.collection("companies").document(product.companyId)
                .collection("products").document(product.id)
            docRef.set(
                mapOf(
                    "id" to product.id,
                    "companyId" to product.companyId,
                    "sku" to product.sku,
                    "name" to product.name,
                    "category" to product.category,
                    "purchasePrice" to product.purchasePrice,
                    "sellingPrice" to product.sellingPrice,
                    "currentStock" to product.currentStock,
                    "minStockAlert" to product.minStockAlert,
                    "unit" to product.unit,
                    "description" to product.description,
                    "imageUri" to product.imageUri,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "syncProductToCloud failed", e)
            Result.failure(e)
        }
    }

    suspend fun deleteProductFromCloud(companyId: String, productId: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Utilisateur non connecté"))
        return try {
            db.collection("companies").document(companyId)
                .collection("products").document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "deleteProductFromCloud failed", e)
            Result.failure(e)
        }
    }

    suspend fun syncSaleToCloud(sale: Sale): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Utilisateur non connecté"))
        return try {
            val docRef = db.collection("companies").document(sale.companyId)
                .collection("sales").document(sale.id)
            docRef.set(
                mapOf(
                    "id" to sale.id,
                    "companyId" to sale.companyId,
                    "invoiceNumber" to sale.invoiceNumber,
                    "customerName" to sale.customerName,
                    "customerPhone" to sale.customerPhone,
                    "customerEmail" to sale.customerEmail,
                    "totalAmount" to sale.totalAmount,
                    "paymentStatus" to sale.paymentStatus,
                    "paymentMethod" to sale.paymentMethod,
                    "date" to sale.date,
                    "itemsSummary" to sale.itemsSummary,
                    "itemsCount" to sale.itemsCount,
                    "itemsJson" to sale.itemsJson,
                    "notes" to sale.notes,
                    "createdAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "syncSaleToCloud failed", e)
            Result.failure(e)
        }
    }

    suspend fun syncMovementToCloud(movement: StockMovement): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Utilisateur non connecté"))
        return try {
            val docRef = db.collection("companies").document(movement.companyId)
                .collection("stockMovements").document(movement.id)
            docRef.set(
                mapOf(
                    "id" to movement.id,
                    "companyId" to movement.companyId,
                    "productId" to movement.productId,
                    "productName" to movement.productName,
                    "type" to movement.type,
                    "quantity" to movement.quantity,
                    "reason" to movement.reason,
                    "date" to movement.date,
                    "operator" to movement.operator
                ),
                SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "syncMovementToCloud failed", e)
            Result.failure(e)
        }
    }

    suspend fun syncCustomerToCloud(customer: Customer): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Utilisateur non connecté"))
        return try {
            val docRef = db.collection("companies").document(customer.companyId)
                .collection("customers").document(customer.id)
            docRef.set(
                mapOf(
                    "id" to customer.id,
                    "companyId" to customer.companyId,
                    "name" to customer.name,
                    "company" to customer.company,
                    "email" to customer.email,
                    "phone" to customer.phone,
                    "city" to customer.city,
                    "balance" to customer.balance,
                    "totalOrders" to customer.totalOrders
                ),
                SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncSupplierToCloud(supplier: Supplier): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Utilisateur non connecté"))
        return try {
            val docRef = db.collection("companies").document(supplier.companyId)
                .collection("suppliers").document(supplier.id)
            docRef.set(
                mapOf(
                    "id" to supplier.id,
                    "companyId" to supplier.companyId,
                    "name" to supplier.name,
                    "company" to supplier.company,
                    "email" to supplier.email,
                    "phone" to supplier.phone,
                    "category" to supplier.category,
                    "activeOrders" to supplier.activeOrders,
                    "rating" to supplier.rating
                ),
                SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncPurchaseToCloud(purchase: Purchase): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Utilisateur non connecté"))
        return try {
            val docRef = db.collection("companies").document(purchase.companyId)
                .collection("purchases").document(purchase.id)
            docRef.set(
                mapOf(
                    "id" to purchase.id,
                    "companyId" to purchase.companyId,
                    "orderNumber" to purchase.orderNumber,
                    "supplierName" to purchase.supplierName,
                    "totalAmount" to purchase.totalAmount,
                    "status" to purchase.status,
                    "date" to purchase.date,
                    "itemsSummary" to purchase.itemsSummary,
                    "itemsCount" to purchase.itemsCount
                ),
                SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAllCloudProducts(companyId: String): List<Product> {
        return try {
            val snapshot = db.collection("companies").document(companyId)
                .collection("products").get().await()
            snapshot.documents.mapNotNull { doc ->
                Product(
                    id = doc.getString("id") ?: doc.id,
                    companyId = doc.getString("companyId") ?: companyId,
                    sku = doc.getString("sku") ?: "",
                    name = doc.getString("name") ?: "",
                    category = doc.getString("category") ?: "Général",
                    purchasePrice = doc.getDouble("purchasePrice") ?: 0.0,
                    sellingPrice = doc.getDouble("sellingPrice") ?: 0.0,
                    currentStock = doc.getLong("currentStock")?.toInt() ?: 0,
                    minStockAlert = doc.getLong("minStockAlert")?.toInt() ?: 5,
                    unit = doc.getString("unit") ?: "Unité",
                    description = doc.getString("description") ?: "",
                    imageUri = doc.getString("imageUri") ?: "",
                    syncStatus = "SYNCED",
                    lastSyncedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchAllCloudProducts error", e)
            emptyList()
        }
    }
}
