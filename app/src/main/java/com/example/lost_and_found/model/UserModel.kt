package com.example.lost_and_found.model

data class UserModel(
    val id : String = "",
    val full_name : String = "",
    val email : String = "",
    val password : String = "",
    val profilePhotoURL : String = "",
    val role : String = "user",
) {

    //return type of toMap is a Map<String, Any>
    fun toMap() : Map<String, Any?> {
        return mapOf(
            "id" to id,
            "full_name" to full_name,
            "email" to email,
            "profilePhotoURL" to profilePhotoURL,
        )
    }
}