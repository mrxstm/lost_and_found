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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import android.widget.Toast
import coil.compose.AsyncImage
import com.example.lost_and_found.R
import com.example.lost_and_found.model.ClaimModel
import com.example.lost_and_found.repository.ClaimRepoImpl
import com.example.lost_and_found.repository.CloudinaryRepoImpl
import com.example.lost_and_found.repository.UserRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.components.ClaimItemCard
import com.example.lost_and_found.view.components.ConfirmDialog
import com.example.lost_and_found.view.components.DetailRow
import com.example.lost_and_found.view.components.UserInfoRow
import com.example.lost_and_found.viewmodel.ClaimViewModel
import com.example.lost_and_found.viewmodel.CloudinaryViewModel
import com.example.lost_and_found.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemName: String = "Headphone",
    description: String = "Black Sony headphone found near the library entrance.",
    category: String = "Electronics",
    location: String = "Block A",
    date: String = "2025-01-01",
    status: String = "lost",
    imageUrl: String = "",
    reporterName: String = "Jane Doe",
    reporterPhotoUrl: String = "",
    reporterEmail: String = "",
    reporterPhone: String = "",
    isClaimed: Boolean = false,
    isOwner: Boolean = false,
    itemId: String = "",
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onPickImage: (callback: (Uri?) -> Unit) -> Unit = {},
    onTakePhoto: (callback: (Uri?) -> Unit) -> Unit = {},
    claimViewModel: ClaimViewModel = remember { ClaimViewModel(ClaimRepoImpl()) },
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    val cloudinaryViewModel = remember { CloudinaryViewModel(CloudinaryRepoImpl()) }
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    val userData by userViewModel.users.observeAsState()
    val itemClaims by claimViewModel.itemClaims.observeAsState(emptyList())
    val userClaims by claimViewModel.userClaims.observeAsState(emptyList())
    val isLoading by claimViewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userViewModel.getUserByID(it) }
    }

    // ─── Derived state ────────────────────────────────────────────────────────

    val isLostItem = status.lowercase() == "lost"

    val statusColor = if (isLostItem) Color(0xFFEF4444)
    else colorResource(R.color.greenshade)

    // Has the current user already submitted any claim on this item?
    val alreadySubmitted = userClaims?.any { it.itemId == itemId } == true

    // Is the current user's claim approved?
    val approvedClaim = userClaims?.firstOrNull {
        it.itemId == itemId && it.status == "approved"
    }

    // ✅ Is the current user's claim rejected?
    val rejectedClaim = userClaims?.firstOrNull {
        it.itemId == itemId && it.status == "rejected"
    }

    // ─── UI states ────────────────────────────────────────────────────────────

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showActionSheet by remember { mutableStateOf(false) }
    var showContactInfo by remember { mutableStateOf(false) }

    var actionMessage by remember { mutableStateOf("") }
    var proofImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val actionSheetState = rememberModalBottomSheetState()

    // ─── Labels that change based on item status ──────────────────────────────

    val actionButtonLabel = if (isLostItem) "I Found This Item" else "Submit Claim"
    val sheetTitle = if (isLostItem) "Report Found Item" else "Submit Claim"
    val sheetSubtitle = if (isLostItem)
        "Provide details about where you found this item"
    else
        "Provide details to prove this item belongs to you"
    val messageLabel = if (isLostItem)
        "Where and how did you find it?"
    else
        "Why is this your item?"
    val pendingLabel = if (isLostItem)
        "⏳ Your report is pending review by the owner"
    else
        "⏳ Your claim is pending review"
    val approvedLabel = if (isLostItem) "Report Approved!" else "Claim Approved!"
    val contactRevealLabel = if (isLostItem)
        "Tap to get owner's contact info"
    else
        "Tap to get founder's contact info"
    val contactSectionLabel = if (isLostItem)
        "Contact the owner to return the item:"
    else
        "Contact the founder to collect your item:"

    // ─── Dialogs ──────────────────────────────────────────────────────────────

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

    // ─── Action Bottom Sheet ──────────────────────────────────────────────────

    if (showActionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false },
            sheetState = actionSheetState,
            containerColor = Color(0xFF1F2937)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    sheetTitle,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Ruluko
                )
                Text(
                    sheetSubtitle,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    fontFamily = Ruluko
                )

                UserInfoRow(
                    name = userData?.full_name ?: "Loading...",
                    subtitle = userData?.email ?: "",
                    photoUrl = userData?.profilePhotoURL ?: "",
                    label = if (isLostItem) "Reporting as" else "Claiming as",
                    showBackground = false
                )

                OutlinedTextField(
                    value = actionMessage,
                    onValueChange = { actionMessage = it },
                    label = { Text(messageLabel) },
                    minLines = 3,
                    maxLines = 5,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF374151),
                        unfocusedContainerColor = Color(0xFF374151),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = colorResource(R.color.greenshade),
                        unfocusedIndicatorColor = Color(0xFF4B5563),
                        cursorColor = colorResource(R.color.greenshade),
                        focusedLabelColor = colorResource(R.color.greenshade),
                        unfocusedLabelColor = Color(0xFF9CA3AF),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Proof image upload
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF374151))
                        .border(1.dp, Color(0xFF4B5563), RoundedCornerShape(12.dp))
                        .clickable {
                            if (proofImageUri == null) {
                                onPickImage { uri -> proofImageUri = uri }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (proofImageUri != null) {
                        AsyncImage(
                            model = proofImageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { proofImageUri = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_close_24),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_photo_library_24),
                                contentDescription = null,
                                tint = colorResource(R.color.greenshade),
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "Upload proof image (optional)",
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp,
                                fontFamily = Ruluko
                            )
                        }
                    }
                }

                // Submit button
                Button(
                    onClick = {
                        if (actionMessage.isEmpty()) {
                            Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            isSubmitting = true

                            fun buildAndSubmitClaim(uploadedImageUrl: String) {
                                val claim = ClaimModel(
                                    itemId = itemId,
                                    itemName = itemName,
                                    claimantId = currentUser?.uid ?: "",
                                    claimantName = userData?.full_name ?: "",
                                    claimantPhotoUrl = userData?.profilePhotoURL ?: "",
                                    message = actionMessage,
                                    proofImageUrl = uploadedImageUrl,
                                    createdAt = System.currentTimeMillis()
                                )
                                claimViewModel.submitClaim(claim) { success, msg ->
                                    isSubmitting = false
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        actionMessage = ""
                                        proofImageUri = null
                                        showActionSheet = false
                                    }
                                }
                            }

                            if (proofImageUri != null) {
                                cloudinaryViewModel.uploadImage(
                                    context,
                                    proofImageUri!!
                                ) { uploadedUrl ->
                                    if (uploadedUrl != null) {
                                        buildAndSubmitClaim(uploadedUrl)
                                    } else {
                                        isSubmitting = false
                                        Toast.makeText(
                                            context,
                                            "Image upload failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                buildAndSubmitClaim("")
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
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Submitting...", fontFamily = Ruluko, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            "Submit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = Ruluko
                        )
                    }
                }
            }
        }
    }

    // ─── Main content ─────────────────────────────────────────────────────────

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blackshade))
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 100.dp)
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

        // Location + Date
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
            UserInfoRow(
                name = reporterName,
                subtitle = "",
                photoUrl = reporterPhotoUrl,
                label = if (isLostItem) "Reported lost by" else "Reported found by",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // ─── Action section ───────────────────────────────────────────────────
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
                // ── Non-owner action section ──────────────────────────────────
                when {

                    //  State 1: Approved — show contact info reveal
                    approvedClaim != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1F2937))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_check_circle_24),
                                    contentDescription = null,
                                    tint = colorResource(R.color.greenshade),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    approvedLabel,
                                    color = colorResource(R.color.greenshade),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = Ruluko
                                )
                            }

                            if (!showContactInfo) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            colorResource(R.color.greenshade).copy(alpha = 0.15f)
                                        )
                                        .clickable { showContactInfo = true }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.account),
                                        contentDescription = null,
                                        tint = colorResource(R.color.greenshade),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        contactRevealLabel,
                                        color = colorResource(R.color.greenshade),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = Ruluko
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                                        contentDescription = null,
                                        tint = colorResource(R.color.greenshade),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Text(
                                    contactSectionLabel,
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 12.sp,
                                    fontFamily = Ruluko
                                )
                                if (reporterEmail.isNotEmpty()) {
                                    DetailRow(
                                        icon = R.drawable.message,
                                        label = "Email",
                                        value = reporterEmail
                                    )
                                }
                                if (reporterPhone.isNotEmpty()) {
                                    DetailRow(
                                        icon = R.drawable.baseline_lock_24,
                                        label = "Phone",
                                        value = reporterPhone
                                    )
                                }
                                Text(
                                    "Hide contact info",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 11.sp,
                                    fontFamily = Ruluko,
                                    modifier = Modifier.clickable { showContactInfo = false }
                                )
                            }
                        }
                    }

                    //  State 2: Rejected — show rejection message
                    rejectedClaim != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1F2937))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_close_24),
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    if (isLostItem) "Report Rejected" else "Claim Rejected",
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = Ruluko
                                )
                            }
                            Text(
                                if (isLostItem)
                                    "The owner reviewed your report and did not approve it."
                                else
                                    "The founder reviewed your claim and did not approve it.",
                                color = Color(0xFF9CA3AF),
                                fontSize = 13.sp,
                                fontFamily = Ruluko,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    //  State 3: Pending — show pending message
                    alreadySubmitted -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1F2937))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                pendingLabel,
                                color = Color(0xFFEAB308),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Ruluko
                            )
                        }
                    }

                    //  State 4: Item already claimed/resolved by someone else
                    isClaimed -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1F2937))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isLostItem) "✅ This item has already been found"
                                else "✅ This item has already been claimed",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp,
                                fontFamily = Ruluko
                            )
                        }
                    }

                    // State 5: Open — show action button
                    else -> {
                        Button(
                            onClick = { showActionSheet = true },
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
                                painter = painterResource(
                                    if (isLostItem) R.drawable.outline_check_circle_24
                                    else R.drawable.outline_content_paste_24
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                actionButtonLabel,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = Ruluko
                            )
                        }
                    }
                }
            }
        }

        // ─── Owner sees submissions list ──────────────────────────────────────
        if (isOwner) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    if (isLostItem) "Found Reports" else "Claims",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Ruluko,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorResource(R.color.greenshade))
                    }
                }
            } else if (itemClaims.isNullOrEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1F2937))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (isLostItem) "No found reports yet"
                            else "No claims yet",
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp,
                            fontFamily = Ruluko
                        )
                    }
                }
            } else {
                items(itemClaims ?: emptyList()) { claim ->
                    ClaimItemCard(
                        claim = claim,
                        onApprove = {
                            claimViewModel.updateClaimStatus(claim.id, "approved") { _, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onReject = {
                            claimViewModel.updateClaimStatus(claim.id, "rejected") { _, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
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