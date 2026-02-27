package com.example.lost_and_found.view

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lost_and_found.R
import com.example.lost_and_found.repository.ItemRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.components.ItemCard
import com.example.lost_and_found.view.components.SectionHeader
import com.example.lost_and_found.view.components.StatCard
import com.example.lost_and_found.viewmodel.ItemViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen() {

    val context = LocalContext.current
    val itemViewModel = remember { ItemViewModel(ItemRepoImpl()) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Observe LiveData
    val allItems by itemViewModel.allItems.observeAsState(emptyList())
    val isLoading by itemViewModel.isLoading.observeAsState(false)

    // Fetch all items when screen loads
    LaunchedEffect(Unit) {
        itemViewModel.getAllItems()
    }

    // Filter lost and found items
    val lostItems = allItems?.filter { it.status == "lost" } ?: emptyList()
    val foundItems = allItems?.filter { it.status == "found" } ?: emptyList()

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
                    onClick = {
                        val intent = Intent(context, ReportActivity::class.java).apply {
                            putExtra("status", "lost")
                        }
                        context.startActivity(intent)
                    },
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
                    onClick = {
                        val intent = Intent(context, ReportActivity::class.java).apply {
                            putExtra("status", "found")
                        }
                        context.startActivity(intent)
                    },
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

        // Info / Stats section
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
                        value = "${allItems?.size ?: 0}",
                        label = "Total Items",
                        icon = R.drawable.outline_content_paste_24,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "${foundItems.size}",
                        label = "Item returned",
                        icon = R.drawable.outline_assignment_return_24,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "${lostItems.size}",
                        label = "Lost Items",
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

        // Loading indicator
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.greenshade)
                    )
                }
            }
        } else {

            // Recently Lost
            item {
                SectionHeader(title = "Recently Lost", onSeeAll = {})
                Spacer(Modifier.height(10.dp))
                if (lostItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No lost items reported yet",
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp,
                            fontFamily = Ruluko
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lostItems) { item ->
                            ItemCard(
                                itemName = item.itemName,
                                location = item.location,
                                date = item.date,
                                status = item.status,
                                imageUrl = item.imageUrl,
                                onClick = {
                                    val intent = Intent(
                                        context,
                                        ItemDetailActivity::class.java
                                    ).apply {
                                        putExtra("itemId", item.id)
                                        putExtra("itemName", item.itemName)
                                        putExtra("description", item.description)
                                        putExtra("category", item.category)
                                        putExtra("location", item.location)
                                        putExtra("date", item.date)
                                        putExtra("status", item.status)
                                        putExtra("imageUrl", item.imageUrl)
                                        putExtra("reporterName", item.reporterName)
                                        putExtra("reporterPhotoUrl", item.reporterPhotoUrl)
                                        putExtra("isOwner", item.reportedBy == currentUserId)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }

            // Recently Found
            item {
                SectionHeader(title = "Recently Found", onSeeAll = {})
                Spacer(Modifier.height(10.dp))
                if (foundItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No found items reported yet",
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp,
                            fontFamily = Ruluko
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(foundItems) { item ->
                            ItemCard(
                                itemName = item.itemName,
                                location = item.location,
                                date = item.date,
                                status = item.status,
                                imageUrl = item.imageUrl,
                                onClick = {
                                    val intent = Intent(
                                        context,
                                        ItemDetailActivity::class.java
                                    ).apply {
                                        putExtra("itemId", item.id)
                                        putExtra("itemName", item.itemName)
                                        putExtra("description", item.description)
                                        putExtra("category", item.category)
                                        putExtra("location", item.location)
                                        putExtra("date", item.date)
                                        putExtra("status", item.status)
                                        putExtra("imageUrl", item.imageUrl)
                                        putExtra("reporterName", item.reporterName)
                                        putExtra("reporterPhotoUrl", item.reporterPhotoUrl)
                                        putExtra("isOwner", item.reportedBy == currentUserId)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}