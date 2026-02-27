package com.example.lost_and_found.view.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun ItemCard(
    itemName: String,
    location: String,
    date: String,
    status: String,        // "lost" or "found"
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val statusColor = if (status.lowercase() == "lost")
        Color(0xFFEF4444)   // red
    else
        Color(0xFF5DCEA6)   // green

    val statusLabel = if (status.lowercase() == "lost") "Lost" else "Found"

    Box(
        modifier = modifier
            .width(175.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1F2937))
            .clickable { onClick() }   // 👈 add this

    ) {
        Column {

            // Image + Status Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = itemName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )

                // Status badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(statusColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Item info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = itemName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Ruluko
                )

                // Location row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.outline_location_on_24),
                        contentDescription = null,
                        tint = colorResource(R.color.greenshade),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = location,
                        color = Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontFamily = Ruluko
                    )
                }

                // Date row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.twotone_calendar_today_24),
                        contentDescription = null,
                        tint = colorResource(R.color.greenshade),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = date,
                        color = Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontFamily = Ruluko
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ItemCardPreview() {
    ItemCard(
        itemName = "Headphone",
        location = "Block A",
        date = "2025-01-01",
        status = "lost",
        imageUrl = ""
    )
}