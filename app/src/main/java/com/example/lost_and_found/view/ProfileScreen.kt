package com.example.lost_and_found.view

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lost_and_found.R
import com.example.lost_and_found.model.UserModel
import com.example.lost_and_found.repository.ItemRepoImpl
import com.example.lost_and_found.repository.UserRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.components.ClaimCard
import com.example.lost_and_found.view.components.ConfirmDialog
import com.example.lost_and_found.view.components.ItemCard
import com.example.lost_and_found.viewmodel.ItemViewModel
import com.example.lost_and_found.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen() {

    val context = LocalContext.current
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val itemViewModel = remember { ItemViewModel(ItemRepoImpl()) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }

    // Observe LiveData
    val userData by userViewModel.users.observeAsState()
    val userItems by itemViewModel.userItems.observeAsState(emptyList())
    val isLoading by itemViewModel.isLoading.observeAsState(false)

    // Fetch data when screen loads
    LaunchedEffect(Unit) {
        currentUser?.uid?.let {
            userViewModel.getUserByID(it)
            itemViewModel.getItemsByUser(it)
        }
    }

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
            onConfirm = {
                FirebaseAuth.getInstance().signOut()
                showLogoutDialog = false
                val intent = Intent(context, LoginScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
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
            onConfirm = {
                currentUser?.uid?.let { userId ->
                    userViewModel.deleteAccount(userId) { success, msg ->
                        if (success) {
                            FirebaseAuth.getInstance().currentUser?.delete()
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, LoginScreen::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    // Change Password Dialog
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
                    onClick = {
                        when {
                            currentPassword.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    "Please enter current password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            newPassword.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    "Please enter new password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            newPassword != confirmPassword -> {
                                Toast.makeText(
                                    context,
                                    "Passwords do not match",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                val credential =
                                    com.google.firebase.auth.EmailAuthProvider.getCredential(
                                        currentUser?.email ?: "", currentPassword
                                    )
                                currentUser?.reauthenticate(credential)
                                    ?.addOnCompleteListener { reauth ->
                                        if (reauth.isSuccessful) {
                                            currentUser.updatePassword(newPassword)
                                                .addOnCompleteListener { update ->
                                                    if (update.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Password updated successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        showChangePasswordDialog = false
                                                        currentPassword = ""
                                                        newPassword = ""
                                                        confirmPassword = ""
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed: ${update.exception?.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Current password is incorrect",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }
                    },
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

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = Color(0xFF1F2937),
            title = {
                Text("Edit Profile", color = Color.White, fontFamily = Ruluko)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            editName.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    "Please enter your name",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                val updatedModel = UserModel(
                                    id = currentUser?.uid ?: "",
                                    full_name = editName,
                                    email = userData?.email ?: "",
                                    phone = editPhone,
                                    profilePhotoURL = userData?.profilePhotoURL ?: "",
                                    role = userData?.role ?: "user"
                                )
                                currentUser?.uid?.let { uid ->
                                    userViewModel.editProfile(uid, updatedModel) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        if (success) showEditProfileDialog = false
                                    }
                                }
                            }
                        }
                    },
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
                TextButton(onClick = { showEditProfileDialog = false }) {
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
                    if (userData?.profilePhotoURL?.isNotEmpty() == true) {
                        AsyncImage(
                            model = userData?.profilePhotoURL,
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
                    text = userData?.full_name ?: "Loading...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Ruluko
                )

                // Email
                Text(
                    text = userData?.email ?: "",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp,
                    fontFamily = Ruluko
                )

                // Role badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            if (userData?.role == "admin") Color(0xFF7C3AED)
                            else colorResource(R.color.greenshade).copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = userData?.role?.replaceFirstChar { it.uppercase() } ?: "User",
                        color = if (userData?.role == "admin") Color.White
                        else colorResource(R.color.greenshade),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Ruluko
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Action buttons row

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Edit Profile 👈 add first
                    OutlinedButton(
                        onClick = {
                            editName = userData?.full_name ?: ""
                            editPhone = userData?.phone ?: ""
                            showEditProfileDialog = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_edit_24),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Edit", fontSize = 12.sp, fontFamily = Ruluko)
                    }

                }
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

        // Tabs
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
                                color = if (selectedReportFilter == filter) Color.Black
                                else Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                fontFamily = Ruluko
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
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
                val filteredReports = userItems
                    ?.filter { it.status == selectedReportFilter }
                    ?: emptyList()

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
                                            putExtra("isOwner", true) // always owner in my reports
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

        // My Claims Tab — will connect when Claims CRUD is done
        if (selectedTab == "claims") {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Claims coming soon...",
                        color = Color(0xFF9CA3AF),
                        fontFamily = Ruluko,
                        fontSize = 13.sp
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