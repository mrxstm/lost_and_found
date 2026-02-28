package com.example.lost_and_found.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lost_and_found.model.ClaimModel
import com.example.lost_and_found.repository.ClaimRepo

class ClaimViewModel(val repo: ClaimRepo) : ViewModel() {

    private val _userClaims = MutableLiveData<List<ClaimModel>?>()
    val userClaims: MutableLiveData<List<ClaimModel>?>
        get() = _userClaims

    private val _itemClaims = MutableLiveData<List<ClaimModel>?>()
    val itemClaims: MutableLiveData<List<ClaimModel>?>
        get() = _itemClaims

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean>
        get() = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: MutableLiveData<String?>
        get() = _message

    fun submitClaim(
        claim: ClaimModel,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.submitClaim(claim) { success, msg ->
            _isLoading.postValue(false)
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    fun getClaimsByUser(userId: String) {
        _isLoading.postValue(true)
        repo.getClaimsByUser(userId) { success, msg, data ->
            _isLoading.postValue(false)
            if (success) {
                _userClaims.postValue(data)
            } else {
                _message.postValue(msg)
            }
        }
    }

    fun getClaimsByItem(itemId: String) {
        _isLoading.postValue(true)
        repo.getClaimsByItem(itemId) { success, msg, data ->
            _isLoading.postValue(false)
            if (success) {
                _itemClaims.postValue(data)
            } else {
                _message.postValue(msg)
            }
        }
    }

    fun updateClaimStatus(
        claimId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.updateClaimStatus(claimId, status) { success, msg ->
            _isLoading.postValue(false)
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    fun deleteClaim(
        claimId: String,
        callback: (Boolean, String) -> Unit
    ) {
        _isLoading.postValue(true)
        repo.deleteClaim(claimId) { success, msg ->
            _isLoading.postValue(false)
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    fun clearMessage() {
        _message.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()
        repo.removeListeners()
    }
}