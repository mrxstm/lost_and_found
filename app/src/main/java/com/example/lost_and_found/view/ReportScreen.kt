package com.example.lost_and_found.view

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lost_and_found.R
import com.example.lost_and_found.model.ItemModel
import com.example.lost_and_found.repository.CloudinaryRepoImpl
import com.example.lost_and_found.repository.ItemRepoImpl
import com.example.lost_and_found.repository.UserRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.viewmodel.CloudinaryViewModel
import com.example.lost_and_found.viewmodel.ItemViewModel
import com.example.lost_and_found.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    preSelectedStatus: String = "lost",
    onPickImage: (callback: (Uri?) -> Unit) -> Unit = {},
    onTakePhoto: (callback: (Uri?) -> Unit) -> Unit = {},
    isEditMode: Boolean = false,
    itemId: String = "",
    prefillName: String = "",
    prefillDescription: String = "",
    prefillLocation: String = "",
    prefillCategory: String = "",
    prefillDate: String = "",
    prefillImageUrl: String = "",
    onBack: () -> Unit = {},
    showBackButton: Boolean = false,

) {
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // ViewModels
    val cloudinaryViewModel = remember { CloudinaryViewModel(CloudinaryRepoImpl()) }
    val itemViewModel = remember { ItemViewModel(ItemRepoImpl()) }
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userData by userViewModel.users.observeAsState()

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userViewModel.getUserByID(it) }
    }

    // Form states
    var itemName by remember { mutableStateOf(prefillName) }
    var description by remember { mutableStateOf(prefillDescription) }
    var location by remember { mutableStateOf(prefillLocation) }
    var selectedCategory by remember { mutableStateOf(prefillCategory) }
    var selectedStatus by remember { mutableStateOf(preSelectedStatus) }
    var selectedDate by remember { mutableStateOf(prefillDate) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageRemoved by remember { mutableStateOf(false) }

    // UI states
    var categoryExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showImageBottomSheet by remember { mutableStateOf(false) }

    val categories = listOf(
        "Electronics", "Clothing", "Accessories",
        "Documents", "Keys", "Bags", "Others"
    )

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF1F2937),
        unfocusedContainerColor = Color(0xFF1F2937),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedIndicatorColor = colorResource(R.color.greenshade),
        unfocusedIndicatorColor = Color(0xFF374151),
        cursorColor = colorResource(R.color.greenshade),
        focusedLabelColor = colorResource(R.color.greenshade),
        unfocusedLabelColor = Color(0xFF9CA3AF),
    )

    // Date picker state
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        selectedDate = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = colorResource(R.color.greenshade))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Image picker bottom sheet
    val bottomSheetState = rememberModalBottomSheetState()
    if (showImageBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showImageBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = Color(0xFF1F2937)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Select Image",
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
                            showImageBottomSheet = false
                            onPickImage { uri -> selectedImageUri = uri }
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
                            showImageBottomSheet = false
                            onTakePhoto { uri -> selectedImageUri = uri }
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

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blackshade))
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {

        // Title
        item {
            if (showBackButton) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F2937))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            Text(
                if (isEditMode) "Edit Item" else "Report Item",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Ruluko
            )
            Text(
                if (isEditMode) "Update the item details" else "Fill in the details of the item",
                color = Color(0xFF9CA3AF),
                fontSize = 12.sp,
                fontFamily = Ruluko
            )
        }

        // Lost / Found Toggle
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1F2937))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("lost", "found").forEach { status ->
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
                            fontSize = 14.sp,
                            fontFamily = Ruluko
                        )
                    }
                }
            }
        }

        // Image Upload
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1F2937))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF374151),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        if (selectedImageUri == null && (!isEditMode || existingImageRemoved)) {
                            showImageBottomSheet = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else if (isEditMode && prefillImageUrl.isNotEmpty() && !existingImageRemoved) {
                    AsyncImage(
                        model = prefillImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_camera_alt_24),
                            contentDescription = null,
                            tint = colorResource(R.color.greenshade),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            "Tap to upload image",
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp,
                            fontFamily = Ruluko
                        )
                    }
                }

                // Remove button
                if (selectedImageUri != null || (isEditMode && prefillImageUrl.isNotEmpty() && !existingImageRemoved)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable {
                                selectedImageUri = null
                                existingImageRemoved = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_close_24),
                            contentDescription = "Remove image",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Item Name
        item {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Description
        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Location
        item {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_location_on_24),
                        contentDescription = null,
                        tint = colorResource(R.color.greenshade)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Dropdown
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    label = { Text("Category") },
                    readOnly = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.clickable { categoryExpanded = true }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryExpanded = true }
                )
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(Color(0xFF1F2937))
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(category, color = Color.White, fontFamily = Ruluko)
                            },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Date Picker
        item {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                label = { Text("Date") },
                readOnly = true,
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.twotone_calendar_today_24),
                        contentDescription = null,
                        tint = colorResource(R.color.greenshade),
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )
        }

        // Submit Button
        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    when {
                        itemName.isEmpty() -> {
                            Toast.makeText(context, "Please enter item name", Toast.LENGTH_SHORT).show()
                        }
                        description.isEmpty() -> {
                            Toast.makeText(context, "Please enter description", Toast.LENGTH_SHORT).show()
                        }
                        location.isEmpty() -> {
                            Toast.makeText(context, "Please enter location", Toast.LENGTH_SHORT).show()
                        }
                        selectedCategory.isEmpty() -> {
                            Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show()
                        }
                        selectedDate.isEmpty() -> {
                            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            isUploading = true
                            if (selectedImageUri != null) {
                                // New image — upload to Cloudinary first
                                cloudinaryViewModel.uploadImage(
                                    context,
                                    selectedImageUri!!
                                ) { imageUrl ->
                                    if (imageUrl != null) {
                                        val item = ItemModel(
                                            id = itemId,
                                            itemName = itemName,
                                            description = description,
                                            category = selectedCategory,
                                            location = location,
                                            status = selectedStatus,
                                            imageUrl = imageUrl,
                                            reportedBy = currentUser?.uid ?: "",
                                            reporterName = userData?.full_name ?: "",
                                            reporterPhotoUrl = userData?.profilePhotoURL ?: "",
                                            date = selectedDate,
                                            createdAt = System.currentTimeMillis(),
                                            reporterEmail = userData?.email ?: "",
                                            reporterPhone = userData?.phone ?: "",
                                        )
                                        if (isEditMode) {
                                            itemViewModel.updateItem(itemId, item) { success, msg ->
                                                isUploading = false
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                if (success) {
                                                    (context as? android.app.Activity)?.finish()
                                                }
                                            }
                                        } else {
                                            itemViewModel.addItem(item) { success, msg ->
                                                isUploading = false
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                if (success) {
                                                    itemName = ""
                                                    description = ""
                                                    location = ""
                                                    selectedCategory = ""
                                                    selectedDate = ""
                                                    selectedImageUri = null
                                                }
                                            }
                                        }
                                    } else {
                                        isUploading = false
                                        Toast.makeText(
                                            context,
                                            "Image upload failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                // No new image
                                val item = ItemModel(
                                    id = itemId,
                                    itemName = itemName,
                                    description = description,
                                    category = selectedCategory,
                                    location = location,
                                    status = selectedStatus,
                                    imageUrl = when {
                                        existingImageRemoved -> ""
                                        isEditMode -> prefillImageUrl
                                        else -> ""
                                    },
                                    reportedBy = currentUser?.uid ?: "",
                                    reporterName = userData?.full_name ?: "",
                                    reporterPhotoUrl = userData?.profilePhotoURL ?: "",
                                    reporterEmail = userData?.email ?: "",
                                    reporterPhone = userData?.phone ?: "",
                                    date = selectedDate,
                                    createdAt = System.currentTimeMillis()
                                )
                                if (isEditMode) {
                                    itemViewModel.updateItem(itemId, item) { success, msg ->
                                        isUploading = false
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        if (success) {
                                            (context as? android.app.Activity)?.finish()
                                        }
                                    }
                                } else {
                                    itemViewModel.addItem(item) { success, msg ->
                                        isUploading = false
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        if (success) {
                                            itemName = ""
                                            description = ""
                                            location = ""
                                            selectedCategory = ""
                                            selectedDate = ""
                                            selectedImageUri = null
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.greenshade),
                    contentColor = Color.Black
                ),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Uploading...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = Ruluko
                    )
                } else {
                    Text(
                        if (isEditMode) "Update Item" else "Submit Report",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = Ruluko
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    ReportScreen()
}