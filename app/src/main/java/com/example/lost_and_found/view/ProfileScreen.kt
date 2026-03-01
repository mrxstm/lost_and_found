package com.example.lost_and_found.view

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.lost_and_found.repository.ClaimRepoImpl
import com.example.lost_and_found.repository.CloudinaryRepoImpl
import com.example.lost_and_found.repository.ItemRepoImpl
import com.example.lost_and_found.repository.UserRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.components.ClaimCard
import com.example.lost_and_found.view.components.ConfirmDialog
import com.example.lost_and_found.view.components.ItemCard
import com.example.lost_and_found.viewmodel.ClaimViewModel
import com.example.lost_and_found.viewmodel.CloudinaryViewModel
import com.example.lost_and_found.viewmodel.ItemViewModel
import com.example.lost_and_found.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onPickImage: (callback: (Uri?) -> Unit) -> Unit = {},
    onTakePhoto: (callback: (Uri?) -> Unit) -> Unit = {},
) {
    val context = LocalContext.current
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val itemViewModel = remember { ItemViewModel(ItemRepoImpl()) }
    val claimViewModel = remember { ClaimViewModel(ClaimRepoImpl()) }
    val cloudinaryViewModel = remember { CloudinaryViewModel(CloudinaryRepoImpl()) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    //  Observe LiveData
    val userData by userViewModel.users.observeAsState()
    val userItems by itemViewModel.userItems.observeAsState(emptyList())
    val isLoading by itemViewModel.isLoading.observeAsState(false)
    val userClaims by claimViewModel.userClaims.observeAsState(emptyList())
    val founderClaims by claimViewModel.founderClaims.observeAsState(emptyList())
    val isClaimsLoading by claimViewModel.isLoading.observeAsState(false)

    //  Fetch data
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            userViewModel.getUserByID(uid)
            itemViewModel.getItemsByUser(uid)
            claimViewModel.getClaimsByUser(uid)
            claimViewModel.getClaimsByFounder(uid)
        }
    }

    //UI states
    var selectedTab by remember { mutableStateOf("reports") }
    var selectedReportFilter by remember { mutableStateOf("lost") }
    var selectedClaimFilter by remember { mutableStateOf("received") }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showImageSheet by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val imageSheetState = rememberModalBottomSheetState()

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

    //Date formatter
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    //Profile picture bottom sheet
    if (showImageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showImageSheet = false },
            sheetState = imageSheetState,
            containerColor = Color(0xFF1F2937)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Update Profile Photo",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Ruluko
                )

                // Gallery option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF374151))
                        .clickable {
                            showImageSheet = false
                            onPickImage { uri ->
                                if (uri != null) {
                                    isUploadingPhoto = true
                                    cloudinaryViewModel.uploadImage(context, uri) { uploadedUrl ->
                                        if (uploadedUrl != null) {
                                            val updatedModel = UserModel(
                                                id = currentUser?.uid ?: "",
                                                full_name = userData?.full_name ?: "",
                                                email = userData?.email ?: "",
                                                phone = userData?.phone ?: "",
                                                profilePhotoURL = uploadedUrl,
                                                role = userData?.role ?: "user"
                                            )
                                            currentUser?.uid?.let { uid ->
                                                userViewModel.editProfile(
                                                    uid,
                                                    updatedModel
                                                ) { success, msg ->
                                                    isUploadingPhoto = false
                                                    Toast.makeText(
                                                        context,
                                                        msg,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    if (success) userViewModel.getUserByID(uid)
                                                }
                                            }
                                        } else {
                                            isUploadingPhoto = false
                                            Toast.makeText(
                                                context,
                                                "Upload failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_photo_library_24),
                        contentDescription = null,
                        tint = colorResource(R.color.greenshade),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Choose from Gallery", color = Color.White, fontFamily = Ruluko)
                }

                // Camera option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF374151))
                        .clickable {
                            showImageSheet = false
                            onTakePhoto { uri ->
                                if (uri != null) {
                                    isUploadingPhoto = true
                                    cloudinaryViewModel.uploadImage(context, uri) { uploadedUrl ->
                                        if (uploadedUrl != null) {
                                            val updatedModel = UserModel(
                                                id = currentUser?.uid ?: "",
                                                full_name = userData?.full_name ?: "",
                                                email = userData?.email ?: "",
                                                phone = userData?.phone ?: "",
                                                profilePhotoURL = uploadedUrl,
                                                role = userData?.role ?: "user"
                                            )
                                            currentUser?.uid?.let { uid ->
                                                userViewModel.editProfile(
                                                    uid,
                                                    updatedModel
                                                ) { success, msg ->
                                                    isUploadingPhoto = false
                                                    Toast.makeText(
                                                        context,
                                                        msg,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    if (success) userViewModel.getUserByID(uid)
                                                }
                                            }
                                        } else {
                                            isUploadingPhoto = false
                                            Toast.makeText(
                                                context,
                                                "Upload failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_camera_alt_24),
                        contentDescription = null,
                        tint = colorResource(R.color.greenshade),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Take a Photo", color = Color.White, fontFamily = Ruluko)
                }

                Spacer(Modifier.height(8.dp))
            }
        }
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
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

    //Change Password Dialog
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            containerColor = Color(0xFF1F2937),
            title = { Text("Change Password", color = Color.White, fontFamily = Ruluko) },
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
                            currentPassword.isEmpty() -> Toast.makeText(
                                context, "Please enter current password", Toast.LENGTH_SHORT
                            ).show()
                            newPassword.isEmpty() -> Toast.makeText(
                                context, "Please enter new password", Toast.LENGTH_SHORT
                            ).show()
                            newPassword != confirmPassword -> Toast.makeText(
                                context, "Passwords do not match", Toast.LENGTH_SHORT
                            ).show()
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
                ) { Text("Update", fontFamily = Ruluko) }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF), fontFamily = Ruluko)
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = Color(0xFF1F2937),
            title = { Text("Edit Profile", color = Color.White, fontFamily = Ruluko) },
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
                        if (editName.isEmpty()) {
                            Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT)
                                .show()
                        } else {
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.greenshade),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Update", fontFamily = Ruluko) }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF), fontFamily = Ruluko)
                }
            }
        )
    }

    //Main content
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
                // Avatar with edit overlay
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (isUploadingPhoto) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF374151))
                                .border(2.dp, colorResource(R.color.greenshade), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = colorResource(R.color.greenshade),
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else if (userData?.profilePhotoURL?.isNotEmpty() == true) {
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

                    // Edit icon — opens image bottom sheet
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.greenshade))
                            .clickable { showImageSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_edit_24),
                            contentDescription = "Edit photo",
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

                // Edit profile button
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        Text("Edit Profile", fontSize = 12.sp, fontFamily = Ruluko)
                    }
                }

                // Password + Delete buttons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { showChangePasswordDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
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
                        Text("Delete Account", fontSize = 12.sp, fontFamily = Ruluko)
                    }
                }
            }
        }

        //Tabs: My Reports / My Claims
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

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorResource(R.color.greenshade))
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

        // My Claims Tab
        if (selectedTab == "claims") {

            // Toggle: Received (on my items) vs Submitted (by me)
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
                    listOf(
                        "received" to "Received",
                        "submitted" to "Submitted"
                    ).forEach { (key, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedClaimFilter == key)
                                        colorResource(R.color.greenshade)
                                    else Color.Transparent
                                )
                                .clickable { selectedClaimFilter = key }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (selectedClaimFilter == key) Color.Black
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

            if (isClaimsLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorResource(R.color.greenshade))
                    }
                }
            } else if (selectedClaimFilter == "received") {

                //  Claims received on my items
                val receivedClaims = founderClaims ?: emptyList()

                if (receivedClaims.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No claims received on your items",
                                color = Color(0xFF9CA3AF),
                                fontFamily = Ruluko,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(receivedClaims) { claim ->
                        ClaimCard(
                            itemName = claim.itemName,
                            status = claim.status,
                            date = dateFormatter.format(Date(claim.createdAt)),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onClick = {
                                val intent = Intent(context, ItemDetailActivity::class.java).apply {
                                    putExtra("itemId", claim.itemId)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }

            } else {

                //  Claims I submitted on others' items
                val submittedClaims = userClaims ?: emptyList()

                if (submittedClaims.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "You haven't submitted any claims yet",
                                color = Color(0xFF9CA3AF),
                                fontFamily = Ruluko,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(submittedClaims) { claim ->
                        ClaimCard(
                            itemName = claim.itemName,
                            status = claim.status,
                            date = dateFormatter.format(Date(claim.createdAt)),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onClick = {
                                val intent = Intent(context, ItemDetailActivity::class.java).apply {
                                    putExtra("itemId", claim.itemId)
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}