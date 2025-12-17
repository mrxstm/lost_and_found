package com.example.lost_and_found.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lost_and_found.model.UserModel
import com.example.lost_and_found.repository.UserRepo

class UserViewModel(val repo : UserRepo) : ViewModel() {

    fun register(email : String, password : String, callback : (Boolean, String, String)-> Unit) {
        repo.register(email, password, callback)
    }

    fun login(email : String, password : String, callback : (Boolean, String)-> Unit) {
        repo.login(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteAccount(userId, callback)
    }

    fun editProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.editProfile(userId, model, callback)
    }

    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers : MutableLiveData<List<UserModel>?>
        get() =_allUsers


    fun getAllUser() {
        repo.getAllUser {
            success, msg, data ->
            if(success) {
                _allUsers.postValue(data)
            }
        }
    }

    private val _users = MutableLiveData<UserModel?>()
    val users : MutableLiveData<UserModel?>
        get() = _users

    fun getUserByID(userId : String) {
        repo.getUserByID(userId) {
            success, msg, data ->
            if(success) {
                _users.postValue(data)
            }
        }
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }

}