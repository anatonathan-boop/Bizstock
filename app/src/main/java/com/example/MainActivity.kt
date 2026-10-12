package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddCustomerDialog
import com.example.ui.components.AddEditProductDialog
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.AdjustStockDialog
import com.example.ui.components.ConfirmDeleteProductDialog
import com.example.ui.components.CreateSaleDialog
import com.example.ui.components.ProductDetailDialog
import com.example.ui.components.QuickActionsSheet
import com.example.ui.components.ResponsiveScaffold
import com.example.ui.components.SaleInvoiceDialog
import com.example.ui.components.SyncDetailsDialog
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PlusScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.PurchasesScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.StockScreen
import com.example.ui.screens.SuppliersScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BizStockViewModel
import com.example.ui.viewmodel.NavigationDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BizStockApp()
            }
        }
    }
}

@Composable
fun BizStockApp(viewModel: BizStockViewModel = viewModel()) {
    val context = LocalContext.current
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val syncInfo by viewModel.syncInfo.collectAsStateWithLifecycle()
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val allProductsList by viewModel.products.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val sales by viewModel.filteredSales.collectAsStateWithLifecycle()
    val salesSearchQuery by viewModel.salesSearchQuery.collectAsStateWithLifecycle()
    val salesStatusFilter by viewModel.salesStatusFilter.collectAsStateWithLifecycle()
    val showCreateSaleDialog by viewModel.showCreateSaleDialog.collectAsStateWithLifecycle()
    val selectedSaleForInvoice by viewModel.selectedSaleForInvoice.collectAsStateWithLifecycle()
    val purchases by viewModel.purchases.collectAsStateWithLifecycle()
    val stockMovements by viewModel.stockMovements.collectAsStateWithLifecycle()
    val metrics by viewModel.businessMetrics.collectAsStateWithLifecycle()

    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val companyName by viewModel.companyName.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val stockFilter by viewModel.stockFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.productSortOption.collectAsStateWithLifecycle()
    val productViewMode by viewModel.productViewMode.collectAsStateWithLifecycle()
    val selectedProductForDetail by viewModel.selectedProductForDetail.collectAsStateWithLifecycle()

    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val showAddDialog by viewModel.showAddProductDialog.collectAsStateWithLifecycle()
    val editingProduct by viewModel.editingProduct.collectAsStateWithLifecycle()
    val productToDelete by viewModel.productToDelete.collectAsStateWithLifecycle()
    val adjustingProduct by viewModel.adjustingProduct.collectAsStateWithLifecycle()
    val showSyncDetailsDialog by viewModel.showSyncDetailsDialog.collectAsStateWithLifecycle()

    val showQuickActionsMenu by viewModel.showQuickActionsMenu.collectAsStateWithLifecycle()
    val showAddCustomerDialog by viewModel.showAddCustomerDialog.collectAsStateWithLifecycle()
    val showAddExpenseDialog by viewModel.showAddExpenseDialog.collectAsStateWithLifecycle()

    val canModify = syncInfo.isEffectivelyOnline

    // Android back navigation: return to appropriate root screen
    BackHandler(enabled = currentDestination != NavigationDestination.SPLASH &&
            ((isLoggedIn && currentDestination != NavigationDestination.DASHBOARD && currentDestination != NavigationDestination.HOME) ||
            (!isLoggedIn && currentDestination != NavigationDestination.LOGIN))) {
        viewModel.setDestination(if (isLoggedIn) NavigationDestination.DASHBOARD else NavigationDestination.LOGIN)
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (currentDestination == NavigationDestination.SPLASH) {
        SplashScreen(
            onTimeout = {
                viewModel.setDestination(if (isLoggedIn) NavigationDestination.DASHBOARD else NavigationDestination.LOGIN)
            }
        )
    } else if (!isLoggedIn && (currentDestination == NavigationDestination.LOGIN || currentDestination == NavigationDestination.REGISTER)) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentDestination) {
                NavigationDestination.LOGIN -> {
                    LoginScreen(
                        onLogin = { email, pass -> viewModel.login(email, pass) },
                        onGoogleSignIn = { viewModel.signInWithGoogle(context) },
                        onNavigate = { viewModel.setDestination(it) },
                        isWideScreen = false
                    )
                }
                NavigationDestination.REGISTER -> {
                    RegisterScreen(
                        onGoogleSignIn = { viewModel.signInWithGoogle(context) },
                        onRegister = { company, name, email, pass -> viewModel.register(company, name, email, pass) },
                        onNavigate = { viewModel.setDestination(it) },
                        isWideScreen = false
                    )
                }
                else -> Unit
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    } else {
        ResponsiveScaffold(
            currentDestination = currentDestination,
            onNavigate = { viewModel.setDestination(it) },
            syncInfo = syncInfo,
            isLoggedIn = isLoggedIn,
            userName = userName,
            onSyncClick = { viewModel.syncNow() },
            onOpenSyncDetails = { viewModel.setShowSyncDetailsDialog(true) },
            onToggleOfflineMode = { viewModel.toggleManualOfflineMode() },
            onQuickActionClick = { viewModel.openQuickActions() },
            snackbarHostState = snackbarHostState,
            lowStockCount = metrics.lowStockCount + metrics.outOfStockCount
        ) { isWideScreen ->
            when (currentDestination) {
                NavigationDestination.SPLASH -> Unit
                NavigationDestination.HOME -> {
                    HomeScreen(
                        metrics = metrics,
                        userName = userName,
                        companyName = companyName,
                        lowStockProducts = allProductsList.filter { it.isLowStock || it.isOutOfStock },
                        canModify = canModify,
                        onNavigate = { viewModel.setDestination(it) },
                        onNewSaleClick = { viewModel.openCreateSaleDialog() },
                        onAddProductClick = { viewModel.openAddProductDialog() },
                        onAddExpenseClick = { viewModel.openAddExpenseDialog() },
                        isWideScreen = isWideScreen
                    )
                }
            NavigationDestination.LOGIN -> {
                LoginScreen(
                    onLogin = { email, pass -> viewModel.login(email, pass) },
                    onGoogleSignIn = { viewModel.signInWithGoogle(context) },
                    onNavigate = { viewModel.setDestination(it) },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.REGISTER -> {
                RegisterScreen(
                        onGoogleSignIn = { viewModel.signInWithGoogle(context) },
                    onRegister = { company, name, email, pass -> viewModel.register(company, name, email, pass) },
                    onNavigate = { viewModel.setDestination(it) },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.DASHBOARD -> {
                DashboardScreen(
                    metrics = metrics,
                    recentSales = sales,
                    canModify = canModify,
                    isWideScreen = isWideScreen,
                    onNavigate = { viewModel.setDestination(it) },
                    onAddProductClick = { viewModel.openAddProductDialog() },
                    onNewSaleClick = { viewModel.openCreateSaleDialog() },
                    onSyncClick = { viewModel.syncNow() }
                )
            }
            NavigationDestination.PRODUCTS -> {
                ProductsScreen(
                    products = products,
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    selectedCategory = selectedCategory,
                    onCategoryChange = { viewModel.setSelectedCategory(it) },
                    stockFilter = stockFilter,
                    onStockFilterChange = { viewModel.setStockFilter(it) },
                    sortOption = sortOption,
                    onSortOptionChange = { viewModel.setProductSortOption(it) },
                    viewMode = productViewMode,
                    onViewModeChange = { viewModel.setProductViewMode(it) },
                    canModify = canModify,
                    onAddProductClick = { viewModel.openAddProductDialog() },
                    onViewProductDetail = { viewModel.openProductDetail(it) },
                    onEditProductClick = { viewModel.openEditProductDialog(it) },
                    onDeleteProductClick = { viewModel.confirmDeleteProduct(it) },
                    onAdjustStockClick = { viewModel.openAdjustStockDialog(it) },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.STOCK -> {
                StockScreen(
                    products = allProductsList,
                    movements = stockMovements,
                    canModify = canModify,
                    onAddProductClick = { viewModel.openAddProductDialog() },
                    onEditProductClick = { viewModel.openEditProductDialog(it) },
                    onAdjustStockClick = { viewModel.openAdjustStockDialog(it) },
                    onViewProductDetail = { viewModel.openProductDetail(it) },
                    onDeleteProductClick = { viewModel.confirmDeleteProduct(it) },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.SALES -> {
                SalesScreen(
                    sales = sales,
                    searchQuery = salesSearchQuery,
                    onSearchQueryChange = { viewModel.setSalesSearchQuery(it) },
                    statusFilter = salesStatusFilter,
                    onStatusFilterChange = { viewModel.setSalesStatusFilter(it) },
                    canModify = canModify,
                    onNewSaleClick = { viewModel.openCreateSaleDialog() },
                    onViewInvoiceClick = { viewModel.openSaleInvoice(it) },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.PURCHASES -> {
                PurchasesScreen(
                    purchases = purchases,
                    canModify = canModify,
                    onAddExpenseClick = { viewModel.openAddExpenseDialog() },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.CUSTOMERS -> {
                CustomersScreen(
                    customers = customers,
                    canModify = canModify,
                    onAddCustomerClick = { viewModel.openAddCustomerDialog() },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.SUPPLIERS -> {
                SuppliersScreen(
                    suppliers = suppliers,
                    canModify = canModify,
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.STATISTICS -> {
                StatisticsScreen(
                    metrics = metrics,
                    products = allProductsList,
                    onExportReport = { viewModel.showSnackbar("Rapport d'indices exporté avec succès.") },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.SETTINGS -> {
                SettingsScreen(
                    syncInfo = syncInfo,
                    isLoggedIn = isLoggedIn,
                    userName = userName,
                    userEmail = userEmail,
                    companyName = companyName,
                    onSyncClick = { viewModel.syncNow() },
                    onToggleOfflineMode = { viewModel.toggleManualOfflineMode() },
                    onLogout = { viewModel.logout() },
                    onNavigate = { viewModel.setDestination(it) },
                    isWideScreen = isWideScreen
                )
            }
            NavigationDestination.PLUS -> {
                PlusScreen(
                    companyName = companyName,
                    userName = userName,
                    userEmail = userEmail,
                    syncInfo = syncInfo,
                    onNavigate = { viewModel.setDestination(it) },
                    onSyncClick = { viewModel.syncNow() },
                    onExportReport = { viewModel.showSnackbar("Rapport BIZSTOCK généré et prêt à l'export ✓") },
                    onLogout = { viewModel.logout() },
                    isWideScreen = isWideScreen
                )
            }
        }
    }
}

    // Add or Edit Product Dialog
    if (showAddDialog) {
        AddEditProductDialog(
            productToEdit = editingProduct,
            onDismiss = { viewModel.closeProductDialog() },
            onSave = { name, sku, category, buyPrice, sellPrice, stock, alert, unit, desc, img ->
                viewModel.saveProduct(
                    name = name,
                    sku = sku,
                    category = category,
                    purchasePrice = buyPrice,
                    sellingPrice = sellPrice,
                    stock = stock,
                    minAlert = alert,
                    unit = unit,
                    description = desc,
                    imageUri = img
                )
            }
        )
    }

    // View Product Detail Dialog
    selectedProductForDetail?.let { prod ->
        ProductDetailDialog(
            product = prod,
            canModify = canModify,
            onDismiss = { viewModel.closeProductDetail() },
            onEdit = {
                viewModel.closeProductDetail()
                viewModel.openEditProductDialog(prod)
            },
            onDelete = {
                viewModel.closeProductDetail()
                viewModel.confirmDeleteProduct(prod)
            },
            onAdjustStock = {
                viewModel.closeProductDetail()
                viewModel.openAdjustStockDialog(prod)
            }
        )
    }

    // Confirm Delete Dialog
    productToDelete?.let { prod ->
        ConfirmDeleteProductDialog(
            product = prod,
            onDismiss = { viewModel.dismissDeleteDialog() },
            onConfirm = { viewModel.deleteConfirmedProduct() }
        )
    }

    // Adjust Stock Dialog
    adjustingProduct?.let { prod ->
        AdjustStockDialog(
            product = prod,
            onDismiss = { viewModel.closeAdjustStockDialog() },
            onConfirm = { qtyChange, reason ->
                viewModel.adjustStock(qtyChange, reason)
            }
        )
    }

    // BIZSTOCK Cloud Sync & Connection Details Dialog
    if (showSyncDetailsDialog) {
        SyncDetailsDialog(
            syncInfo = syncInfo,
            onDismiss = { viewModel.setShowSyncDetailsDialog(false) },
            onSyncNow = { viewModel.syncNow() },
            onToggleOfflineMode = { viewModel.toggleManualOfflineMode() }
        )
    }

    // Create Sale Dialog
    if (showCreateSaleDialog) {
        CreateSaleDialog(
            products = allProductsList,
            customers = customers,
            onDismiss = { viewModel.closeCreateSaleDialog() },
            onSaveSale = { client, phone, email, status, method, items, notes ->
                viewModel.recordSale(
                    customerName = client,
                    customerPhone = phone,
                    customerEmail = email,
                    paymentStatus = status,
                    paymentMethod = method,
                    items = items,
                    notes = notes
                )
            }
        )
    }

    // View Sale Invoice Dialog
    selectedSaleForInvoice?.let { sale ->
        SaleInvoiceDialog(
            sale = sale,
            onDismiss = { viewModel.closeSaleInvoice() },
            onPrint = { viewModel.showSnackbar("Facture #${sale.invoiceNumber} envoyée vers l'imprimante pro.") },
            onShare = { viewModel.showSnackbar("Facture #${sale.invoiceNumber} prête au partage (PDF / Email).") }
        )
    }

    // Quick Actions Modal Bottom Sheet
    if (showQuickActionsMenu) {
        QuickActionsSheet(
            onDismiss = { viewModel.closeQuickActions() },
            onNewSaleClick = {
                viewModel.closeQuickActions()
                viewModel.openCreateSaleDialog()
            },
            onAddProductClick = {
                viewModel.closeQuickActions()
                viewModel.openAddProductDialog()
            },
            onAddExpenseClick = {
                viewModel.closeQuickActions()
                viewModel.openAddExpenseDialog()
            },
            onAddCustomerClick = {
                viewModel.closeQuickActions()
                viewModel.openAddCustomerDialog()
            }
        )
    }

    // Add Customer Dialog
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { viewModel.closeAddCustomerDialog() },
            onSave = { name, phone, email, company, city ->
                viewModel.saveCustomer(name, phone, email, company, city)
            }
        )
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { viewModel.closeAddExpenseDialog() },
            onSave = { title, amount, category, notes ->
                viewModel.saveExpense(title, amount, category, notes)
            }
        )
    }
}
