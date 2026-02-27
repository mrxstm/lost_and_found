package com.example.lost_and_found.repository

import android.content.Context
import android.net.Uri

interface CloudinaryRepo {

    fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    )

    fun getFileNameFromUri(
        context: Context,
        imageUri: Uri
    ): String?
}