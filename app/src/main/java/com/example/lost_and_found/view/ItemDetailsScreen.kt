package com.example.lost_and_found.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lost_and_found.R
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.components.ConfirmDialog

@Composable
fun ItemDetailScreen(
    itemName: String = "Headphone",
    description: String = "Black Sony headphone found near the library entrance. Has a sticker on the left ear cup.",
    category: String = "Electronics",
    location: String = "Block A",
    date: String = "2025-01-01",
    status: String = "lost",
    imageUrl: String = "",
    reporterName: String = "Jane Doe",
    reporterPhotoUrl: String = "",
    isOwner: Boolean = false,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClaim: () -> Unit = {}
) {
    val statusColor = if (status.lowercase() == "lost") Color(0xFFEF4444)
    else colorResource(R.color.greenshade)

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Delete Item",
            message = "Are you sure you want to delete this item? This action cannot be undone.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blackshade))
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        // Image + Back button + Status badge
        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)

            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = itemName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1F2937)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_photo_library_24),
                            contentDescription = null,
                            tint = Color(0xFF374151),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    colorResource(R.color.blackshade)
                                )
                            )
                        )
                )

                // Back button
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { onBack() }
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(50.dp))
                        .background(statusColor)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = status.replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Ruluko
                    )
                }
            }
        }

        // Item name + category badge
        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = itemName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Ruluko,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF1F2937))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = category,
                        color = colorResource(R.color.greenshade),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Ruluko
                    )
                }
            }
        }

        // Location + Date info rows
        item {
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1F2937))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DetailRow(
                    icon = R.drawable.outline_location_on_24,
                    label = "Location",
                    value = location
                )
                DetailRow(
                    icon = R.drawable.twotone_calendar_today_24,
                    label = "Date",
                    value = date
                )
            }
        }

        // Description
        item {
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1F2937))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Description",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    fontFamily = Ruluko
                )
                Text(
                    text = description,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = Ruluko,
                    lineHeight = 22.sp
                )
            }
        }

        // Reporter info
        item {

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1F2937))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (reporterPhotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = reporterPhotoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.dp, colorResource(R.color.greenshade), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF374151))
                            .border(1.dp, colorResource(R.color.greenshade), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.account),
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Reported by",
                        color = Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontFamily = Ruluko
                    )
                    Text(
                        text = reporterName,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Ruluko
                    )
                }
            }
        }

        // Action buttons
        item {
            Spacer(Modifier.height(16.dp))

            if (isOwner) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorResource(R.color.greenshade)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, colorResource(R.color.greenshade)
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_edit_24),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Edit Item",
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Ruluko
                        )
                    }

                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Delete",
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Ruluko
                        )
                    }
                }
            } else {
                Button(
                    onClick = onClaim,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.greenshade),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_content_paste_24),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Submit Claim",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = Ruluko
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: Int,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF374151)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = colorResource(R.color.greenshade),
                modifier = Modifier.size(18.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                color = Color(0xFF9CA3AF),
                fontSize = 11.sp,
                fontFamily = Ruluko
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Ruluko
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailScreenPreview() {
    ItemDetailScreen()
}

@Preview(showBackground = true)
@Composable
fun ItemDetailScreenOwnerPreview() {
    ItemDetailScreen(isOwner = true)
}