package com.example.lost_and_found.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lost_and_found.R
import com.example.lost_and_found.repository.ItemRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.components.ActiveFilterTag
import com.example.lost_and_found.view.components.FilterChip
import com.example.lost_and_found.view.components.ItemCard
import com.example.lost_and_found.viewmodel.ItemViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen() {

    val context = LocalContext.current
    val itemViewModel = remember { ItemViewModel(ItemRepoImpl()) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Observe LiveData
    val allItems by itemViewModel.allItems.observeAsState(emptyList())
    val isLoading by itemViewModel.isLoading.observeAsState(false)

    // Search & filter states
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("all") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }

    // Temp states for bottom sheet
    var tempCategory by remember { mutableStateOf("All") }
    var tempLocation by remember { mutableStateOf("All") }
    var showFilterSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    val categories = listOf(
        "All", "Electronics", "Clothing",
        "Accessories", "Documents", "Keys", "Bags", "Others"
    )

    // Dynamic locations from fetched items
    val locations = listOf("All") + (allItems
        ?.map { it.location }
        ?.distinct()
        ?.filter { it.isNotEmpty() }
        ?: emptyList())

    val isFilterActive = selectedCategory != "All" || selectedLocation != "All"

    // Fetch all items when screen loads
    LaunchedEffect(Unit) {
        itemViewModel.getAllItems()
    }

    // Filter logic
    val filteredItems = allItems?.filter { item ->
        val matchesSearch = item.itemName.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        val matchesStatus = selectedStatus == "all" || item.status == selectedStatus
        val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
        val matchesLocation = selectedLocation == "All" || item.location == selectedLocation
        matchesSearch && matchesStatus && matchesCategory && matchesLocation
    } ?: emptyList()

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = bottomSheetState,
            containerColor = Color(0xFF1F2937)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Filter Items",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Ruluko
                    )
                    Text(
                        "Reset",
                        color = colorResource(R.color.greenshade),
                        fontSize = 13.sp,
                        fontFamily = Ruluko,
                        modifier = Modifier.clickable {
                            tempCategory = "All"
                            tempLocation = "All"
                        }
                    )
                }

                // Category chips
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Category",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Ruluko
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                label = category,
                                isSelected = tempCategory == category,
                                onClick = { tempCategory = category }
                            )
                        }
                    }
                }

                // Location chips
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Location",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Ruluko
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        locations.forEach { location ->
                            FilterChip(
                                label = location,
                                isSelected = tempLocation == location,
                                onClick = { tempLocation = location }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Apply button
                Button(
                    onClick = {
                        selectedCategory = tempCategory
                        selectedLocation = tempLocation
                        showFilterSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.greenshade),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        "Apply Filters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = Ruluko
                    )
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blackshade)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Title
        item {
            Text(
                "Search Items",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Ruluko
            )
            Text(
                "Find lost or found items",
                color = Color(0xFF9CA3AF),
                fontSize = 12.sp,
                fontFamily = Ruluko
            )
        }

        // Search bar + Filter button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search items...",
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_close_24),
                                contentDescription = "Clear",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { searchQuery = "" }
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1F2937),
                        unfocusedContainerColor = Color(0xFF1F2937),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = colorResource(R.color.greenshade),
                        unfocusedIndicatorColor = Color(0xFF374151),
                        cursorColor = colorResource(R.color.greenshade),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                // Filter button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isFilterActive) colorResource(R.color.greenshade)
                            else Color(0xFF1F2937)
                        )
                        .clickable {
                            tempCategory = selectedCategory
                            tempLocation = selectedLocation
                            showFilterSheet = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_filter_list_24),
                        contentDescription = "Filter",
                        tint = if (isFilterActive) Color.Black else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Active filter tags
        if (isFilterActive) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedCategory != "All") {
                        ActiveFilterTag(
                            label = selectedCategory,
                            onRemove = { selectedCategory = "All" }
                        )
                    }
                    if (selectedLocation != "All") {
                        ActiveFilterTag(
                            label = selectedLocation,
                            onRemove = { selectedLocation = "All" }
                        )
                    }
                }
            }
        }

        // Lost / Found / All toggle
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1F2937))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("all", "lost", "found").forEach { status ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selectedStatus == status)
                                    colorResource(R.color.greenshade)
                                else Color.Transparent
                            )
                            .clickable { selectedStatus = status }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = status.replaceFirstChar { it.uppercase() },
                            color = if (selectedStatus == status) Color.Black else Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            fontFamily = Ruluko
                        )
                    }
                }
            }
        }

        // Loading indicator
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.greenshade)
                    )
                }
            }
        } else {

            // Results count
            item {
                Text(
                    "${filteredItems.size} item${if (filteredItems.size != 1) "s" else ""} found",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    fontFamily = Ruluko
                )
            }

            // Results grid
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = null,
                                tint = Color(0xFF374151),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "No items found",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp,
                                fontFamily = Ruluko
                            )
                        }
                    }
                }
            } else {
                items(filteredItems.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            ItemCard(
                                itemName = item.itemName,
                                location = item.location,
                                date = item.date,
                                status = item.status,
                                imageUrl = item.imageUrl,
                                modifier = Modifier.weight(1f),
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
                        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}