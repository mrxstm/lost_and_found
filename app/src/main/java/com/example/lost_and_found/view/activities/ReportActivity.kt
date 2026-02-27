package com.example.lost_and_found.view

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.lost_and_found.R
import com.example.lost_and_found.utils.ImageUtils
class ReportActivity : ComponentActivity() {

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

        val preSelectedStatus = intent.getStringExtra("status") ?: "lost"

        setContent {
            ReportScreen(
                preSelectedStatus = preSelectedStatus,
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