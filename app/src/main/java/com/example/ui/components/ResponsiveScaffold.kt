package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SyncInfo
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandSlate
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOnline
import com.example.ui.viewmodel.NavigationDestination
import kotlinx.coroutines.launch

data class NavigationSection(
    val categoryTitle: String,
    val items: List<NavDestinationItem>
)

data class NavDestinationItem(
    val destination: NavigationDestination,
    val icon: ImageVector,
    val label: String
)

val navigationSections = listOf(
    NavigationSection(
        categoryTitle = "VUE GÉNÉRALE",
        items = listOf(
            NavDestinationItem(NavigationDestination.HOME, Icons.Default.Home, "Accueil"),
            NavDestinationItem(NavigationDestination.DASHBOARD, Icons.Default.Dashboard, "Tableau de bord"),
            NavDestinationItem(NavigationDestination.STATISTICS, Icons.Default.BarChart, "Statistiques")
        )
    ),
    NavigationSection(
        categoryTitle = "CATALOGUE & STOCK",
        items = listOf(
            NavDestinationItem(NavigationDestination.PRODUCTS, Icons.Default.Inventory2, "Produits"),
            NavDestinationItem(NavigationDestination.STOCK, Icons.Default.Inventory, "Stock")
        )
    ),
    NavigationSection(
        categoryTitle = "OPÉRATIONS",
        items = listOf(
            NavDestinationItem(NavigationDestination.SALES, Icons.Default.PointOfSale, "Ventes"),
            NavDestinationItem(NavigationDestination.PURCHASES, Icons.Default.ShoppingCart, "Achats")
        )
    ),
    NavigationSection(
        categoryTitle = "RÉPERTOIRE B2B",
        items = listOf(
            NavDestinationItem(NavigationDestination.CUSTOMERS, Icons.Default.People, "Clients"),
            NavDestinationItem(NavigationDestination.SUPPLIERS, Icons.Default.Business, "Fournisseurs")
        )
    ),
    NavigationSection(
        categoryTitle = "COMPTE & SYSTÈME",
        items = listOf(
            NavDestinationItem(NavigationDestination.SETTINGS, Icons.Default.Settings, "Paramètres"),
            NavDestinationItem(NavigationDestination.LOGIN, Icons.Default.LockOpen, "Connexion"),
            NavDestinationItem(NavigationDestination.REGISTER, Icons.Default.HowToReg, "Inscription")
        )
    )
)

val mobileBottomBarItems = listOf(
    NavDestinationItem(NavigationDestination.HOME, Icons.Default.Home, "Accueil"),
    NavDestinationItem(NavigationDestination.DASHBOARD, Icons.Default.Dashboard, "Tableau"),
    NavDestinationItem(NavigationDestination.PRODUCTS, Icons.Default.Inventory2, "Produits"),
    NavDestinationItem(NavigationDestination.STOCK, Icons.Default.Inventory, "Stock"),
    NavDestinationItem(NavigationDestination.STATISTICS, Icons.Default.BarChart, "Stats")
)

