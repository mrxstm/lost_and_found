package com.example.lost_and_found.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.example.lost_and_found.R
import com.example.lost_and_found.repository.ClaimRepoImpl
import com.example.lost_and_found.repository.ItemRepoImpl
import com.example.lost_and_found.utils.ImageUtils
import com.example.lost_and_found.viewmodel.ClaimViewModel
import com.example.lost_and_found.viewmodel.ItemViewModel
import com.google.firebase.auth.FirebaseAuth

class ItemDetailActivity : ComponentActivity() {

    private val itemViewModel by lazy { ItemViewModel(ItemRepoImpl()) }
    private val claimViewModel by lazy { ClaimViewModel(ClaimRepoImpl()) }
    private lateinit var imageUtils: ImageUtils
    private var onImageSelected: ((Uri?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this, R.color.blackshade)),
            navigationBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this, R.color.blackshade))
        )

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            onImageSelected?.invoke(uri)
        }

        val itemId = intent.getStringExtra("itemId") ?: ""
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        itemViewModel.getItemById(itemId)

        setContent {
            val item by itemViewModel.item.observeAsState()

            // ✅ Derive isOwner safely using remember(item)
            val isOwner = remember(item) { item?.reportedBy == currentUserId }

            // ✅ Key on item?.reportedBy — null while loading, real value once loaded
            // This guarantees the correct branch fires exactly once after item loads
            LaunchedEffect(item?.reportedBy) {
                val reportedBy = item?.reportedBy ?: return@LaunchedEffect
                if (reportedBy == currentUserId) {
                    claimViewModel.getClaimsByItem(itemId)
                } else {
                    claimViewModel.getClaimsByUser(currentUserId)
                }
            }

            ItemDetailScreen(
                itemName = item?.itemName ?: "",
                description = item?.description ?: "",
                category = item?.category ?: "",
                location = item?.location ?: "",
                date = item?.date ?: "",
                status = item?.status ?: "",
                imageUrl = item?.imageUrl ?: "",
                reporterName = item?.reporterName ?: "",
                reporterPhotoUrl = item?.reporterPhotoUrl ?: "",
                reporterEmail = item?.reporterEmail ?: "",
                reporterPhone = item?.reporterPhone ?: "",
                isClaimed = item?.isClaimed ?: false,
                isOwner = isOwner,
                itemId = itemId,
                claimViewModel = claimViewModel,
                onBack = { finish() },
                reportedBy = item?.reportedBy ?: "",
                onEdit = {
                    val intent = android.content.Intent(
                        this, EditItemActivity::class.java
                    ).apply {
                        putExtra("itemId", itemId)
                        putExtra("itemName", item?.itemName ?: "")
                        putExtra("description", item?.description ?: "")
                        putExtra("category", item?.category ?: "")
                        putExtra("location", item?.location ?: "")
                        putExtra("date", item?.date ?: "")
                        putExtra("status", item?.status ?: "")
                        putExtra("imageUrl", item?.imageUrl ?: "")
                    }
                    startActivity(intent)
                },
                onDelete = {
                    itemViewModel.deleteItem(itemId) { success, msg ->
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        if (success) finish()
                    }
                },
                onPickImage = { callback ->
                    onImageSelected = callback
                    imageUtils.launchImagePicker()
                },
                onTakePhoto = { callback ->
                    onImageSelected = callback
                    imageUtils.launchCamera()
                }
            )
        }
    }
}