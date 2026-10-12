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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.data.model.SyncInfo
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandSlate
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.viewmodel.NavigationDestination

@Composable
fun PlusScreen(
    companyName: String,
    userName: String,
    userEmail: String,
    syncInfo: SyncInfo,
    onNavigate: (NavigationDestination) -> Unit,
    onSyncClick: () -> Unit,
    onExportReport: () -> Unit,
    onLogout: () -> Unit,
    isWideScreen: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("plus_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // En-tête : Titre & Carte Entreprise
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Plus d'outils",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavyDark
                )
                Text(
                    text = "Gestion avancée, rapports et configuration",
                    fontSize = 13.sp,
                    color = BrandSlateLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavyDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandBlue,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = companyName.ifBlank { "BIZSTOCK Entreprise" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = userName,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SaaS Pro",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 1 : Gestion Commerciale
        item {
            PlusCategoryCard(
                categoryTitle = "GESTION COMMERCIALE",
                items = listOf(
                    PlusMenuItem(
                        title = "Clients",
                        subtitle = "Répertoire, historique d'achats et soldes",
                        icon = Icons.Default.People,
                        iconColor = BrandBlue,
                        onClick = { onNavigate(NavigationDestination.CUSTOMERS) }
                    ),
                    PlusMenuItem(
                        title = "Dépenses & Achats",
                        subtitle = "Suivi des charges, commandes fournisseurs",
                        icon = Icons.Default.ShoppingCart,
                        iconColor = Color(0xFFF59E0B),
                        onClick = { onNavigate(NavigationDestination.PURCHASES) }
                    ),
                    PlusMenuItem(
                        title = "Fournisseurs",
                        subtitle = "Carnet d'adresses et conditions partenaires",
                        icon = Icons.Default.Business,
                        iconColor = Color(0xFF8B5CF6),
                        onClick = { onNavigate(NavigationDestination.SUPPLIERS) }
                    )
                )
            )
        }

        // Section 2 : Performance & Rapports
        item {
            PlusCategoryCard(
                categoryTitle = "ANALYSES & RAPPORTS",
                items = listOf(
                    PlusMenuItem(
                        title = "Statistiques & Marges",
                        subtitle = "Indicateurs de rentabilité et évolution des ventes",
                        icon = Icons.Default.BarChart,
                        iconColor = Color(0xFF06B6D4),
                        onClick = { onNavigate(NavigationDestination.STATISTICS) }
                    ),
                    PlusMenuItem(
                        title = "Rapports d'activité",
                        subtitle = "Synthèse d'exploitation et export comptable",
                        icon = Icons.Default.Assessment,
                        iconColor = Color(0xFF10B981),
                        onClick = onExportReport
                    )
                )
            )
        }

        // Section 3 : SaaS & Abonnement
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SurfaceBorderLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "MON ABONNEMENT SAAS",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandSlateLight,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Formule Illimitée Pro",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavyDark
                            )
                            Text(
                                text = "Accès multi-utilisateurs • Synchronisation Cloud",
                                fontSize = 12.sp,
                                color = BrandSlateLight
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "Actif ✓",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 4 : Paramètres & Système
        item {
            PlusCategoryCard(
                categoryTitle = "SYSTÈME & ASSISTANCE",
                items = listOf(
                    PlusMenuItem(
                        title = "Synchronisation Cloud",
                        subtitle = if (syncInfo.isEffectivelyOnline) "Synchronisé avec Firebase Cloud" else "Mode hors ligne actif",
                        icon = Icons.Default.CloudSync,
                        iconColor = if (syncInfo.isEffectivelyOnline) StatusOnline else Color(0xFFF59E0B),
                        onClick = onSyncClick
                    ),
                    PlusMenuItem(
                        title = "Paramètres de l'entreprise",
                        subtitle = "Préférences, sécurité et profil",
                        icon = Icons.Default.Settings,
                        iconColor = BrandSlate,
                        onClick = { onNavigate(NavigationDestination.SETTINGS) }
                    ),
                    PlusMenuItem(
                        title = "Déconnexion",
                        subtitle = "Fermer la session actuelle en toute sécurité",
                        icon = Icons.Default.Logout,
                        iconColor = StatusError,
                        onClick = onLogout
                    )
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private data class PlusMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val onClick: () -> Unit
)

@Composable
private fun PlusCategoryCard(
    categoryTitle: String,
    items: List<PlusMenuItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SurfaceBorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = categoryTitle,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = BrandSlateLight,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = item.onClick)
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = item.iconColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavyDark
                        )
                        Text(
                            text = item.subtitle,
                            fontSize = 12.sp,
                            color = BrandSlateLight
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = BrandSlateLight.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (index < items.size - 1) {
                    HorizontalDivider(
                        color = SurfaceBorderLight,
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(start = 50.dp)
                    )
                }
            }
        }
    }
}
