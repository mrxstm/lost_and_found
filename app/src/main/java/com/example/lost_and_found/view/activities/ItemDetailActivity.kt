package com.example.lost_and_found.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.ContextCompat
import com.example.lost_and_found.R
import com.example.lost_and_found.repository.ItemRepoImpl
import com.example.lost_and_found.viewmodel.ItemViewModel
import com.google.firebase.auth.FirebaseAuth

class ItemDetailActivity : ComponentActivity() {

    private val itemViewModel by lazy { ItemViewModel(ItemRepoImpl()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this, R.color.blackshade)),
            navigationBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this, R.color.blackshade))
        )

        val itemId = intent.getStringExtra("itemId") ?: ""
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Fetch fresh item data from Firebase
        itemViewModel.getItemById(itemId)

        setContent {
            // Observe live item data from Firebase
            val item by itemViewModel.item.observeAsState()

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
                isOwner = item?.reportedBy == currentUserId,
                onBack = { finish() },
                onEdit = {
                    val intent = android.content.Intent(
                        this,
                        EditItemActivity::class.java
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
                onClaim = { }
            )
        }
    }
}