package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Sale
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOfflineBg
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.StatusOnlineBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SaleInvoiceDialog(
    sale: Sale,
    onDismiss: () -> Unit,
    onPrint: () -> Unit,
    onShare: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", Locale.FRANCE) }
    val formattedDate = remember(sale.date) { dateFormat.format(Date(sale.date)) }
    val isPaid = sale.paymentStatus == "PAYÉE"
    val items = remember(sale) { sale.getParsedItems() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "FACTURE OFFICIELLE",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = sale.invoiceNumber,
                            fontSize = 12.sp,
                            color = BrandBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Enterprise Header Card
                Surface(
                    color = BrandNavyDark,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "BIZSTOCK Entreprise",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "SIMPLE • EFFICACE • DURABLE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00C49F),
                                    letterSpacing = 0.8.sp
                                )
                            }

                            // Payment Status Chip
                            Surface(
                                color = if (isPaid) StatusOnlineBg else StatusOfflineBg,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.Pending,
                                        contentDescription = null,
                                        tint = if (isPaid) StatusOnline else StatusOffline,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPaid) "PAYÉE" else "EN ATTENTE",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = if (isPaid) StatusOnline else StatusOffline
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Émise le :", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.65f))
                                Text(formattedDate, fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Mode de règlement :", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.65f))
                                Text(sale.paymentMethod, fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Billed To (Client Details)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "FACTURÉ À :",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandSlateLight,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sale.customerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (sale.customerPhone.isNotBlank()) {
                            Text(
                                text = "Tél : ${sale.customerPhone}",
                                fontSize = 12.sp,
                                color = BrandSlateLight
                            )
                        }
                        if (sale.customerEmail.isNotBlank()) {
                            Text(
                                text = "Email : ${sale.customerEmail}",
                                fontSize = 12.sp,
                                color = BrandSlateLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Items Details Table
                Text(
                    text = "Détail des Articles Vendus",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Table Header
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Désignation & Réf", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.8f))
                        Text("Qté", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                        Text("Indice U.", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                        Text("Total", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                    }
                }

                // Table Rows
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (index % 2 == 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.8f)) {
                                Text(item.productName, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
                                Text(item.productSku, fontSize = 10.sp, color = BrandSlateLight)
                            }
                            Text("${item.quantity}", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                            Text("${item.unitPriceIndex} pts", fontSize = 11.5.sp, color = BrandSlateLight, modifier = Modifier.weight(0.9f))
                            Text(
                                text = "${item.totalLineIndex} pts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlue,
                                modifier = Modifier.weight(0.9f)
                            )
                        }
                        if (index < items.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        }
                    }
                }

                // Total Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INDICE GLOBAL FACTURE",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                        )
                        Text(
                            text = "${sale.totalIndex} pts",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = BrandBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Inventory & Traceability Badge
                Surface(
                    color = StatusOnlineBg,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = StatusOnline,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ Décrémentation automatique du stock effectuée • Enregistré dans l'historique des sorties.",
                            fontSize = 11.sp,
                            color = StatusOnline,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (sale.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Notes & Références :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSlateLight)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(sale.notes, fontSize = 11.5.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer Legal / Watermark
                Text(
                    text = "Facture commerciale émise via BIZSTOCK. Conforme aux règles d'inventaire et de suivi des indices d'entreprise.",
                    fontSize = 10.sp,
                    color = BrandSlateLight,
                    lineHeight = 13.sp
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onPrint,
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("print_invoice_btn")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Imprimer")
                }

                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("share_invoice_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Partager / PDF")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(44.dp)
            ) {
                Text("Fermer")
            }
        }
    )
}
