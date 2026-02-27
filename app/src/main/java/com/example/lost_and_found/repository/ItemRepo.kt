package com.example.lost_and_found.repository

import com.example.lost_and_found.model.ItemModel

interface ItemRepo {

    fun addItem(
        item: ItemModel,
        callback: (Boolean, String) -> Unit
    )

    fun getAllItems(
        callback: (Boolean, String, List<ItemModel>?) -> Unit
    )

    fun getItemById(
        itemId: String,
        callback: (Boolean, String, ItemModel?) -> Unit
    )

    fun getItemsByUser(
        userId: String,
        callback: (Boolean, String, List<ItemModel>?) -> Unit
    )

    fun getItemsByStatus(
        status: String,
        callback: (Boolean, String, List<ItemModel>?) -> Unit
    )

    fun updateItem(
        itemId: String,
        item: ItemModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteItem(
        itemId: String,
        callback: (Boolean, String) -> Unit
    )
}