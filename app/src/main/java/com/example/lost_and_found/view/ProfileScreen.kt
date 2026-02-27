package com.example.lost_and_found.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.AlertDialog
import coil.compose.AsyncImage
import com.example.lost_and_found.R
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.components.ClaimCard
import com.example.lost_and_found.view.components.ConfirmDialog
import com.example.lost_and_found.view.components.ItemCard

// Dummy claim data class
data class DummyClaim(
    val itemName: String,
    val status: String,
    val date: String
)

@Composable
fun ProfileScreen() {

    // Dummy user data — will be replaced with Firebase later
    val userName = "John Doe"
    val userEmail = "john@example.com"
    val userRole = "student"
    val userPhotoUrl = ""

    var selectedTab by remember { mutableStateOf("reports") }
    var selectedReportFilter by remember { mutableStateOf("lost") }

    // Dialog states
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    // Change password states
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Dummy data
    val myReports = listOf(
        DummyItem("Headphone", "Block A", "2025-01-01", "lost", ""),
        DummyItem("Wallet", "Library", "2025-01-03", "lost", ""),
        DummyItem("Water Bottle", "Cafeteria", "2025-01-05", "found", ""),
    )

    val myClaims = listOf(
        DummyClaim("Laptop Bag", "pending", "2025-01-07"),
        DummyClaim("ID Card", "approved", "2025-01-08"),
        DummyClaim("Keys", "rejected", "2025-01-09"),
    )

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF374151),
        unfocusedContainerColor = Color(0xFF374151),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedIndicatorColor = colorResource(R.color.greenshade),
        unfocusedIndicatorColor = Color(0xFF4B5563),
        cursorColor = colorResource(R.color.greenshade),
        focusedLabelColor = colorResource(R.color.greenshade),
        unfocusedLabelColor = Color(0xFF9CA3AF),
    )

    // Logout Dialog
    if (showLogoutDialog) {
        ConfirmDialog(
            title = "Logout",
            message = "Are you sure you want to logout?",
            confirmText = "Logout",
            onConfirm = { showLogoutDialog = false },
            onDismiss = { showLogoutDialog = false }
        )
    }

    // Delete Account Dialog
    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Delete Account",
            message = "Are you sure you want to delete your account? This action cannot be undone.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = { showDeleteDialog = false },
            onDismiss = { showDeleteDialog = false }
        )
    }

    // Change Password Dialog — kept as AlertDialog since it has custom fields
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            containerColor = Color(0xFF1F2937),
            title = {
                Text("Change Password", color = Color.White, fontFamily = Ruluko)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        colors = textFieldColors,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        colors = textFieldColors,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        colors = textFieldColors,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showChangePasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.greenshade),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Update", fontFamily = Ruluko)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF), fontFamily = Ruluko)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blackshade)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        // Profile Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F2937))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Avatar
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (userPhotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .border(2.dp, colorResource(R.color.greenshade), CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF374151))
                                .border(2.dp, colorResource(R.color.greenshade), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.account),
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Edit icon on avatar
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.greenshade))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_edit_24),
                            contentDescription = "Edit",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Name
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Ruluko
                )

                // Email
                Text(
                    text = userEmail,
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp,
                    fontFamily = Ruluko
                )

                // Role badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            if (userRole == "admin") Color(0xFF7C3AED)
                            else colorResource(R.color.greenshade).copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = userRole.replaceFirstChar { it.uppercase() },
                        color = if (userRole == "admin") Color.White
                        else colorResource(R.color.greenshade),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Ruluko
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Action buttons row
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    // Change Password
                    OutlinedButton(
                        onClick = { showChangePasswordDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_lock_24),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Password", fontSize = 12.sp, fontFamily = Ruluko)
                    }

                    // Delete Account
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Delete", fontSize = 12.sp, fontFamily = Ruluko)
                    }

                    // Logout
                    Button(
                        onClick = { showLogoutDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.greenshade),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_logout_24),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Logout", fontSize = 12.sp, fontFamily = Ruluko)
                    }
                }
            }
        }

        // Tabs — My Reports / My Claims
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("reports" to "My Reports", "claims" to "My Claims").forEach { (key, label) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedTab == key) colorResource(R.color.greenshade)
                                else Color(0xFF1F2937)
                            )
                            .clickable { selectedTab = key }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (selectedTab == key) Color.Black else Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = Ruluko
                        )
                    }
                }
            }
        }

        // My Reports Tab
        if (selectedTab == "reports") {

            // Lost / Found filter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1F2937))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("lost", "found").forEach { filter ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedReportFilter == filter)
                                        colorResource(R.color.greenshade)
                                    else Color.Transparent
                                )
                                .clickable { selectedReportFilter = filter }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter.replaceFirstChar { it.uppercase() },
                                color = if (selectedReportFilter == filter) Color.Black else Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                fontFamily = Ruluko
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }


            // Reports grid
            val filteredReports = myReports.filter { it.status == selectedReportFilter }

            if (filteredReports.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No $selectedReportFilter items reported",
                            color = Color(0xFF9CA3AF),
                            fontFamily = Ruluko,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(filteredReports.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            ItemCard(
                                itemName = item.itemName,
                                location = item.location,
                                date = item.date,
                                status = item.status,
                                imageUrl = item.imageUrl,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // My Claims Tab
        if (selectedTab == "claims") {
            if (myClaims.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No claims submitted yet",
                            color = Color(0xFF9CA3AF),
                            fontFamily = Ruluko,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(myClaims) { claim ->
                    ClaimCard(
                        itemName = claim.itemName,
                        status = claim.status,
                        date = claim.date,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}