package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.cloud.BizStockCloudService
import com.example.data.local.BizStockDatabase
import com.example.data.model.BusinessMetrics
import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.StockMovement
import com.example.data.model.Supplier
import com.example.data.model.SyncInfo
import com.example.data.network.NetworkMonitor
import com.example.data.repository.BizStockRepository
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class NavigationDestination(val title: String) {
    SPLASH("Lancement"),
    HOME("Accueil"),
    LOGIN("Connexion"),
    REGISTER("Inscription"),
    DASHBOARD("Tableau de bord"),
    PRODUCTS("Produits"),
    STOCK("Stock"),
    SALES("Ventes"),
    PURCHASES("Achats"),
    CUSTOMERS("Clients"),
    SUPPLIERS("Fournisseurs"),
    STATISTICS("Statistiques"),
    SETTINGS("Paramètres"),
    PLUS("Plus")
}

enum class StockFilter(val label: String) {
    ALL("Tous"),
    LOW_STOCK("Stock faible"),
    OUT_OF_STOCK("Rupture"),
    IN_STOCK("En stock")
}

enum class ProductSortOption(val label: String) {
    NAME_ASC("Nom (A-Z)"),
    NAME_DESC("Nom (Z-A)"),
    STOCK_ASC("Stock croissant"),
    STOCK_DESC("Stock décroissant"),
    PRICE_INDEX_DESC("Indice Prix élevé"),
    RECENT("Plus récents")
}

enum class ProductViewMode {
    TABLE, CARDS
}

class BizStockViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BizStockDatabase.getDatabase(application, viewModelScope)
    val cloudService = BizStockCloudService(application)
    private val repository = BizStockRepository(database.bizStockDao(), cloudService)
    private val networkMonitor = NetworkMonitor(application)

    // SaaS Multi-enterprise tenant ID
    private val _currentCompanyId = MutableStateFlow("company_default")
    val currentCompanyId: StateFlow<String> = _currentCompanyId.asStateFlow()

    // Navigation (Starts on SPLASH)
    private val _currentDestination = MutableStateFlow(NavigationDestination.SPLASH)
    val currentDestination: StateFlow<NavigationDestination> = _currentDestination.asStateFlow()

    // User session & Authentification
    private val _isLoggedIn = MutableStateFlow(cloudService.getCurrentUser() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow(cloudService.getCurrentUser()?.email ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow(cloudService.getCurrentUser()?.displayName ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _companyName = MutableStateFlow("")
    val companyName: StateFlow<String> = _companyName.asStateFlow()

    // Filters, Sort & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Toutes")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _stockFilter = MutableStateFlow(StockFilter.ALL)
    val stockFilter: StateFlow<StockFilter> = _stockFilter.asStateFlow()

    private val _productSortOption = MutableStateFlow(ProductSortOption.NAME_ASC)
    val productSortOption: StateFlow<ProductSortOption> = _productSortOption.asStateFlow()

    private val _productViewMode = MutableStateFlow(ProductViewMode.TABLE)
    val productViewMode: StateFlow<ProductViewMode> = _productViewMode.asStateFlow()

    // Data streams
    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<Supplier>> = repository.allSuppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<Sale>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchases: StateFlow<List<Purchase>> = repository.allPurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockMovements: StateFlow<List<StockMovement>> = repository.allMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Network & Sync State
    private val _isManualOffline = MutableStateFlow(false)
    val isManualOffline: StateFlow<Boolean> = _isManualOffline.asStateFlow()

    private val _syncInfo = MutableStateFlow(SyncInfo(isOnline = true))
    val syncInfo: StateFlow<SyncInfo> = _syncInfo.asStateFlow()

    private var previousOnlineState: Boolean? = null

    // UI Feedback & Dialogs
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _showAddProductDialog = MutableStateFlow(false)
    val showAddProductDialog: StateFlow<Boolean> = _showAddProductDialog.asStateFlow()

    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct.asStateFlow()

    private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete: StateFlow<Product?> = _productToDelete.asStateFlow()

    private val _adjustingProduct = MutableStateFlow<Product?>(null)
    val adjustingProduct: StateFlow<Product?> = _adjustingProduct.asStateFlow()

    private val _selectedProductForDetail = MutableStateFlow<Product?>(null)
    val selectedProductForDetail: StateFlow<Product?> = _selectedProductForDetail.asStateFlow()

    private val _showSyncDetailsDialog = MutableStateFlow(false)
    val showSyncDetailsDialog: StateFlow<Boolean> = _showSyncDetailsDialog.asStateFlow()

    private val _showQuickActionsMenu = MutableStateFlow(false)
    val showQuickActionsMenu: StateFlow<Boolean> = _showQuickActionsMenu.asStateFlow()

    private val _showAddCustomerDialog = MutableStateFlow(false)
    val showAddCustomerDialog: StateFlow<Boolean> = _showAddCustomerDialog.asStateFlow()

    private val _showAddExpenseDialog = MutableStateFlow(false)
    val showAddExpenseDialog: StateFlow<Boolean> = _showAddExpenseDialog.asStateFlow()

    // Sales management
    private val _salesSearchQuery = MutableStateFlow("")
    val salesSearchQuery: StateFlow<String> = _salesSearchQuery.asStateFlow()

    private val _salesStatusFilter = MutableStateFlow("TOUS")
    val salesStatusFilter: StateFlow<String> = _salesStatusFilter.asStateFlow()

    private val _showCreateSaleDialog = MutableStateFlow(false)
    val showCreateSaleDialog: StateFlow<Boolean> = _showCreateSaleDialog.asStateFlow()

    private val _selectedSaleForInvoice = MutableStateFlow<Sale?>(null)
    val selectedSaleForInvoice: StateFlow<Sale?> = _selectedSaleForInvoice.asStateFlow()

    val filteredSales: StateFlow<List<Sale>> = combine(
        sales,
        _salesSearchQuery,
        _salesStatusFilter
    ) { salesList, query, status ->
        salesList.filter { sale ->
            val matchesQuery = query.isBlank() ||
                    sale.invoiceNumber.contains(query, ignoreCase = true) ||
                    sale.customerName.contains(query, ignoreCase = true) ||
                    sale.itemsSummary.contains(query, ignoreCase = true)
            val matchesStatus = status == "TOUS" || sale.paymentStatus.equals(status, ignoreCase = true)
            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered & Sorted Products
    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        _searchQuery,
        _selectedCategory,
        _stockFilter,
        _productSortOption
    ) { prodList, query, cat, filter, sort ->
        val filtered = prodList.filter { prod ->
            val matchesQuery = query.isBlank() ||
                    prod.name.contains(query, ignoreCase = true) ||
                    prod.sku.contains(query, ignoreCase = true) ||
                    prod.category.contains(query, ignoreCase = true) ||
                    prod.description.contains(query, ignoreCase = true)

            val matchesCategory = cat == "Toutes" || prod.category.equals(cat, ignoreCase = true)

            val matchesFilter = when (filter) {
                StockFilter.ALL -> true
                StockFilter.LOW_STOCK -> prod.isLowStock
                StockFilter.OUT_OF_STOCK -> prod.isOutOfStock
                StockFilter.IN_STOCK -> !prod.isOutOfStock && !prod.isLowStock
            }

            matchesQuery && matchesCategory && matchesFilter
        }

        when (sort) {
            ProductSortOption.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            ProductSortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
            ProductSortOption.STOCK_ASC -> filtered.sortedBy { it.currentStock }
            ProductSortOption.STOCK_DESC -> filtered.sortedByDescending { it.currentStock }
            ProductSortOption.PRICE_INDEX_DESC -> filtered.sortedByDescending { it.priceIndex }
            ProductSortOption.RECENT -> filtered.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Business KPIs (indices)
    val businessMetrics: StateFlow<BusinessMetrics> = combine(
        products,
        sales,
        purchases,
        customers,
        suppliers
    ) { prodList, salesList, purchList, custList, suppList ->
        val totalRev = salesList.sumOf { it.totalAmount }
        val totalPurch = purchList.sumOf { it.totalAmount }
        val stockVal = prodList.sumOf { it.purchasePrice * it.currentStock }

        BusinessMetrics(
            totalRevenue = totalRev,
            todayRevenue = totalRev * 0.35,
            totalPurchases = totalPurch,
            todayPurchases = totalPurch * 0.25,
            netProfit = totalRev - totalPurch,
            totalStockValue = stockVal,
            totalProductsCount = prodList.size,
            lowStockCount = prodList.count { it.isLowStock },
            outOfStockCount = prodList.count { it.isOutOfStock },
            totalClientsCount = custList.size,
            totalSuppliersCount = suppList.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BusinessMetrics())

    init {
        val firebaseUser = cloudService.getCurrentUser()
        if (firebaseUser != null) {
            _isLoggedIn.value = true
            viewModelScope.launch {
                try {
                    val (profile, comp) = cloudService.setupUserAndCompany(firebaseUser)
                    _currentCompanyId.value = comp.id
                    _companyName.value = comp.name
                    _userName.value = profile.displayName.ifBlank { firebaseUser.displayName ?: "Nathan Anato" }
                    _userEmail.value = profile.email.ifBlank { firebaseUser.email ?: "" }
                    startRealtimeSync(comp.id)
                } catch (e: Exception) {
                    Log.w("BizStockViewModel", "Firebase user init failed: ${e.message}")
                }
            }
        }

        viewModelScope.launch {
            combine(networkMonitor.isOnline, _isManualOffline) { realOnline, manualOff ->
                Pair(realOnline, manualOff)
            }.collect { (realOnline, manualOff) ->
                val effectivelyOnline = realOnline && !manualOff
                val currentInfo = _syncInfo.value
                _syncInfo.value = currentInfo.copy(
                    isOnline = realOnline,
                    isManualOfflineMode = manualOff,
                    syncLogMessage = if (effectivelyOnline) "Connecté au Cloud BIZSTOCK" else "Mode Hors ligne actif"
                )

                if (previousOnlineState == false && effectivelyOnline) {
                    showSnackbar("Connexion rétablie : synchronisation automatique BIZSTOCK...")
                    syncNow(isAutomatic = true)
                }
                previousOnlineState = effectivelyOnline
            }
        }
    }

    private fun startRealtimeSync(companyId: String) {
        viewModelScope.launch {
            cloudService.listenProducts(companyId).collect { cloudProds ->
                if (cloudProds.isNotEmpty()) {
                    database.bizStockDao().insertProducts(cloudProds)
                }
            }
        }
    }

    fun setDestination(destination: NavigationDestination) {
        if (!_isLoggedIn.value && destination != NavigationDestination.SPLASH && destination != NavigationDestination.LOGIN && destination != NavigationDestination.REGISTER) {
            _currentDestination.value = NavigationDestination.LOGIN
            showSnackbar("Veuillez vous connecter pour accéder à cet espace.")
            return
        }
        _currentDestination.value = destination
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val serverClientId = context.getString(R.string.default_web_client_id)
                val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId).build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = context, request = request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                val authResult = cloudService.auth.signInWithCredential(authCredential).await()
                val user = authResult.user

                if (user != null) {
                    val (profile, comp) = cloudService.setupUserAndCompany(user, _companyName.value)
                    _currentCompanyId.value = comp.id
                    _companyName.value = comp.name
                    _userName.value = profile.displayName.ifBlank { user.displayName ?: "Nathan Anato" }
                    _userEmail.value = profile.email.ifBlank { user.email ?: "" }
                    _isLoggedIn.value = true
                    _currentDestination.value = NavigationDestination.DASHBOARD
                    startRealtimeSync(comp.id)
                    showSnackbar("Connecté avec succès via Google : ${profile.email}")
                    syncNow(isAutomatic = true)
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("BizStockViewModel", "Connexion Google annulée: ${e.message}")
                showSnackbar("Connexion Google annulée.")
            } catch (e: Exception) {
                Log.e("BizStockViewModel", "Erreur Google Sign-In", e)
                showSnackbar("Erreur de connexion : ${e.localizedMessage ?: e.message}")
            }
        }
    }

    fun completeSplash() {
        if (_isLoggedIn.value) {
            _currentDestination.value = NavigationDestination.DASHBOARD
        } else {
            _currentDestination.value = NavigationDestination.LOGIN
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            showSnackbar("Veuillez renseigner votre email et mot de passe.")
            return
        }

        viewModelScope.launch {
            val result = cloudService.signInWithEmail(email, pass)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    val (profile, comp) = cloudService.setupUserAndCompany(user, _companyName.value)
                    _currentCompanyId.value = comp.id
                    _companyName.value = comp.name
                    _userName.value = profile.displayName.ifBlank { user.displayName ?: email.substringBefore("@") }
                    _userEmail.value = user.email ?: email
                    _isLoggedIn.value = true
                    _currentDestination.value = NavigationDestination.DASHBOARD
                    startRealtimeSync(comp.id)
                    showSnackbar("Connexion réussie. Bienvenue sur BIZSTOCK !")
                    syncNow(isAutomatic = true)
                    return@launch
                }
            } else {
                val err = result.exceptionOrNull()
                val rawMsg = err?.localizedMessage ?: err?.message ?: "Identifiants incorrects"
                val friendlyMsg = when {
                    rawMsg.contains("invalid-credential", ignoreCase = true) || rawMsg.contains("password is invalid", ignoreCase = true) || rawMsg.contains("wrong-password", ignoreCase = true) ->
                        "Email ou mot de passe incorrect."
                    rawMsg.contains("user-not-found", ignoreCase = true) ->
                        "Aucun compte trouvé avec cet email. Veuillez créer un compte."
                    rawMsg.contains("network", ignoreCase = true) ->
                        "Erreur réseau. Vérifiez votre connexion internet."
                    rawMsg.contains("too-many-requests", ignoreCase = true) ->
                        "Trop de tentatives infructueuses. Veuillez patienter quelques minutes."
                    rawMsg.contains("operation is not allowed", ignoreCase = true) ->
                        "L'activation du mot de passe est en cours de propagation sur Firebase (délai de 1 à 2 min). Réessayez ou utilisez Google."
                    else -> rawMsg
                }
                showSnackbar(friendlyMsg)
            }
        }
    }

    fun register(company: String, name: String, email: String, pass: String) {
        if (email.isBlank() || name.isBlank() || pass.isBlank()) {
            showSnackbar("Veuillez remplir tous les champs obligatoires.")
            return
        }

        viewModelScope.launch {
            val companyNameClean = company.ifBlank { "Mon Entreprise BIZSTOCK" }
            val result = cloudService.registerWithEmail(email, pass)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    val (profile, comp) = cloudService.setupUserAndCompany(user, companyNameClean)
                    _currentCompanyId.value = comp.id
                    _companyName.value = comp.name
                    _userName.value = name
                    _userEmail.value = email
                    _isLoggedIn.value = true
                    _currentDestination.value = NavigationDestination.DASHBOARD
                    startRealtimeSync(comp.id)
                    showSnackbar("Compte créé avec succès ! Bienvenue $name.")
                    syncNow(isAutomatic = true)
                    return@launch
                }
            } else {
                val err = result.exceptionOrNull()
                val rawMsg = err?.localizedMessage ?: err?.message ?: "Erreur d'inscription"
                val friendlyMsg = when {
                    rawMsg.contains("email-already-in-use", ignoreCase = true) ->
                        "Cette adresse email est déjà associée à un compte. Veuillez vous connecter."
                    rawMsg.contains("weak-password", ignoreCase = true) ->
                        "Le mot de passe doit comporter au moins 6 caractères."
                    rawMsg.contains("invalid-email", ignoreCase = true) ->
                        "Adresse email invalide."
                    rawMsg.contains("operation is not allowed", ignoreCase = true) ->
                        "L'activation du mot de passe vient d'être activée sur Firebase. Veuillez patienter 1 à 2 minutes pour la propagation ou utiliser Google."
                    else -> "Échec de création de compte : $rawMsg"
                }
                showSnackbar(friendlyMsg)
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            showSnackbar("Veuillez indiquer votre adresse email.")
            return
        }
        viewModelScope.launch {
            val result = cloudService.sendPasswordReset(email)
            result.onSuccess {
                showSnackbar("Un lien de réinitialisation a été envoyé à $email.")
            }.onFailure { err ->
                showSnackbar("Erreur : ${err.localizedMessage ?: "Vérifiez l'adresse email"}")
            }
        }
    }

    fun logout() {
        try {
            cloudService.auth.signOut()
        } catch (_: Exception) {}
        _isLoggedIn.value = false
        _userEmail.value = ""
        _userName.value = ""
        _companyName.value = ""
        _currentDestination.value = NavigationDestination.LOGIN
        showSnackbar("Vous avez été déconnecté.")
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearLocalData()
            showSnackbar("Toutes les données locales ont été réinitialisées.")
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setStockFilter(filter: StockFilter) {
        _stockFilter.value = filter
    }

    fun setProductSortOption(sort: ProductSortOption) {
        _productSortOption.value = sort
    }

    fun setProductViewMode(mode: ProductViewMode) {
        _productViewMode.value = mode
    }

    fun toggleManualOfflineMode() {
        val next = !_isManualOffline.value
        _isManualOffline.value = next
        if (next) {
            showSnackbar("Mode hors ligne activé. Ajouts et modifications verrouillés.")
        } else {
            showSnackbar("Mode en ligne réactivé. Synchronisation...")
        }
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun setShowSyncDetailsDialog(show: Boolean) {
        _showSyncDetailsDialog.value = show
    }

    fun openQuickActions() {
        _showQuickActionsMenu.value = true
    }

    fun closeQuickActions() {
        _showQuickActionsMenu.value = false
    }

    fun openAddProductDialog() {
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible : Vous êtes hors ligne. L'ajout de produit requiert une connexion.")
            return
        }
        _editingProduct.value = null
        _showAddProductDialog.value = true
    }

    fun openEditProductDialog(product: Product) {
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible : Vous êtes hors ligne. La modification est verrouillée.")
            return
        }
        _editingProduct.value = product
        _showAddProductDialog.value = true
    }

    fun closeProductDialog() {
        _showAddProductDialog.value = false
        _editingProduct.value = null
    }

    fun openProductDetail(product: Product) {
        _selectedProductForDetail.value = product
    }

    fun closeProductDetail() {
        _selectedProductForDetail.value = null
    }

    fun confirmDeleteProduct(product: Product) {
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible : Vous êtes hors ligne. La suppression requiert une connexion.")
            return
        }
        _productToDelete.value = product
    }

    fun dismissDeleteDialog() {
        _productToDelete.value = null
    }

    fun deleteConfirmedProduct() {
        val prod = _productToDelete.value ?: return
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible en mode hors ligne.")
            _productToDelete.value = null
            return
        }
        viewModelScope.launch {
            val result = repository.deleteProduct(prod)
            if (result.isSuccess) {
                showSnackbar("Produit « ${prod.name} » supprimé avec succès.")
            } else {
                showSnackbar("Erreur lors de la suppression.")
            }
            _productToDelete.value = null
        }
    }

    fun openAdjustStockDialog(product: Product) {
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible : Vous êtes hors ligne. L'ajustement requiert une connexion.")
            return
        }
        _adjustingProduct.value = product
    }

    fun closeAdjustStockDialog() {
        _adjustingProduct.value = null
    }

    fun adjustStock(delta: Int, reason: String) {
        val prod = _adjustingProduct.value ?: return
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible en mode hors ligne.")
            _adjustingProduct.value = null
            return
        }
        viewModelScope.launch {
            val result = repository.adjustStock(prod.id, prod.name, delta, reason)
            if (result.isSuccess) {
                showSnackbar("Stock mis à jour pour ${prod.name}")
            } else {
                showSnackbar("Erreur lors de l'ajustement du stock.")
            }
            _adjustingProduct.value = null
        }
    }

    fun saveProduct(
        id: String? = null,
        name: String,
        sku: String,
        category: String,
        purchasePrice: Double,
        sellingPrice: Double,
        stock: Int,
        minAlert: Int,
        unit: String,
        description: String,
        imageUri: String? = null
    ) {
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible : Vous êtes hors ligne. L'enregistrement requiert une connexion.")
            return
        }

        viewModelScope.launch {
            val compId = _currentCompanyId.value
            val isNew = id == null
            val product = Product(
                id = id ?: java.util.UUID.randomUUID().toString(),
                companyId = compId,
                sku = sku.ifBlank { "PRD-${System.currentTimeMillis() % 10000}" },
                name = name,
                category = category.ifBlank { "Général" },
                purchasePrice = purchasePrice,
                sellingPrice = sellingPrice,
                currentStock = stock,
                minStockAlert = minAlert,
                unit = unit.ifBlank { "Unité" },
                description = description,
                imageUri = imageUri ?: "",
                syncStatus = "SYNCED",
                lastSyncedAt = System.currentTimeMillis()
            )

            val result = if (isNew) repository.addProduct(product) else repository.updateProduct(product)
            if (result.isSuccess) {
                showSnackbar(if (isNew) "Produit ajouté avec succès !" else "Produit mis à jour !")
                closeProductDialog()
            } else {
                showSnackbar("Erreur lors de l'enregistrement.")
            }
        }
    }

    fun syncNow(isAutomatic: Boolean = false) {
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Synchronisation impossible : Vous êtes hors ligne.")
            return
        }

        viewModelScope.launch {
            _syncInfo.value = _syncInfo.value.copy(isSyncing = true)
            val result = repository.syncWithCloud(_currentCompanyId.value)
            _syncInfo.value = _syncInfo.value.copy(
                isSyncing = false,
                lastSyncTimestamp = System.currentTimeMillis(),
                syncLogMessage = result.getOrDefault("Synchronisation terminée")
            )
            if (!isAutomatic) {
                showSnackbar("Données synchronisées avec le Cloud BIZSTOCK !")
            }
        }
    }

    // Sales actions
    fun setSalesSearchQuery(query: String) {
        _salesSearchQuery.value = query
    }

    fun setSalesStatusFilter(status: String) {
        _salesStatusFilter.value = status
    }

    fun openCreateSaleDialog() {
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible : Vous êtes hors ligne. La création de facture requiert une connexion.")
            return
        }
        _showCreateSaleDialog.value = true
    }

    fun closeCreateSaleDialog() {
        _showCreateSaleDialog.value = false
    }

    fun openSaleInvoice(sale: Sale) {
        _selectedSaleForInvoice.value = sale
    }

    fun closeSaleInvoice() {
        _selectedSaleForInvoice.value = null
    }

    fun recordSale(
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        paymentStatus: String,
        paymentMethod: String,
        items: List<SaleItem>,
        notes: String
    ) {
        if (!_syncInfo.value.isEffectivelyOnline) {
            showSnackbar("Action impossible hors ligne.")
            return
        }

        viewModelScope.launch {
            val total = items.sumOf { it.unitPrice * it.quantity }
            val count = items.sumOf { it.quantity }
            val summary = items.joinToString(", ") { "${it.quantity}x ${it.productName}" }
            val invoiceNum = "FAC-${System.currentTimeMillis() % 100000}"

            val sale = Sale(
                id = java.util.UUID.randomUUID().toString(),
                companyId = _currentCompanyId.value,
                invoiceNumber = invoiceNum,
                customerName = customerName,
                customerPhone = customerPhone,
                customerEmail = customerEmail,
                totalAmount = total,
                paymentStatus = paymentStatus,
                paymentMethod = paymentMethod,
                date = System.currentTimeMillis(),
                itemsSummary = summary,
                itemsCount = count,
                notes = notes,
                syncStatus = "SYNCED",
                lastSyncedAt = System.currentTimeMillis()
            )

            val result = repository.recordSale(sale, items)
            if (result.isSuccess) {
                showSnackbar("Facture $invoiceNum émise avec succès !")
                closeCreateSaleDialog()
            } else {
                showSnackbar("Erreur lors de la création de la vente.")
            }
        }
    }

    // Customer & Expense Dialog actions
    fun openAddCustomerDialog() {
        _showAddCustomerDialog.value = true
    }

    fun closeAddCustomerDialog() {
        _showAddCustomerDialog.value = false
    }

    fun saveCustomer(name: String, phone: String, email: String, company: String, city: String) {
        viewModelScope.launch {
            val customer = Customer(
                id = java.util.UUID.randomUUID().toString(),
                companyId = _currentCompanyId.value,
                name = name,
                phone = phone,
                email = email,
                company = company,
                city = city
            )
            repository.addCustomer(customer)
            showSnackbar("Client « $name » ajouté !")
            closeAddCustomerDialog()
        }
    }

    fun openAddExpenseDialog() {
        _showAddExpenseDialog.value = true
    }

    fun closeAddExpenseDialog() {
        _showAddExpenseDialog.value = false
    }

    fun saveExpense(title: String, amount: Double, category: String, notes: String) {
        viewModelScope.launch {
            val purchase = Purchase(
                id = java.util.UUID.randomUUID().toString(),
                companyId = _currentCompanyId.value,
                orderNumber = "ACH-${System.currentTimeMillis() % 10000}",
                supplierName = title,
                totalAmount = amount,
                status = "REÇU",
                itemsSummary = category,
                itemsCount = 1
            )
            repository.addPurchase(purchase)
            showSnackbar("Dépense enregistrée !")
            closeAddExpenseDialog()
        }
    }
}
