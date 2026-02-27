package com.example.lost_and_found.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.lost_and_found.repository.CloudinaryRepo

class CloudinaryViewModel(val repo: CloudinaryRepo) : ViewModel() {

    fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    ) {
        repo.uploadImage(context, imageUri, callback)
    }
}