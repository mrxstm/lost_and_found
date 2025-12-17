package com.example.lost_and_found.repository

import com.example.lost_and_found.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref : DatabaseReference = database.getReference("Users")

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    callback(true, "Registration successful", "${auth.currentUser?.uid}")
                } else {
                    callback(false, "${it.exception?.message}", "")
                }
            }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    callback(true, "Login successful")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    callback(true, "Reset email send to $email")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    callback(true, "Successfully deleted")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun editProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).updateChildren(model.toMap())
    }

    override fun getAllUser(callback: (Boolean, String, List<UserModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var allUsers = mutableListOf<UserModel>();
                if(snapshot.exists()) {
                    for (data in snapshot.children) {
                        var users = data.getValue(UserModel::class.java)

                        if(users != null) {
                            allUsers.add(users)
                        }
                    }
                    callback(true, "data fetched", allUsers)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }

        })
    }

    override fun getUserByID(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(userId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val user = snapshot.getValue(UserModel::class.java)
                        if(user != null) {
                            callback(true, "Profile fetched", user)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }

            })
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(model)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    callback(true, "Successfully added")
                } else {
                    callback(false, "Error adding to database")
                }
            }
    }

}