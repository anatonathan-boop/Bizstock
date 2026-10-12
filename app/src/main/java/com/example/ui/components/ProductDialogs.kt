package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.SyncInfo
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorBg
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOfflineBg
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.StatusOnlineBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddEditProductDialog(
    productToEdit: Product?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        sku: String,
        category: String,
        purchasePrice: Double,
        sellingPrice: Double,
        stock: Int,
        minAlert: Int,
        unit: String,
        description: String,
        imageUri: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var sku by remember { mutableStateOf(productToEdit?.sku ?: "") }
    var category by remember { mutableStateOf(productToEdit?.category ?: "Informatique") }
    var purchasePriceStr by remember { mutableStateOf(productToEdit?.purchasePrice?.let { String.format(Locale.ROOT, "%.2f", it) } ?: "") }
    var sellingPriceStr by remember { mutableStateOf(productToEdit?.sellingPrice?.let { String.format(Locale.ROOT, "%.2f", it) } ?: "") }
    var stockStr by remember { mutableStateOf(productToEdit?.currentStock?.toString() ?: "10") }
    var minAlertStr by remember { mutableStateOf(productToEdit?.minStockAlert?.toString() ?: "5") }
    var unit by remember { mutableStateOf(productToEdit?.unit ?: "Unité") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var imageUri by remember { mutableStateOf(productToEdit?.imageUri ?: "") }

    // Validation errors state
    var nameError by remember { mutableStateOf<String?>(null) }
    var skuError by remember { mutableStateOf<String?>(null) }
    var purchasePriceError by remember { mutableStateOf<String?>(null) }
    var sellingPriceError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }
    var minAlertError by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Informatique", "Périphériques", "Réseau", "Stockage", "Audio", "Alimentation", "Logistique", "Autre")
    val units = listOf("Unité", "Kg", "Boîte", "Carton", "Pack", "Litre")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandBlue.copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (productToEdit != null) Icons.Default.Edit else Icons.Default.Add,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (productToEdit != null) "Modifier le produit" else "Nouveau Produit BIZSTOCK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Name Field (Requis)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.trim().length < 2) "Le nom doit comporter au moins 2 caractères" else null
                    },
                    label = { Text("Nom du produit *") },
                    isError = nameError != null,
                    supportingText = {
                        nameError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                // SKU & Unit Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = {
                            sku = it
                            skuError = if (it.trim().isEmpty()) "La référence SKU est obligatoire" else null
                        },
                        label = { Text("Référence (SKU) *") },
                        placeholder = { Text("Ex: REF-2026") },
                        isError = skuError != null,
                        supportingText = {
                            skuError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("product_sku_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unité") },
                        modifier = Modifier.weight(0.8f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Chips
                Text("Catégorie * :", fontSize = 12.sp, color = BrandSlateLight, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(4).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Prices / Cost Inputs
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = purchasePriceStr,
                        onValueChange = {
                            purchasePriceStr = it
                            val parsed = it.toDoubleOrNull()
                            purchasePriceError = if (parsed == null || parsed < 0) "Indice coût invalide" else null
                        },
                        label = { Text("Indice Coût d'achat *") },
                        placeholder = { Text("Ex: 75") },
                        isError = purchasePriceError != null,
                        supportingText = {
                            purchasePriceError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_buy_price_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = sellingPriceStr,
                        onValueChange = {
                            sellingPriceStr = it
                            val parsed = it.toDoubleOrNull()
                            sellingPriceError = if (parsed == null || parsed <= 0) "Indice prix > 0 requis" else null
                        },
                        label = { Text("Indice Prix vente *") },
                        placeholder = { Text("Ex: 120") },
                        isError = sellingPriceError != null,
                        supportingText = {
                            sellingPriceError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_sell_price_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Stock & Alert Threshold Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = {
                            stockStr = it
                            val parsed = it.toIntOrNull()
                            stockError = if (parsed == null || parsed < 0) "Quantité >= 0" else null
                        },
                        label = { Text("Quantité disponible *") },
                        isError = stockError != null,
                        supportingText = {
                            stockError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_stock_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = minAlertStr,
                        onValueChange = {
                            minAlertStr = it
                            val parsed = it.toIntOrNull()
                            minAlertError = if (parsed == null || parsed < 0) "Seuil >= 0" else null
                        },
                        label = { Text("Seuil minimum *") },
                        isError = minAlertError != null,
                        supportingText = {
                            minAlertError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_min_alert_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Image / Illustration selection
                OutlinedTextField(
                    value = imageUri,
                    onValueChange = { imageUri = it },
                    label = { Text("Illustration / URL image (optionnel)") },
                    placeholder = { Text("Laisser vide pour icône automatique par catégorie") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & spécifications techniques") },
                    placeholder = { Text("Détails du produit, modèle, compatibilité...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val trimmedSku = if (sku.isBlank()) "PRD-${System.currentTimeMillis() % 10000}" else sku.trim()
                    val buyPrice = purchasePriceStr.toDoubleOrNull()
                    val sellPrice = sellingPriceStr.toDoubleOrNull()
                    val stock = stockStr.toIntOrNull()
                    val minAlert = minAlertStr.toIntOrNull()

                    // Validate
                    var hasError = false
                    if (trimmedName.length < 2) {
                        nameError = "Nom requis (au moins 2 caractères)"
                        hasError = true
                    }
                    if (buyPrice == null || buyPrice < 0) {
                        purchasePriceError = "Coût d'achat valide requis"
                        hasError = true
                    }
                    if (sellPrice == null || sellPrice <= 0) {
                        sellingPriceError = "Prix de vente valide requis (> 0)"
                        hasError = true
                    }
                    if (stock == null || stock < 0) {
                        stockError = "Quantité >= 0 requise"
                        hasError = true
                    }
                    if (minAlert == null || minAlert < 0) {
                        minAlertError = "Seuil >= 0 requis"
                        hasError = true
                    }

                    if (!hasError) {
                        onSave(
                            trimmedName,
                            trimmedSku,
                            category,
                            buyPrice!!,
                            sellPrice!!,
                            stock!!,
                            minAlert!!,
                            unit.trim().ifBlank { "Unité" },
                            description.trim(),
                            imageUri.trim()
                        )
                    }
                },
                modifier = Modifier
                    .height(48.dp)
                    .testTag("save_product_button"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
            ) {
                Text(if (productToEdit != null) "Mettre à jour" else "Enregistrer le produit")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun ProductDetailDialog(
    product: Product,
    canModify: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdjustStock: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE) }
    val formattedDate = remember(product.createdAt) { dateFormat.format(Date(product.createdAt)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProductImageBadge(product = product, size = 48)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "Réf : ${product.sku} • ${product.category}",
                            fontSize = 12.sp,
                            color = BrandSlateLight
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Stock Availability Banner
                val (badgeBg, badgeText, badgeColor) = when {
                    product.isOutOfStock -> Triple(StatusErrorBg, "Rupture de Stock", StatusError)
                    product.isLowStock -> Triple(StatusOfflineBg, "Niveau Faible (${product.currentStock} ${product.unit})", StatusOffline)
                    else -> Triple(StatusOnlineBg, "En Stock Disponible (${product.currentStock} ${product.unit})", StatusOnline)
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = badgeText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Specifications Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Indicateurs & Spécifications", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow("Indice Prix Vente", "${product.priceIndex} pts")
                        DetailRow("Indice Coût Achat", "${product.costIndex} pts")
                        DetailRow("Ratio Rentabilité", "${product.marginRatio} (${product.performanceRank})")
                        DetailRow("Quantité Disponible", "${product.currentStock} ${product.unit}")
                        DetailRow("Seuil Alerte Minimum", "${product.minStockAlert} ${product.unit}")
                        DetailRow("Date de Création", formattedDate)
                    }
                }

                if (product.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Description :", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.description,
                        fontSize = 12.sp,
                        color = BrandSlateLight,
                        lineHeight = 16.sp
                    )
                }

                if (!canModify) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = StatusOfflineBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = StatusOffline, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mode Hors ligne actif : consultation seule autorisée.",
                                fontSize = 11.5.sp,
                                color = StatusOffline,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Stock adjustment action
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onAdjustStock()
                    },
                    enabled = canModify,
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stock")
                }

                // Edit action
                Button(
                    onClick = {
                        onDismiss()
                        onEdit()
                    },
                    enabled = canModify,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Modifier")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Fermer")
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = BrandSlateLight)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ConfirmDeleteProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = StatusError,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Supprimer ce produit ?", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text("Vous êtes sur le point de supprimer le produit :")
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "« ${product.name} » (${product.sku})",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Cette action retirera le produit du catalogue BIZSTOCK.",
                    fontSize = 12.sp,
                    color = BrandSlateLight
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("confirm_delete_button")
            ) {
                Text("Supprimer définitivement")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun AdjustStockDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (quantityChange: Int, reason: String) -> Unit
) {
    var isAddition by remember { mutableStateOf(true) }
    var quantityStr by remember { mutableStateOf("1") }
    var reason by remember { mutableStateOf("Réapprovisionnement") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val reasons = listOf(
        "Réapprovisionnement",
        "Vente au comptoir",
        "Inventaire physique",
        "Casse / Défectueux",
        "Retour client"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ajuster le stock", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Stock actuel : ${product.currentStock} ${product.unit}",
                    fontSize = 13.sp,
                    color = BrandSlateLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Entry vs Exit
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { isAddition = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAddition) StatusOnline else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isAddition) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Entrée (+)")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { isAddition = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAddition) StatusError else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (!isAddition) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sortie (-)")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Quantité à ${if (isAddition) "ajouter" else "retirer"}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adjust_stock_qty_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Motif :", fontSize = 12.sp, color = BrandSlateLight)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    reasons.take(3).forEach { r ->
                        FilterChip(
                            selected = reason == r,
                            onClick = { reason = r },
                            label = { Text(r, fontSize = 10.5.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityStr.toIntOrNull()
                    if (qty == null || qty <= 0) {
                        errorMsg = "Quantité invalide."
                        return@Button
                    }
                    val finalQty = if (isAddition) qty else -qty
                    onConfirm(finalQty, reason)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("confirm_adjust_stock_button")
            ) {
                Text("Appliquer")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun SyncDetailsDialog(
    syncInfo: SyncInfo,
    onDismiss: () -> Unit,
    onSyncNow: () -> Unit,
    onToggleOfflineMode: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE) }
    val lastSyncStr = remember(syncInfo.lastSyncTimestamp) {
        dateFormat.format(Date(syncInfo.lastSyncTimestamp))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("BIZSTOCK Cloud", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = if (syncInfo.isEffectivelyOnline) StatusOnlineBg else StatusOfflineBg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (syncInfo.isEffectivelyOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (syncInfo.isEffectivelyOnline) StatusOnline else StatusOffline,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (syncInfo.isEffectivelyOnline) "Connecté au Cloud BIZSTOCK" else "Mode Hors ligne actif",
                                fontWeight = FontWeight.Bold,
                                color = if (syncInfo.isEffectivelyOnline) StatusOnline else StatusOffline,
                                fontSize = 14.sp
                            )
                            Text(
                                text = syncInfo.syncLogMessage,
                                fontSize = 11.5.sp,
                                color = BrandSlateLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dernière synchronisation :", fontSize = 12.sp, color = BrandSlateLight)
                    Text(lastSyncStr, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Réseau physique :", fontSize = 12.sp, color = BrandSlateLight)
                    Text(
                        if (syncInfo.isOnline) "Actif (Internet OK)" else "Déconnecté",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (syncInfo.isOnline) StatusOnline else StatusError
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Offline Simulation Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mode Hors ligne de test",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Simule la perte de connexion pour tester le verrouillage des ajouts/modifications et la consultation locale.",
                            fontSize = 11.sp,
                            color = BrandSlateLight,
                            lineHeight = 14.sp
                        )
                    }
                    Switch(
                        checked = syncInfo.isManualOfflineMode,
                        onCheckedChange = { onToggleOfflineMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StatusOffline,
                            checkedTrackColor = StatusOffline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("offline_simulation_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Règles BIZSTOCK : En mode hors ligne, la consultation des produits, stocks, ventes et clients est totale. Les ajouts/modifications/suppressions sont verrouillés. La synchronisation redémarre automatiquement au rétablissement du réseau.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSyncNow,
                enabled = syncInfo.isEffectivelyOnline && !syncInfo.isSyncing,
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("trigger_sync_button")
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (syncInfo.isSyncing) "Synchronisation..." else "Synchroniser")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Fermer")
            }
        }
    )
}
