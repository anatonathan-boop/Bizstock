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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.BrandBlue
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
fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase(Locale.ROOT)) {
        "informatique" -> Icons.Default.Laptop
        "périphériques" -> Icons.Default.Keyboard
        "réseau" -> Icons.Default.Router
        "stockage" -> Icons.Default.Storage
        "audio" -> Icons.Default.Headphones
        "alimentation" -> Icons.Default.Power
        "logistique" -> Icons.Default.QrCodeScanner
        else -> Icons.Default.Inventory2
    }
}

@Composable
fun ProductImageBadge(
    product: Product,
    size: Int = 44,
    modifier: Modifier = Modifier
) {
    val icon = getCategoryIcon(product.category)
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = BrandBlue.copy(alpha = 0.12f),
        modifier = modifier
            .size(size.dp)
            .border(1.dp, BrandBlue.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = product.name,
                tint = BrandBlue,
                modifier = Modifier.size((size * 0.55).dp)
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    canModify: Boolean,
    onViewDetail: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdjustStock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }
    val formattedDate = remember(product.createdAt) { dateFormat.format(Date(product.createdAt)) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetail() }
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Image Badge, SKU, Name & Stock Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    ProductImageBadge(product = product, size = 46)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    RoundedCornerShape(6.dp)
                                )
                            ) {
                                Text(
                                    text = product.sku,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = product.category,
                                fontSize = 11.5.sp,
                                color = BrandSlateLight,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = product.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Stock Badge
                val (badgeBg, badgeText, badgeColor) = when {
                    product.isOutOfStock -> Triple(StatusErrorBg, "Rupture", StatusError)
                    product.isLowStock -> Triple(StatusOfflineBg, "Faible (${product.currentStock})", StatusOffline)
                    else -> Triple(StatusOnlineBg, "En stock (${product.currentStock})", StatusOnline)
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }

            if (product.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = BrandSlateLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Operational Indices Row
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Indice Prix", fontSize = 10.5.sp, color = BrandSlateLight)
                        Text(
                            text = "${product.priceIndex} pts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )
                    }

                    Column {
                        Text(text = "Indice Coût", fontSize = 10.5.sp, color = BrandSlateLight)
                        Text(
                            text = "${product.costIndex} pts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(text = "Seuil Mini", fontSize = 10.5.sp, color = BrandSlateLight)
                        Text(
                            text = "${product.minStockAlert} ${product.unit}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Rentabilité", fontSize = 10.5.sp, color = BrandSlateLight)
                        Text(
                            text = "${product.marginRatio} (${product.performanceRank})",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.margin >= 0) StatusOnline else StatusError
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Creation Date & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ajouté le $formattedDate",
                    fontSize = 11.sp,
                    color = BrandSlateLight
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Detail Action Button
                    IconButton(
                        onClick = onViewDetail,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Voir les détails du produit",
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Stock In/Out Adjustment Button
                    IconButton(
                        onClick = onAdjustStock,
                        enabled = canModify,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Ajuster stock",
                            tint = if (canModify) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Edit button
                    IconButton(
                        onClick = onEdit,
                        enabled = canModify,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier",
                            tint = if (canModify) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Delete button
                    IconButton(
                        onClick = onDelete,
                        enabled = canModify,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = if (canModify) StatusError else Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductTableRow(
    product: Product,
    canModify: Boolean,
    onViewDetail: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdjustStock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }
    val formattedDate = remember(product.createdAt) { dateFormat.format(Date(product.createdAt)) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetail() }
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image / Icon
            ProductImageBadge(product = product, size = 36)

            Spacer(modifier = Modifier.width(10.dp))

            // SKU
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            ) {
                Text(
                    text = product.sku,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Name
            Column(modifier = Modifier.weight(1.8f)) {
                Text(
                    text = product.name,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.category,
                    fontSize = 11.sp,
                    color = BrandSlateLight
                )
            }

            // Available Quantity
            val stockColor = when {
                product.isOutOfStock -> StatusError
                product.isLowStock -> StatusOffline
                else -> StatusOnline
            }
            Column(
                modifier = Modifier.weight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${product.currentStock} ${product.unit}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = stockColor
                )
                Text(
                    text = "Seuil: ${product.minStockAlert}",
                    fontSize = 10.sp,
                    color = BrandSlateLight
                )
            }

            // Price & Cost Indices
            Column(
                modifier = Modifier.weight(1.1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Prix: ${product.priceIndex} pts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
                Text(
                    text = "Coût: ${product.costIndex} pts",
                    fontSize = 11.sp,
                    color = BrandSlateLight
                )
            }

            // Ratio & Rank
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = product.marginRatio,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (product.margin >= 0) StatusOnline else StatusError
                )
                Text(
                    text = product.performanceRank,
                    fontSize = 10.5.sp,
                    color = BrandSlateLight
                )
            }

            // Date
            Text(
                text = formattedDate,
                fontSize = 11.sp,
                color = BrandSlateLight,
                modifier = Modifier.weight(0.9f)
            )

            // Actions (Detail, Stock, Edit, Delete)
            Row(
                modifier = Modifier.weight(1.4f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onViewDetail, modifier = Modifier.size(38.dp)) {
                    Icon(Icons.Default.Info, contentDescription = "Détails", tint = BrandBlue, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onAdjustStock, enabled = canModify, modifier = Modifier.size(38.dp)) {
                    Icon(Icons.Default.SwapVert, contentDescription = "Ajuster Stock", tint = if (canModify) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onEdit, enabled = canModify, modifier = Modifier.size(38.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = if (canModify) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, enabled = canModify, modifier = Modifier.size(38.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = if (canModify) StatusError else Color.Gray.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
