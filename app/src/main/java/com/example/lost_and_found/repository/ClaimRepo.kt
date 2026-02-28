package com.example.lost_and_found.repository

import com.example.lost_and_found.model.ClaimModel

interface ClaimRepo {

    fun submitClaim(
        claim: ClaimModel,
        callback: (Boolean, String) -> Unit
    )

    fun getClaimsByUser(
        userId: String,
        callback: (Boolean, String, List<ClaimModel>?) -> Unit
    )

    fun getClaimsByItem(
        itemId: String,
        callback: (Boolean, String, List<ClaimModel>?) -> Unit
    )

    fun updateClaimStatus(
        claimId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    )

    fun deleteClaim(
        claimId: String,
        callback: (Boolean, String) -> Unit
    )

    fun removeListeners()


}