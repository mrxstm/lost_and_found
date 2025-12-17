package com.example.lost_and_found.repository

import com.example.lost_and_found.model.UserModel

interface UserRepo {

    //register callback has two String parameters because one is for user_id
    fun register(email : String, password : String, callback : (Boolean, String, String)-> Unit)

    fun login(email : String, password : String, callback : (Boolean, String)-> Unit)

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit)

    fun editProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit)

    fun getAllUser(callback: (Boolean, String, List<UserModel>?) -> Unit)

    fun getUserByID(userId : String, callback: (Boolean, String, UserModel?) -> Unit)

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit)



}