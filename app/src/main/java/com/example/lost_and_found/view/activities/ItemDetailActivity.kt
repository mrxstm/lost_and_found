package com.example.lost_and_found.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.lost_and_found.R

class ItemDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this, R.color.blackshade)),
            navigationBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this, R.color.blackshade))
        )

        // Get data passed from intent
        val itemName = intent.getStringExtra("itemName") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val status = intent.getStringExtra("status") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val reporterName = intent.getStringExtra("reporterName") ?: ""
        val reporterPhotoUrl = intent.getStringExtra("reporterPhotoUrl") ?: ""
        val isOwner = intent.getBooleanExtra("isOwner", false)

        setContent {
            ItemDetailScreen(
                itemName = itemName,
                description = description,
                category = category,
                location = location,
                date = date,
                status = status,
                imageUrl = imageUrl,
                reporterName = reporterName,
                reporterPhotoUrl = reporterPhotoUrl,
                isOwner = isOwner,
                onBack = { finish() },
                onEdit = { },
                onDelete = { finish() },
                onClaim = { }
            )
        }
    }
}