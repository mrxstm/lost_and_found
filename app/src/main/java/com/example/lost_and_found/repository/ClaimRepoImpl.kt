package com.example.lost_and_found.repository

import com.example.lost_and_found.model.ClaimModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ClaimRepoImpl : ClaimRepo {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.getReference("Claims")

    private var userClaimsListener: ValueEventListener? = null
    private var userClaimsRef: DatabaseReference? = null

    private var itemClaimsListener: ValueEventListener? = null
    private var itemClaimsRef: DatabaseReference? = null

    //  New listener reference for founder claims
    private var founderClaimsListener: ValueEventListener? = null
    private var founderClaimsRef: DatabaseReference? = null

    override fun submitClaim(
        claim: ClaimModel,
        callback: (Boolean, String) -> Unit
    ) {
        val claimId = ref.push().key ?: ""
        val newClaim = claim.copy(id = claimId)

        ref.child(claimId).setValue(newClaim)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Claim submitted successfully")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun getClaimsByUser(
        userId: String,
        callback: (Boolean, String, List<ClaimModel>?) -> Unit
    ) {
        userClaimsListener?.let { userClaimsRef?.removeEventListener(it) }

        val query = ref.orderByChild("claimantId").equalTo(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val claims = mutableListOf<ClaimModel>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val claim = data.getValue(ClaimModel::class.java)
                        if (claim != null) claims.add(claim)
                    }
                }
                callback(true, "Claims fetched", claims)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        }

        userClaimsListener = listener
        userClaimsRef = ref
        query.addValueEventListener(listener)
    }

    override fun getClaimsByItem(
        itemId: String,
        callback: (Boolean, String, List<ClaimModel>?) -> Unit
    ) {
        itemClaimsListener?.let { itemClaimsRef?.removeEventListener(it) }

        val query = ref.orderByChild("itemId").equalTo(itemId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val claims = mutableListOf<ClaimModel>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val claim = data.getValue(ClaimModel::class.java)
                        if (claim != null) claims.add(claim)
                    }
                }
                callback(true, "Claims fetched", claims)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        }

        itemClaimsListener = listener
        itemClaimsRef = ref
        query.addValueEventListener(listener)
    }

    //  New — real-time listener for all claims on founder's items
    override fun getClaimsByFounder(
        founderId: String,
        callback: (Boolean, String, List<ClaimModel>?) -> Unit
    ) {
        founderClaimsListener?.let { founderClaimsRef?.removeEventListener(it) }

        val query = ref.orderByChild("founderId").equalTo(founderId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val claims = mutableListOf<ClaimModel>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val claim = data.getValue(ClaimModel::class.java)
                        if (claim != null) claims.add(claim)
                    }
                }
                callback(true, "Claims fetched", claims)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        }

        founderClaimsListener = listener
        founderClaimsRef = ref
        query.addValueEventListener(listener)
    }

    override fun updateClaimStatus(
        claimId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(claimId).child("status").setValue(status)
            .addOnSuccessListener {
                if (status == "approved") {
                    ref.child(claimId).child("itemId").get()
                        .addOnSuccessListener { snapshot ->
                            val itemId = snapshot.getValue(String::class.java) ?: ""
                            if (itemId.isEmpty()) {
                                callback(true, "Claim approved")
                                return@addOnSuccessListener
                            }
                            ref.orderByChild("itemId").equalTo(itemId).get()
                                .addOnSuccessListener { itemSnapshot ->
                                    val updates = mutableMapOf<String, Any>()
                                    for (data in itemSnapshot.children) {
                                        val id = data.key ?: continue
                                        val claimStatus = data.child("status")
                                            .getValue(String::class.java)
                                        if (id != claimId && claimStatus == "pending") {
                                            updates["$id/status"] = "rejected"
                                        }
                                    }
                                    if (updates.isNotEmpty()) {
                                        ref.updateChildren(updates)
                                            .addOnSuccessListener {
                                                callback(true, "Claim approved")
                                            }
                                            .addOnFailureListener {
                                                callback(true, "Claim approved")
                                            }
                                    } else {
                                        callback(true, "Claim approved")
                                    }
                                }
                                .addOnFailureListener { callback(true, "Claim approved") }
                        }
                        .addOnFailureListener { callback(true, "Claim approved") }
                } else {
                    callback(true, "Claim status updated")
                }
            }
            .addOnFailureListener { e ->
                callback(false, "${e.message}")
            }
    }

    override fun deleteClaim(
        claimId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(claimId).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Claim deleted successfully")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun removeListeners() {
        userClaimsListener?.let { userClaimsRef?.removeEventListener(it) }
        itemClaimsListener?.let { itemClaimsRef?.removeEventListener(it) }
        founderClaimsListener?.let { founderClaimsRef?.removeEventListener(it) }
        userClaimsListener = null
        itemClaimsListener = null
        founderClaimsListener = null
        userClaimsRef = null
        itemClaimsRef = null
        founderClaimsRef = null
    }
}