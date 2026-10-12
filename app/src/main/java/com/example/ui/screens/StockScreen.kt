package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.StockMovement
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandSlate
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockScreen(
    products: List<Product>,
    movements: List<StockMovement> = emptyList(),
    canModify: Boolean,
    onAddProductClick: () -> Unit = {},
    onEditProductClick: (Product) -> Unit = {},
    onAdjustStockClick: (Product) -> Unit = {},
    onViewProductDetail: (Product) -> Unit = {},
    onDeleteProductClick: (Product) -> Unit = {},
    isWideScreen: Boolean
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Toutes") }
    var showOnlyLowStock by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE) }

    val categories = remember(products) {
        listOf("Toutes") + products.map { it.category }.distinct().filter { it.isNotBlank() }
    }

    val filteredProducts = remember(products, searchQuery, selectedCategory, showOnlyLowStock) {
        products.filter { prod ->
            val matchesQuery = prod.name.contains(searchQuery, ignoreCase = true) ||
                    prod.sku.contains(searchQuery, ignoreCase = true) ||
                    prod.category.contains(searchQuery, ignoreCase = true)
            val matchesCat = selectedCategory == "Toutes" || prod.category.equals(selectedCategory, ignoreCase = true)
            val matchesAlert = !showOnlyLowStock || prod.isLowStock || prod.isOutOfStock
            matchesQuery && matchesCat && matchesAlert
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("stock_screen")
    ) {
        // En-tête : Titre + Bouton "+ Ajouter"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Stock",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavyDark
                    )
                    Text(
                        text = "${products.size} article(s) inventorié(s)",
                        fontSize = 13.sp,
                        color = BrandSlateLight
                    )
                }

                Button(
                    onClick = onAddProductClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    modifier = Modifier.testTag("stock_add_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ajouter",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Onglets Produits vs Mouvements
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFF1F5F9),
                contentColor = BrandNavyDark,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Produits & Niveaux",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Mouvements (${movements.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                Spacer(modifier = Modifier.height(10.dp))

                // Recherche simple
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Rechercher un produit, référence...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = BrandSlateLight,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer",
                                    tint = BrandSlateLight
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stock_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filtres de catégories + filtre alerte
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            selected = showOnlyLowStock,
                            onClick = { showOnlyLowStock = !showOnlyLowStock },
                            label = {
                                Text(
                                    text = "Alertes Stock",
                                    fontSize = 12.sp,
                                    fontWeight = if (showOnlyLowStock) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFEF2F2),
                                selectedLabelColor = Color(0xFF991B1B),
                                selectedLeadingIconColor = Color(0xFF991B1B)
                            )
                        )
                    }

                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat && !showOnlyLowStock
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategory = cat
                                showOnlyLowStock = false
                            },
                            label = {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandNavyDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Contenu : Liste de cartes simples
        if (selectedTab == 0) {
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = BrandSlateLight,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Aucun produit trouvé",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Appuyez sur '+ Ajouter' pour créer un produit.",
                            fontSize = 13.sp,
                            color = BrandSlateLight
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductStockCard(
                            product = product,
                            canModify = canModify,
                            onClick = { onViewProductDetail(product) },
                            onEditClick = { onEditProductClick(product) },
                            onAdjustClick = { onAdjustStockClick(product) }
                        )
                    }
                }
            }
        } else {
            // Onglet Mouvements
            if (movements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun mouvement de stock enregistré.",
                        fontSize = 14.sp,
                        color = BrandSlateLight
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(movements, key = { it.id }) { movement ->
                        val isEntry = movement.type == "ENTRÉE" || movement.quantity > 0
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, SurfaceBorderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isEntry) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isEntry) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = if (isEntry) Color(0xFF15803D) else Color(0xFFB91C1C),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = movement.productName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandNavyDark
                                    )
                                    Text(
                                        text = "${movement.reason} • ${dateFormat.format(Date(movement.date))}",
                                        fontSize = 11.5.sp,
                                        color = BrandSlateLight
                                    )
                                }

                                Text(
                                    text = if (isEntry) "+${movement.quantity}" else "-${movement.quantity}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEntry) Color(0xFF15803D) else Color(0xFFB91C1C)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductStockCard(
    product: Product,
    canModify: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onAdjustClick: () -> Unit
) {
    val isOutOfStock = product.isOutOfStock
    val isLowStock = product.isLowStock

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_stock_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            1.dp,
            if (isOutOfStock) Color(0xFFFECACA) else if (isLowStock) Color(0xFFFED7AA) else SurfaceBorderLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Ligne 1 : Nom du produit + Prix
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Produit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandSlateLight
                    )
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavyDark
                    )
                    if (product.category.isNotBlank()) {
                        Text(
                            text = product.category,
                            fontSize = 12.sp,
                            color = BrandSlateLight
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Prix",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandSlateLight
                    )
                    Text(
                        text = FormatUtils.formatFcfa(product.sellingPrice),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavyDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ligne 2 : Stock actuel avec alerte claire + boutons actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stock : ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandSlate
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            isOutOfStock -> Color(0xFFFEE2E2)
                            isLowStock -> Color(0xFFFEF3C7)
                            else -> Color(0xFFDCFCE7)
                        }
                    ) {
                        Text(
                            text = "${product.currentStock} ${product.unit}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isOutOfStock -> Color(0xFFB91C1C)
                                isLowStock -> Color(0xFFB45309)
                                else -> Color(0xFF15803D)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (isOutOfStock || isLowStock) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOutOfStock) "Rupture" else "Faible",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOutOfStock) StatusError else StatusOffline
                        )
                    }
                }

                // Actions rapides
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onAdjustClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Ajuster",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ajuster", fontSize = 11.5.sp)
                    }

                    OutlinedButton(
                        onClick = onEditClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
