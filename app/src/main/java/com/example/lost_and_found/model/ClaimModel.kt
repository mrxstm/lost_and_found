package com.example.lost_and_found.model

data class ClaimModel(
    val id: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val claimantId: String = "",
    val claimantName: String = "",
    val claimantPhotoUrl: String = "",
    val message: String = "",
    val proofImageUrl: String = "",
    val status: String = "pending",     // "pending" | "approved" | "rejected"
    val createdAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "itemId" to itemId,
            "itemName" to itemName,
            "claimantId" to claimantId,
            "claimantName" to claimantName,
            "claimantPhotoUrl" to claimantPhotoUrl,
            "message" to message,
            "proofImageUrl" to proofImageUrl,
            "status" to status,
            "createdAt" to createdAt
        )
    }
}