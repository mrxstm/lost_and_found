package com.example.lost_and_found.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lost_and_found.R
import com.example.lost_and_found.ui.theme.Ruluko

@Composable
fun ClaimCard(
    itemName: String,
    status: String,
    date: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val statusColor = when (status.lowercase()) {
        "approved" -> Color(0xFF22C55E)
        "rejected" -> Color(0xFFEF4444)
        else ->       Color(0xFFEAB308)
    }

    val statusBg = when (status.lowercase()) {
        "approved" -> Color(0xFF22C55E).copy(alpha = 0.1f)
        "rejected" -> Color(0xFFEF4444).copy(alpha = 0.1f)
        else ->       Color(0xFFEAB308).copy(alpha = 0.1f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1F2937))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF374151)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_content_paste_24),
                    contentDescription = null,
                    tint = colorResource(R.color.greenshade),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Item name + date
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = itemName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Ruluko
                )
                Text(
                    text = date,
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    fontFamily = Ruluko
                )
            }
        }

        // Status badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(statusBg)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = status.replaceFirstChar { it.uppercase() },
                color = statusColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Ruluko
            )
        }
    }
}

@Preview
@Composable
fun ClaimCardPreview() {
    Column {
        ClaimCard(itemName = "Laptop Bag", status = "pending", date = "2025-01-07")
        ClaimCard(itemName = "ID Card", status = "approved", date = "2025-01-08")
        ClaimCard(itemName = "Keys", status = "rejected", date = "2025-01-09")
    }
}