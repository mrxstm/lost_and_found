package com.example.lost_and_found.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import java.io.File

class ImageUtils(
    private val activity: Activity,
    private val registryOwner: ActivityResultRegistryOwner
) {
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private var onImageSelectedCallback: ((Uri?) -> Unit)? = null
    private var cameraImageUri: Uri? = null

    fun registerLaunchers(onImageSelected: (Uri?) -> Unit) {
        onImageSelectedCallback = onImageSelected

        // Gallery launcher
        galleryLauncher = registryOwner.activityResultRegistry.register(
            "galleryLauncher", ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val uri = result.data?.data
            if (result.resultCode == Activity.RESULT_OK && uri != null) {
                onImageSelectedCallback?.invoke(uri)
            } else {
                Log.e("ImageUtils", "Gallery selection cancelled or failed")
            }
        }

        // Camera launcher
        cameraLauncher = registryOwner.activityResultRegistry.register(
            "cameraLauncher", ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                onImageSelectedCallback?.invoke(cameraImageUri)
            } else {
                Log.e("ImageUtils", "Camera cancelled or failed")
            }
        }

        // Gallery permission launcher
        galleryPermissionLauncher = registryOwner.activityResultRegistry.register(
            "galleryPermissionLauncher", ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) openGallery()
            else Log.e("ImageUtils", "Gallery permission denied")
        }

        // Camera permission launcher — separate from gallery
        cameraPermissionLauncher = registryOwner.activityResultRegistry.register(
            "cameraPermissionLauncher", ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) openCamera()
            else Log.e("ImageUtils", "Camera permission denied")
        }
    }

    fun launchImagePicker() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                activity, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            galleryPermissionLauncher.launch(permission)
        } else {
            openGallery()
        }
    }

    fun launchCamera() {
        // Check camera permission first
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun openGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        try {
            val imageFile = File(
                activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "camera_${System.currentTimeMillis()}.jpg"
            )
            cameraImageUri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                imageFile
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            }
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("ImageUtils", "Camera error: ${e.message}")
        }
    }
}