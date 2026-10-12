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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.model.SaleItem
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorBg
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOfflineBg
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.StatusOnlineBg

@Composable
fun CreateSaleDialog(
    products: List<Product>,
    customers: List<Customer>,
    onDismiss: () -> Unit,
    onSaveSale: (
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        paymentStatus: String,
        paymentMethod: String,
        items: List<SaleItem>,
        notes: String
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var selectedPaymentStatus by remember { mutableStateOf("PAYÉE") }
    var selectedPaymentMethod by remember { mutableStateOf("Comptant") }
    var notes by remember { mutableStateOf("") }

    var customerDropdownExpanded by remember { mutableStateOf(false) }

    // Selected sale items list
    val saleItems = remember { mutableStateListOf<SaleItem>() }

    // Add item picker sub-state
    var showProductPicker by remember { mutableStateOf(false) }
    var selectedProductForAdd by remember { mutableStateOf<Product?>(products.firstOrNull { it.currentStock > 0 }) }
    var quantityToAdd by remember { mutableIntStateOf(1) }
    var itemValidationError by remember { mutableStateOf<String?>(null) }
    var formValidationError by remember { mutableStateOf<String?>(null) }

    // Auto-calculate total sale index
    val totalSaleIndex = saleItems.sumOf { it.totalLineIndex }
    val totalItemsCount = saleItems.sumOf { it.quantity }

    val paymentMethods = listOf("Comptant", "Carte Pro", "Virement", "Chèque")
    val paymentStatuses = listOf("PAYÉE", "EN_ATTENTE")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandBlue.copy(alpha = 0.12f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PointOfSale,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Enregistrer une Vente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Décrémentation de stock & indices automatiques",
                        fontSize = 11.sp,
                        color = BrandSlateLight
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Section 1: Client
                Text(
                    text = "1. Sélection du Client *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = {
                            customerName = it
                            formValidationError = null
                        },
                        label = { Text("Nom du Client / Entreprise *") },
                        placeholder = { Text("Ex: Nexora Solutions ou Client Comptoir") },
                        trailingIcon = {
                            IconButton(onClick = { customerDropdownExpanded = !customerDropdownExpanded }) {
                                Icon(Icons.Default.Person, contentDescription = "Choisir client existant")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sale_customer_input"),
                        singleLine = true
                    )

                    DropdownMenu(
                        expanded = customerDropdownExpanded,
                        onDismissRequest = { customerDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("👤 Client Comptoir (Vente directe)") },
                            onClick = {
                                customerName = "Client Comptoir"
                                customerPhone = ""
                                customerEmail = ""
                                customerDropdownExpanded = false
                            }
                        )
                        HorizontalDivider()
                        customers.forEach { cust ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(cust.name, fontWeight = FontWeight.Bold)
                                        Text("${cust.company} • ${cust.phone}", fontSize = 11.sp, color = BrandSlateLight)
                                    }
                                },
                                onClick = {
                                    customerName = cust.name
                                    customerPhone = cust.phone
                                    customerEmail = cust.email
                                    customerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Articles & Quantité
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. Produits & Quantités *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue
                    )

                    TextButton(
                        onClick = {
                            selectedProductForAdd = products.firstOrNull { it.currentStock > 0 }
                            quantityToAdd = 1
                            itemValidationError = null
                            showProductPicker = true
                        },
                        modifier = Modifier.testTag("add_product_to_sale_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ajouter un article", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // If adding product panel
                if (showProductPicker) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Sélectionner un produit en stock :", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Product selection dropdown / list
                            var productDropdownOpen by remember { mutableStateOf(false) }

                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { productDropdownOpen = true }
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = selectedProductForAdd?.name ?: "Sélectionner un produit...",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            selectedProductForAdd?.let { prod ->
                                                Text(
                                                    text = "${prod.sku} • En stock: ${prod.currentStock} ${prod.unit} • Indice: ${prod.priceIndex} pts",
                                                    fontSize = 11.sp,
                                                    color = BrandSlateLight
                                                )
                                            }
                                        }
                                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = BrandBlue)
                                    }
                                }

                                DropdownMenu(
                                    expanded = productDropdownOpen,
                                    onDismissRequest = { productDropdownOpen = false }
                                ) {
                                    products.forEach { prod ->
                                        val isAvailable = prod.currentStock > 0
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column {
                                                        Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                        Text("Réf: ${prod.sku} • Indice: ${prod.priceIndex} pts", fontSize = 11.sp, color = BrandSlateLight)
                                                    }
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(
                                                        text = if (isAvailable) "${prod.currentStock} dispo" else "Rupture",
                                                        color = if (isAvailable) StatusOnline else StatusError,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            },
                                            enabled = isAvailable,
                                            onClick = {
                                                selectedProductForAdd = prod
                                                quantityToAdd = 1
                                                itemValidationError = null
                                                productDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quantity Selector with Stepper
                            selectedProductForAdd?.let { prod ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Quantité à vendre :", fontSize = 11.5.sp, color = BrandSlateLight)
                                        Text(
                                            text = "Stock disponible : ${prod.currentStock} ${prod.unit}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (prod.currentStock >= quantityToAdd) StatusOnline else StatusError
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                if (quantityToAdd > 1) quantityToAdd--
                                            },
                                            enabled = quantityToAdd > 1,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Moins")
                                        }

                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        ) {
                                            Text(
                                                text = "$quantityToAdd",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                if (quantityToAdd < prod.currentStock) {
                                                    quantityToAdd++
                                                    itemValidationError = null
                                                } else {
                                                    itemValidationError = "Stock maximal disponible atteint (${prod.currentStock})"
                                                }
                                            },
                                            enabled = quantityToAdd < prod.currentStock,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Plus")
                                        }
                                    }
                                }

                                // Line calculation preview
                                val lineIndex = prod.priceIndex * quantityToAdd
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Indice unitaire : ${prod.priceIndex} pts", fontSize = 11.sp, color = BrandSlateLight)
                                    Text(
                                        text = "Sous-total ligne : $lineIndex pts",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandBlue
                                    )
                                }
                            }

                            itemValidationError?.let { err ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(err, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showProductPicker = false }) {
                                    Text("Annuler")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        val prod = selectedProductForAdd
                                        if (prod == null) {
                                            itemValidationError = "Veuillez choisir un produit"
                                            return@Button
                                        }
                                        if (quantityToAdd <= 0) {
                                            itemValidationError = "Quantité minimale : 1"
                                            return@Button
                                        }
                                        if (quantityToAdd > prod.currentStock) {
                                            itemValidationError = "Stock insuffisant (disponible : ${prod.currentStock})"
                                            return@Button
                                        }

                                        // Check if already in list
                                        val existingIndex = saleItems.indexOfFirst { it.productId == prod.id }
                                        if (existingIndex >= 0) {
                                            val current = saleItems[existingIndex]
                                            val newQty = current.quantity + quantityToAdd
                                            if (newQty > prod.currentStock) {
                                                itemValidationError = "La quantité cumulée (${newQty}) dépasse le stock disponible (${prod.currentStock})"
                                                return@Button
                                            }
                                            saleItems[existingIndex] = current.copy(
                                                quantity = newQty,
                                                totalLineIndex = prod.priceIndex * newQty
                                            )
                                        } else {
                                            saleItems.add(
                                                SaleItem(
                                                    productId = prod.id,
                                                    productName = prod.name,
                                                    productSku = prod.sku,
                                                    quantity = quantityToAdd,
                                                    unitPrice = prod.sellingPrice,
                                                    unitPriceIndex = prod.priceIndex,
                                                    totalLineIndex = prod.priceIndex * quantityToAdd
                                                )
                                            )
                                        }
                                        showProductPicker = false
                                        formValidationError = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                    modifier = Modifier.testTag("confirm_add_product_to_sale")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Valider la ligne")
                                }
                            }
                        }
                    }
                }

                // Display selected items
                if (saleItems.isEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Aucun article sélectionné. Cliquez sur « Ajouter un article » ci-dessus.",
                            fontSize = 12.sp,
                            color = BrandSlateLight,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        saleItems.forEachIndexed { index, item ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                        Text(
                                            text = "Réf : ${item.productSku} • ${item.quantity}x @ ${item.unitPriceIndex} pts",
                                            fontSize = 11.sp,
                                            color = BrandSlateLight
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${item.totalLineIndex} pts",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = BrandBlue
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { saleItems.removeAt(index) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Supprimer", tint = StatusError, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Totaux & Règlement
                Surface(
                    color = BrandNavyDark,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total articles :", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("$totalItemsCount unités", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("INDICE TOTAL VENTE :", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "$totalSaleIndex pts",
                                color = Color(0xFF00C49F),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: Mode de Règlement & Statut
                Text(
                    text = "3. Mode de Règlement & Statut",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    paymentMethods.forEach { method ->
                        FilterChip(
                            selected = selectedPaymentMethod == method,
                            onClick = { selectedPaymentMethod = method },
                            label = { Text(method, fontSize = 11.5.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paymentStatuses.forEach { status ->
                        val label = if (status == "PAYÉE") "Encaissée / Payée" else "En attente de paiement"
                        FilterChip(
                            selected = selectedPaymentStatus == status,
                            onClick = { selectedPaymentStatus = status },
                            label = { Text(label, fontSize = 11.5.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes / Référence complémentaire
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes & Références (Optionnel)") },
                    placeholder = { Text("Ex: Bon de commande client #489, livraison expresse") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                formValidationError?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = StatusErrorBg,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = err,
                            color = StatusError,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = customerName.trim().ifBlank { "Client Comptoir" }
                    if (saleItems.isEmpty()) {
                        formValidationError = "Veuillez ajouter au moins un produit à la vente"
                        return@Button
                    }

                    onSaveSale(
                        trimmedName,
                        customerPhone.trim(),
                        customerEmail.trim(),
                        selectedPaymentStatus,
                        selectedPaymentMethod,
                        saleItems.toList(),
                        notes.trim()
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("submit_sale_button")
            ) {
                Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ENREGISTRER LA VENTE", fontWeight = FontWeight.Bold)
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
