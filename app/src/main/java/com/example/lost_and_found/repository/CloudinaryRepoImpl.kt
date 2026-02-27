package com.example.lost_and_found.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.InputStream
import java.util.concurrent.Executors

class CloudinaryRepoImpl : CloudinaryRepo {

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "diik5axrq",
            "api_key" to "744763892584191",
            "api_secret" to "zQ9qwzctbd2UT8ysYfO9qFMPZ4Q"
        )
    )

    override fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)
                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream,
                    ObjectUtils.asMap(
                        "public_id", fileName,
                        "folder", "lost_and_found",
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?
                imageUrl = imageUrl?.replace("http://", "https://")

                android.os.Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                android.os.Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(
        context: Context,
        imageUri: Uri
    ): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(
            imageUri, null, null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}