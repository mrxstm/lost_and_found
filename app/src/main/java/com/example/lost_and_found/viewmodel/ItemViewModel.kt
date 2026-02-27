package com.example.lost_and_found.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lost_and_found.model.ItemModel
import com.example.lost_and_found.repository.ItemRepo

class ItemViewModel(val repo: ItemRepo) : ViewModel() {

    // LiveData for all items
    private val _allItems = MutableLiveData<List<ItemModel>?>()
    val allItems: MutableLiveData<List<ItemModel>?>
        get() = _allItems

    // LiveData for items by status (lost/found)
    private val _itemsByStatus = MutableLiveData<List<ItemModel>?>()
    val itemsByStatus: MutableLiveData<List<ItemModel>?>
        get() = _itemsByStatus

    // LiveData for items by user
    private val _userItems = MutableLiveData<List<ItemModel>?>()
    val userItems: MutableLiveData<List<ItemModel>?>
        get() = _userItems

    // LiveData for single item
    private val _item = MutableLiveData<ItemModel?>()
    val item: MutableLiveData<ItemModel?>
        get() = _item

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean>
        get() = _isLoading

    // LiveData for error/success messages
    private val _message = MutableLiveData<String?>()
    val message: MutableLiveData<String?>
        get() = _message

    fun addItem(
        item: ItemModel,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.addItem(item) { success, msg ->
            _isLoading.postValue(false)
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    fun getAllItems() {
        _isLoading.postValue(true)
        repo.getAllItems { success, msg, data ->
            _isLoading.postValue(false)
            if (success) {
                _allItems.postValue(data)
            } else {
                _message.postValue(msg)
            }
        }
    }

    fun getItemById(itemId: String) {
        _isLoading.postValue(true)
        repo.getItemById(itemId) { success, msg, data ->
            _isLoading.postValue(false)
            if (success) {
                _item.postValue(data)
            } else {
                _message.postValue(msg)
            }
        }
    }

    fun getItemsByUser(userId: String) {
        _isLoading.postValue(true)
        repo.getItemsByUser(userId) { success, msg, data ->
            _isLoading.postValue(false)
            if (success) {
                _userItems.postValue(data)
            } else {
                _message.postValue(msg)
            }
        }
    }

    fun getItemsByStatus(status: String) {
        _isLoading.postValue(true)
        repo.getItemsByStatus(status) { success, msg, data ->
            _isLoading.postValue(false)
            if (success) {
                _itemsByStatus.postValue(data)
            } else {
                _message.postValue(msg)
            }
        }
    }

    fun updateItem(
        itemId: String,
        item: ItemModel,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.updateItem(itemId, item) { success, msg ->
            _isLoading.postValue(false)
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    fun deleteItem(
        itemId: String,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.deleteItem(itemId) { success, msg ->
            _isLoading.postValue(false)
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    // Clear message after showing it
    fun clearMessage() {
        _message.postValue(null)
    }
}