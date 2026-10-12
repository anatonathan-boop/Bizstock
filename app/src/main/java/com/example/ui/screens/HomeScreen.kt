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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessMetrics
import com.example.data.model.Product
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandSlate
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.util.FormatUtils
import com.example.ui.viewmodel.NavigationDestination

@Composable
fun HomeScreen(
    metrics: BusinessMetrics,
    userName: String,
    companyName: String,
    lowStockProducts: List<Product> = emptyList(),
    canModify: Boolean,
    onNavigate: (NavigationDestination) -> Unit,
    onNewSaleClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    isWideScreen: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Salutation & En-tête
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bonjour 👋",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandSlateLight
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userName.ifBlank { "Utilisateur" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavyDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Voici l'état de votre activité aujourd'hui.",
                    fontSize = 14.sp,
                    color = BrandSlateLight
                )
            }
        }

        // 2. Les 4 Cartes Modernes de KPIs
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Carte VENTES DU JOUR
                    KpiCard(
                        title = "VENTES DU JOUR",
                        value = FormatUtils.formatFcfa(if (metrics.todayRevenue > 0) metrics.todayRevenue else metrics.totalRevenue),
                        icon = Icons.Default.PointOfSale,
                        iconColor = BrandBlue,
                        iconBg = BrandBlue.copy(alpha = 0.12f),
                        tag = "kpi_sales",
                        modifier = Modifier.weight(1f)
                    )

                    // Carte STOCK
                    KpiCard(
                        title = "STOCK",
                        value = "${metrics.totalProductsCount} produits",
                        icon = Icons.Default.Inventory2,
                        iconColor = StatusOnline,
                        iconBg = StatusOnline.copy(alpha = 0.12f),
                        tag = "kpi_stock",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Carte DÉPENSES
                    KpiCard(
                        title = "DÉPENSES",
                        value = FormatUtils.formatFcfa(metrics.totalPurchases),
                        icon = Icons.Default.ShoppingCart,
                        iconColor = StatusOffline,
                        iconBg = StatusOffline.copy(alpha = 0.12f),
                        tag = "kpi_expenses",
                        modifier = Modifier.weight(1f)
                    )

                    // Carte BÉNÉFICE
                    KpiCard(
                        title = "BÉNÉFICE",
                        value = FormatUtils.formatFcfa(metrics.netProfit),
                        icon = Icons.Default.TrendingUp,
                        iconColor = if (metrics.netProfit >= 0) StatusOnline else StatusError,
                        iconBg = (if (metrics.netProfit >= 0) StatusOnline else StatusError).copy(alpha = 0.12f),
                        tag = "kpi_profit",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Actions Rapides
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Actions rapides",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavyDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bouton Nouvelle Vente
                    Button(
                        onClick = onNewSaleClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("action_new_sale"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PointOfSale,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nouvelle vente",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Bouton Ajouter Produit
                    OutlinedButton(
                        onClick = onAddProductClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("action_add_product"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, BrandBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ajouter produit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )
                    }
                }

                // Bouton Ajouter Dépense
                OutlinedButton(
                    onClick = onAddExpenseClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("action_add_expense"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SurfaceBorderLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = StatusOffline,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ajouter une dépense",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandNavyDark
                    )
                }
            }
        }

        // 4. Section Alertes
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Alertes",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavyDark
                )

                val hasAlerts = metrics.lowStockCount > 0 || metrics.outOfStockCount > 0

                if (hasAlerts) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(NavigationDestination.STOCK) }
                            .testTag("alert_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = StatusError.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = StatusError,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Attention au stock",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                                val alertText = buildString {
                                    if (metrics.outOfStockCount > 0) {
                                        append("${metrics.outOfStockCount} produit(s) en rupture")
                                    }
                                    if (metrics.lowStockCount > 0) {
                                        if (isNotEmpty()) append(" • ")
                                        append("${metrics.lowStockCount} produit(s) en stock faible")
                                    }
                                }
                                Text(
                                    text = alertText,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Voir le stock",
                                tint = Color(0xFFB91C1C),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = StatusOnline.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusOnline,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Tout est en ordre ✓",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                                Text(
                                    text = "Tous vos stocks sont au niveau optimal.",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    tag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SurfaceBorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandSlateLight,
                    letterSpacing = 0.5.sp
                )
                Surface(
                    shape = CircleShape,
                    color = iconBg,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BrandNavyDark,
                maxLines = 1
            )
        }
    }
}
