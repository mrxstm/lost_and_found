package com.example.lost_and_found.model

data class ItemModel(
    val id: String = "",
    val itemName: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "",
    val status: String = "lost",        // "lost" | "found"
    val imageUrl: String = "",
    val reportedBy: String = "",        // Firebase user UID
    val reporterName: String = "",
    val reporterPhotoUrl: String = "",
    val date: String = "",
    val createdAt: Long = 0L,
    val isClaimed: Boolean = false,
    val reporterEmail: String = "",
    val reporterPhone: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "itemName" to itemName,
            "description" to description,
            "category" to category,
            "location" to location,
            "status" to status,
            "imageUrl" to imageUrl,
            "reportedBy" to reportedBy,
            "reporterName" to reporterName,
            "reporterPhotoUrl" to reporterPhotoUrl,
            "date" to date,
            "createdAt" to createdAt,
            "isClaimed" to isClaimed,
            "reporterEmail" to reporterEmail,
            "reporterPhone" to reporterPhone

        )
    }
}