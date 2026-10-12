package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SyncInfo
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOfflineBg
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.StatusOnlineBg

import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.SurfaceBorderLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BizStockTopBar(
    syncInfo: SyncInfo,
    onSyncClick: () -> Unit,
    onOpenSyncDetails: () -> Unit,
    onToggleOfflineMode: () -> Unit,
    onOpenMobileMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isEffectivelyOnline = syncInfo.isEffectivelyOnline

    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    TopAppBar(
        modifier = modifier
            .fillMaxWidth()
            .border(0.dp, Color.Transparent)
            .testTag("bizstock_top_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = TextPrimaryLight
        ),
        navigationIcon = {
            if (onOpenMobileMenu != null) {
                IconButton(
                    onClick = onOpenMobileMenu,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mobile_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Ouvrir le menu de navigation BIZSTOCK",
                        tint = TextPrimaryLight
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Official BIZSTOCK Logo
                Image(
                    painter = painterResource(id = R.drawable.bizstock_app_icon_1791310032354),
                    contentDescription = "Logo BIZSTOCK",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "BIZSTOCK",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "SIMPLE • EFFICACE • DURABLE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        },
        actions = {
            // Status Chip (Online / Offline)
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenSyncDetails() }
                    .border(
                        1.dp,
                        if (isEffectivelyOnline) StatusOnline.copy(alpha = 0.4f) else StatusOffline.copy(alpha = 0.4f),
                        RoundedCornerShape(20.dp)
                    )
                    .testTag("status_indicator_chip"),
                color = if (isEffectivelyOnline) StatusOnlineBg else StatusOfflineBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isEffectivelyOnline) StatusOnline else StatusOffline)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEffectivelyOnline) "En ligne" else "Hors ligne",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isEffectivelyOnline) StatusOnline else StatusOffline
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Sync Button (min 48dp touch target)
            IconButton(
                onClick = onSyncClick,
                enabled = isEffectivelyOnline && !syncInfo.isSyncing,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("sync_action_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Synchroniser avec le Cloud BIZSTOCK",
                    tint = if (isEffectivelyOnline) BrandBlue else TextPrimaryLight.copy(alpha = 0.4f),
                    modifier = if (syncInfo.isSyncing) Modifier.rotate(rotation) else Modifier
                )
            }

            // Info Button
            IconButton(
                onClick = onOpenSyncDetails,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("sync_details_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Détails de la synchronisation",
                    tint = TextPrimaryLight.copy(alpha = 0.7f)
                )
            }
        }
    )
}

@Composable
fun OfflineBanner(
    isOffline: Boolean,
    onSimulateOnlineClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOffline) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("offline_warning_banner"),
        color = StatusOfflineBg,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Mode Hors ligne",
                tint = StatusOffline,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mode Hors ligne actif",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
                Text(
                    text = "Consultation locale autorisée • Ajouts et modifications verrouillés • Synchronisation automatique dès le retour de connexion.",
                    fontSize = 11.5.sp,
                    color = Color(0xFFB45309),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onSimulateOnlineClick() }
                    .border(1.dp, StatusOffline.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                color = Color.White
            ) {
                Text(
                    text = "Détails",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusOffline,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
