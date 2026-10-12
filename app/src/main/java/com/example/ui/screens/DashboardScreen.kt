package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessMetrics
import com.example.data.model.Sale
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandSlate
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.viewmodel.NavigationDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    metrics: BusinessMetrics,
    recentSales: List<Sale>,
    canModify: Boolean,
    isWideScreen: Boolean,
    onNavigate: (NavigationDestination) -> Unit,
    onAddProductClick: () -> Unit,
    onNewSaleClick: () -> Unit = {},
    onSyncClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simple & Professional Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tableau de bord",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Aujourd'hui • Vue d'ensemble de votre activité",
                    fontSize = 13.sp,
                    color = BrandSlateLight
                )
            }
        }

        // Section "Aujourd'hui" : 3 cartes essentielles (pas de surcharge)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Ventes
                SimpleMetricCard(
                    title = "Ventes",
                    value = "${metrics.activityIndex} pts",
                    subtext = "Activité",
                    icon = Icons.Default.PointOfSale,
                    iconColor = BrandBlue,
                    modifier = Modifier.weight(1f)
                )

                // 2. Commandes
                SimpleMetricCard(
                    title = "Commandes",
                    value = "${recentSales.size}",
                    subtext = "Enregistrées",
                    icon = Icons.Default.ShoppingCart,
                    iconColor = StatusOnline,
                    modifier = Modifier.weight(1f)
                )

                // 3. Alertes Stock
                val totalAlerts = metrics.lowStockCount + metrics.outOfStockCount
                SimpleMetricCard(
                    title = "Stock",
                    value = "$totalAlerts",
                    subtext = if (totalAlerts > 0) "Alertes" else "En ordre",
                    icon = if (totalAlerts > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                    iconColor = if (totalAlerts > 0) StatusOffline else StatusOnline,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Actions principales : 2 grands boutons clairs et simples
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // + Nouvelle vente (Action prioritaire)
                Button(
                    onClick = onNewSaleClick,
                    enabled = canModify,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("dashboard_new_sale_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Nouvelle vente", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // + Ajouter un produit
                OutlinedButton(
                    onClick = onAddProductClick,
                    enabled = canModify,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SurfaceBorderLight),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("dashboard_add_product_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Ajouter un produit", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = BrandSlate)
                }
            }
        }

        // Section "Ventes récentes" (3 à 5 éléments, sobre et propre)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ventes récentes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Voir les ventes →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandBlue,
                            modifier = Modifier
                                .clickable { onNavigate(NavigationDestination.SALES) }
                                .padding(4.dp)
                                .testTag("see_all_sales_link")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (recentSales.isEmpty()) {
                        Text(
                            text = "Aucune vente enregistrée pour le moment.",
                            fontSize = 13.sp,
                            color = BrandSlateLight,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        recentSales.take(4).forEachIndexed { index, sale ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(NavigationDestination.SALES) }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (sale.paymentStatus == "PAYÉE") Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.PointOfSale,
                                                contentDescription = null,
                                                tint = if (sale.paymentStatus == "PAYÉE") StatusOnline else StatusOffline,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = sale.customerName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${sale.invoiceNumber} • ${dateFormat.format(Date(sale.date))}",
                                            fontSize = 11.sp,
                                            color = BrandSlateLight
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${sale.totalIndex} pts",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BrandBlue
                                    )
                                    Text(
                                        text = sale.paymentStatus,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (sale.paymentStatus == "PAYÉE") StatusOnline else StatusOffline
                                    )
                                }
                            }
                            if (index < recentSales.take(4).lastIndex) {
                                HorizontalDivider(color = SurfaceBorderLight.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }

        // Accès direct aux 6 sections organisées
        item {
            Text(
                text = "Accès aux rubriques",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModuleQuickCard(
                        title = "Produits & Stock",
                        subtitle = "Articles et inventaire",
                        icon = Icons.Default.Inventory,
                        onClick = { onNavigate(NavigationDestination.PRODUCTS) },
                        modifier = Modifier.weight(1f)
                    )
                    ModuleQuickCard(
                        title = "Ventes",
                        subtitle = "Factures et commandes",
                        icon = Icons.Default.PointOfSale,
                        onClick = { onNavigate(NavigationDestination.SALES) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModuleQuickCard(
                        title = "Achats",
                        subtitle = "Commandes & Fournisseurs",
                        icon = Icons.Default.ShoppingCart,
                        onClick = { onNavigate(NavigationDestination.PURCHASES) },
                        modifier = Modifier.weight(1f)
                    )
                    ModuleQuickCard(
                        title = "Clients",
                        subtitle = "Fichier et comptes",
                        icon = Icons.Default.People,
                        onClick = { onNavigate(NavigationDestination.CUSTOMERS) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModuleQuickCard(
                        title = "Rapports",
                        subtitle = "Statistiques & Résultats",
                        icon = Icons.Default.BarChart,
                        onClick = { onNavigate(NavigationDestination.STATISTICS) },
                        modifier = Modifier.weight(1f)
                    )
                    ModuleQuickCard(
                        title = "Paramètres",
                        subtitle = "Entreprise & Compte",
                        icon = Icons.Default.Settings,
                        onClick = { onNavigate(NavigationDestination.SETTINGS) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleMetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SurfaceBorderLight)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandSlateLight
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = BrandSlateLight
            )
        }
    }
}

@Composable
private fun ModuleQuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
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
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 11.sp, color = BrandSlateLight, maxLines = 1)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = BrandSlateLight.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
