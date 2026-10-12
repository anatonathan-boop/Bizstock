package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncInfo
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOfflineBg
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.StatusOnlineBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SyncSettingsScreen(
    syncInfo: SyncInfo,
    onSyncClick: () -> Unit,
    onToggleOfflineMode: () -> Unit,
    isWideScreen: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE) }
    val lastSyncFormatted = remember(syncInfo.lastSyncTimestamp) {
        dateFormat.format(Date(syncInfo.lastSyncTimestamp))
    }

    val isEffectivelyOnline = syncInfo.isEffectivelyOnline

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("sync_settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        Column {
            Text(
                text = "Cloud BIZSTOCK & Synchronisation",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gestion de l'architecture déconnectée et réplication cloud",
                fontSize = 12.sp,
                color = BrandSlateLight
            )
        }

        // Main Connection Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isEffectivelyOnline) StatusOnlineBg else StatusOfflineBg
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isEffectivelyOnline) StatusOnline else StatusOffline,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isEffectivelyOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEffectivelyOnline) "État : EN LIGNE (Synchronisé)" else "État : HORS LIGNE (Local)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEffectivelyOnline) StatusOnline else StatusOffline
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isEffectivelyOnline)
                            "Connexion BIZSTOCK active • Synchronisation automatique continue"
                        else
                            "Mode hors ligne • Consultation locale autorisée • Modifications verrouillées",
                        fontSize = 12.sp,
                        color = if (isEffectivelyOnline) Color(0xFF065F46) else Color(0xFF92400E)
                    )
                }
            }
        }

        // Quick Manual Sync Action Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Synchronisation Manuelle",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dernière synchro réussie : $lastSyncFormatted",
                    fontSize = 12.sp,
                    color = BrandSlateLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onSyncClick,
                    enabled = isEffectivelyOnline && !syncInfo.isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("screen_trigger_sync_btn")
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (syncInfo.isSyncing) "Synchronisation en cours..." else "Forcer la synchronisation Cloud",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Offline Simulation Switch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simuler le mode Hors ligne",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Permet de tester immédiatement le verrouillage des ajouts, modifications et suppressions de produits ainsi que la réactivation automatique dès le retour de connexion.",
                            fontSize = 12.sp,
                            color = BrandSlateLight
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Switch(
                        checked = syncInfo.isManualOfflineMode,
                        onCheckedChange = { onToggleOfflineMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StatusOffline,
                            checkedTrackColor = StatusOffline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("settings_offline_switch")
                    )
                }
            }
        }

        // Architecture Specifications Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Architecture BIZSTOCK & Règles Métier",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                SpecRow(
                    icon = Icons.Default.Check,
                    title = "Consultation Locale Haute Performance",
                    description = "Tous les produits, stocks, ventes et clients déjà synchronisés restent consultables instantanément grâce au cache local sécurisé Room."
                )

                Spacer(modifier = Modifier.height(10.dp))

                SpecRow(
                    icon = Icons.Default.Lock,
                    title = "Protection d'Intégrité Cloud en Hors Ligne",
                    description = "Conformément aux spécifications BIZSTOCK, l'ajout, la modification et la suppression de produits sont formellement désactivés hors ligne pour éviter les conflits d'inventaire."
                )

                Spacer(modifier = Modifier.height(10.dp))

                SpecRow(
                    icon = Icons.Default.Sync,
                    title = "Synchronisation Automatique au Rétablissement",
                    description = "Dès que le réseau internet est détecté ou que le mode hors ligne est désactivé, BIZSTOCK déclenche immédiatement une synchronisation avec les serveurs."
                )

                Spacer(modifier = Modifier.height(10.dp))

                SpecRow(
                    icon = Icons.Default.Computer,
                    title = "Support Multi-Écrans (PC & Android)",
                    description = "Interface responsive adaptative : barre latérale NavigationRail sur grand écran PC / bureau et barre basse ergonomique sur smartphones Android."
                )
            }
        }
    }
}

@Composable
fun SpecRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = CircleShape,
            color = BrandBlue.copy(alpha = 0.12f),
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.5.sp,
                color = BrandSlateLight,
                lineHeight = 15.sp
            )
        }
    }
}
