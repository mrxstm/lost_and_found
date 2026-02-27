package com.example.lost_and_found.view

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.lost_and_found.R
import com.example.lost_and_found.utils.ImageUtils

class EditItemActivity : ComponentActivity() {

    private lateinit var imageUtils: ImageUtils
    private var onImageSelected: ((Uri?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(this, R.color.blackshade)
            ),
            navigationBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(this, R.color.blackshade)
            )
        )

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            onImageSelected?.invoke(uri)
        }

        // Get item data from intent
        val itemId = intent.getStringExtra("itemId") ?: ""
        val itemName = intent.getStringExtra("itemName") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val status = intent.getStringExtra("status") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                ReportScreen(
                    preSelectedStatus = status,
                    isEditMode = true,
                    itemId = itemId,
                    prefillName = itemName,
                    prefillDescription = description,
                    prefillLocation = location,
                    prefillCategory = category,
                    prefillDate = date,
                    prefillImageUrl = imageUrl,
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
}