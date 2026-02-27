package com.example.lost_and_found.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lost_and_found.R
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.components.ItemCard
import com.example.lost_and_found.view.components.SectionHeader
import com.example.lost_and_found.view.components.StatCard

// Dummy data class for preview
data class DummyItem(
    val itemName: String,
    val location: String,
    val date: String,
    val status: String,
    val imageUrl: String
)

val dummyItems = listOf(
    DummyItem("Headphone", "Block A", "2025-01-01", "lost", ""),
    DummyItem("Wallet", "Library", "2025-01-03", "lost", ""),
    DummyItem("Water Bottle", "Cafeteria", "2025-01-05", "found", ""),
    DummyItem("Keys", "Parking Lot", "2025-01-06", "found", ""),
)

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blackshade))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {

        // Report buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Report lost item",
                        fontSize = 12.sp,
                        fontFamily = Ruluko
                    )
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.greenshade),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_check_circle_24),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Report found item",
                        fontSize = 12.sp,
                        fontFamily = Ruluko
                    )
                }
            }
        }

        // Info section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Info",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Ruluko
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        value = "2,000",
                        label = "Total report",
                        icon = R.drawable.outline_content_paste_24,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "1,500",
                        label = "Item returned",
                        icon = R.drawable.outline_assignment_return_24,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "150",
                        label = "Active Users",
                        icon = R.drawable.account,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "24h",
                        label = "Avg Response",
                        icon = R.drawable.baseline_access_time_24,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recently Lost
        item {
            SectionHeader(title = "Recently Lost", onSeeAll = {})
            Spacer(Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dummyItems.filter { it.status == "lost" }) { item ->
                    ItemCard(
                        itemName = item.itemName,
                        location = item.location,
                        date = item.date,
                        status = item.status,
                        imageUrl = item.imageUrl
                    )
                }
            }
        }

        // Recently Found
        item {
            SectionHeader(title = "Recently Found", onSeeAll = {})
            Spacer(Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dummyItems.filter { it.status == "found" }) { item ->
                    ItemCard(
                        itemName = item.itemName,
                        location = item.location,
                        date = item.date,
                        status = item.status,
                        imageUrl = item.imageUrl
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}