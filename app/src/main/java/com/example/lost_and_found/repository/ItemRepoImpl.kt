package com.example.lost_and_found.repository

import com.example.lost_and_found.model.ItemModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItemRepoImpl : ItemRepo {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.getReference("Items")

    override fun addItem(
        item: ItemModel,
        callback: (Boolean, String) -> Unit
    ) {
        // Generate unique ID for the item
        val itemId = ref.push().key ?: ""
        val newItem = item.copy(id = itemId)

        ref.child(itemId).setValue(newItem)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Item reported successfully")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun getAllItems(
        callback: (Boolean, String, List<ItemModel>?) -> Unit
    ) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ItemModel>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val item = data.getValue(ItemModel::class.java)
                        if (item != null) {
                            items.add(item)
                        }
                    }
                }
                callback(true, "Items fetched", items)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getItemById(
        itemId: String,
        callback: (Boolean, String, ItemModel?) -> Unit
    ) {
        ref.child(itemId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val item = snapshot.getValue(ItemModel::class.java)
                        callback(true, "Item fetched", item)
                    } else {
                        callback(false, "Item not found", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun getItemsByUser(
        userId: String,
        callback: (Boolean, String, List<ItemModel>?) -> Unit
    ) {
        ref.orderByChild("reportedBy").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<ItemModel>()
                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            val item = data.getValue(ItemModel::class.java)
                            if (item != null) {
                                items.add(item)
                            }
                        }
                    }
                    callback(true, "Items fetched", items)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun getItemsByStatus(
        status: String,
        callback: (Boolean, String, List<ItemModel>?) -> Unit
    ) {
        ref.orderByChild("status").equalTo(status)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<ItemModel>()
                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            val item = data.getValue(ItemModel::class.java)
                            if (item != null) {
                                items.add(item)
                            }
                        }
                    }
                    callback(true, "Items fetched", items)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun updateItem(
        itemId: String,
        item: ItemModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(itemId).updateChildren(item.toMap())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Item updated successfully")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun deleteItem(
        itemId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(itemId).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Item deleted successfully")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }
}