@Composable
fun ResponsiveScaffold(
    currentDestination: NavigationDestination,
    onNavigate: (NavigationDestination) -> Unit,
    syncInfo: SyncInfo,
    isLoggedIn: Boolean,
    userName: String,
    onSyncClick: () -> Unit,
    onOpenSyncDetails: () -> Unit,
    onToggleOfflineMode: () -> Unit,
    onQuickActionClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    lowStockCount: Int,
    content: @Composable (isWideScreen: Boolean) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 760.dp

        if (isWideScreen) {
            // ==================== PC / DESKTOP LAYOUT (SIDEBAR GAUCHE) ====================
            Row(modifier = Modifier.fillMaxSize()) {
                // Desktop Left Sidebar
                Surface(
                    modifier = Modifier
                        .width(270.dp)
                        .fillMaxHeight(),
                    color = BrandNavyDark
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Desktop Brand Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(NavigationDestination.HOME) }
                                .padding(bottom = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.bizstock_app_icon_1791310032354),
                                contentDescription = "Logo BIZSTOCK",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "BIZSTOCK",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "SIMPLE • EFFICACE • DURABLE",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00C49F),
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        // Network Connectivity Chip in Sidebar
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenSyncDetails() },
                            color = if (syncInfo.isEffectivelyOnline) Color(0xFF132A22) else Color(0xFF33200B)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (syncInfo.isEffectivelyOnline) StatusOnline else StatusOffline)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (syncInfo.isEffectivelyOnline) "Cloud BIZSTOCK connecté" else "Mode Hors ligne actif",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (syncInfo.isEffectivelyOnline) StatusOnline else StatusOffline
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BrandSlate.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Full Navigation Categories on PC
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            navigationSections.forEach { section ->
                                Text(
                                    text = section.categoryTitle,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.45f),
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 10.dp, bottom = 4.dp)
                                )

                                section.items.forEach { item ->
                                    val isSelected = currentDestination == item.destination
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onNavigate(item.destination) }
                                            .testTag("pc_nav_${item.destination.name.lowercase()}"),
                                        color = if (isSelected) BrandBlue else Color.Transparent
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.label,
                                                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = item.label,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.weight(1f)
                                            )

                                            if (item.destination == NavigationDestination.STOCK && lowStockCount > 0) {
                                                Surface(
                                                    color = StatusOffline,
                                                    shape = CircleShape
                                                ) {
                                                    Text(
                                                        text = "$lowStockCount",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Offline quick test toggle in PC sidebar
                        HorizontalDivider(color = BrandSlate.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Simuler Hors ligne",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = "Test mode déconnecté",
                                    fontSize = 9.5.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                            Switch(
                                checked = syncInfo.isManualOfflineMode,
                                onCheckedChange = { onToggleOfflineMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = StatusOffline,
                                    checkedTrackColor = StatusOffline.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }

                VerticalDivider(color = BrandSlate.copy(alpha = 0.3f))

                // Desktop Right Content
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        BizStockTopBar(
                            syncInfo = syncInfo,
                            onSyncClick = onSyncClick,
                            onOpenSyncDetails = onOpenSyncDetails,
                            onToggleOfflineMode = onToggleOfflineMode,
                            onOpenMobileMenu = null
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        OfflineBanner(
                            isOffline = !syncInfo.isEffectivelyOnline,
                            onSimulateOnlineClick = onOpenSyncDetails
                        )
                        Box(modifier = Modifier.fillMaxSize()) {
                            content(true)
                        }
                    }
                }
            }
        } else {
            // ==================== MOBILE LAYOUT (TACTILE & MENU MOBILE) ====================
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(300.dp),
                        drawerContainerColor = BrandNavyDark,
                        drawerContentColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Mobile Drawer Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.bizstock_app_icon_1791310032354),
                                        contentDescription = "Logo BIZSTOCK",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "BIZSTOCK",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "SIMPLE • EFFICACE • DURABLE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00C49F),
                                            letterSpacing = 0.6.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { scope.launch { drawerState.close() } },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Fermer le menu",
                                        tint = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = BrandSlate.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Scrollable list of all 12 items in mobile drawer
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                navigationSections.forEach { section ->
                                    Text(
                                        text = section.categoryTitle,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.45f),
                                        letterSpacing = 0.8.sp,
                                        modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp)
                                    )

                                    section.items.forEach { item ->
                                        val isSelected = currentDestination == item.destination
                                        NavigationDrawerItem(
                                            icon = {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.label,
                                                    tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = item.label,
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f)
                                                )
                                            },
                                            badge = {
                                                if (item.destination == NavigationDestination.STOCK && lowStockCount > 0) {
                                                    Surface(color = StatusOffline, shape = CircleShape) {
                                                        Text(
                                                            text = "$lowStockCount",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White,
                                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            selected = isSelected,
                                            onClick = {
                                                onNavigate(item.destination)
                                                scope.launch { drawerState.close() }
                                            },
                                            colors = NavigationDrawerItemDefaults.colors(
                                                selectedContainerColor = BrandBlue,
                                                unselectedContainerColor = Color.Transparent
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                                .testTag("drawer_item_${item.destination.name.lowercase()}")
                                        )
                                    }
                                }
                            }

                            // Bottom drawer connectivity toggle
                            HorizontalDivider(color = BrandSlate.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Simuler Hors ligne",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (syncInfo.isEffectivelyOnline) "En ligne (connecté)" else "Hors ligne actif",
                                        fontSize = 10.sp,
                                        color = if (syncInfo.isEffectivelyOnline) StatusOnline else StatusOffline
                                    )
                                }
                                Switch(
                                    checked = syncInfo.isManualOfflineMode,
                                    onCheckedChange = { onToggleOfflineMode() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = StatusOffline,
                                        checkedTrackColor = StatusOffline.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        }
                    }
                }
            ) {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        BizStockTopBar(
                            syncInfo = syncInfo,
                            onSyncClick = onSyncClick,
                            onOpenSyncDetails = onOpenSyncDetails,
                            onToggleOfflineMode = onToggleOfflineMode,
                            onOpenMobileMenu = { scope.launch { drawerState.open() } }
                        )
                    },
                    bottomBar = {
                        Surface(
                            color = BrandNavyDark,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .testTag("mobile_bottom_bar")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Accueil
                                BottomNavItem(
                                    label = "Accueil",
                                    icon = Icons.Default.Home,
                                    isSelected = currentDestination == NavigationDestination.HOME,
                                    onClick = { onNavigate(NavigationDestination.HOME) },
                                    tag = "bottom_nav_home"
                                )

                                // 2. Ventes
                                BottomNavItem(
                                    label = "Ventes",
                                    icon = Icons.Default.PointOfSale,
                                    isSelected = currentDestination == NavigationDestination.SALES,
                                    onClick = { onNavigate(NavigationDestination.SALES) },
                                    tag = "bottom_nav_sales"
                                )

                                // 3. BOUTON "+" CENTRAL VISUELLEMENT IMPORTANT
                                Surface(
                                    shape = CircleShape,
                                    color = BrandBlue,
                                    shadowElevation = 6.dp,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clickable(onClick = onQuickActionClick)
                                        .testTag("bottom_nav_quick_add")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Actions rapides",
                                            tint = Color.White,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }

                                // 4. Stock
                                BottomNavItem(
                                    label = "Stock",
                                    icon = Icons.Default.Inventory,
                                    isSelected = currentDestination == NavigationDestination.STOCK,
                                    onClick = { onNavigate(NavigationDestination.STOCK) },
                                    tag = "bottom_nav_stock",
                                    badgeCount = lowStockCount
                                )

                                // 5. Plus
                                BottomNavItem(
                                    label = "Plus",
                                    icon = Icons.Default.GridView,
                                    isSelected = currentDestination == NavigationDestination.PLUS,
                                    onClick = { onNavigate(NavigationDestination.PLUS) },
                                    tag = "bottom_nav_plus"
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        OfflineBanner(
                            isOffline = !syncInfo.isEffectivelyOnline,
                            onSimulateOnlineClick = onOpenSyncDetails
                        )
                        Box(modifier = Modifier.fillMaxSize()) {
                            content(false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String,
    badgeCount: Int = 0
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) BrandBlueLight else Color.White.copy(alpha = 0.65f),
                modifier = Modifier.size(22.dp)
            )
            if (badgeCount > 0) {
                Surface(
                    shape = CircleShape,
                    color = StatusOffline,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (badgeCount > 9) "9+" else "$badgeCount",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f)
        )
    }
}
