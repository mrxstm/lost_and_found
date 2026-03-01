package com.example.lost_and_found

import com.example.lost_and_found.model.ClaimModel
import com.example.lost_and_found.repository.ClaimRepo
import com.example.lost_and_found.viewmodel.ClaimViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ClaimViewModelTest {

    @Test
    fun submitClaim_success_test() {
        val repo = mock<ClaimRepo>()
        val viewModel = ClaimViewModel(repo)

        val testClaim = ClaimModel(
            itemId = "item123",
            itemName = "Lost Keys",
            claimantId = "user456",
            claimantName = "John Doe",
            message = "These are my keys",
            founderId = "user789"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Claim submitted successfully")
            null
        }.`when`(repo).submitClaim(any(), any())

        var successResult = false
        var messageResult = ""

        viewModel.submitClaim(testClaim) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Claim submitted successfully", messageResult)
        verify(repo).submitClaim(any(), any())
    }

    @Test
    fun submitClaim_failure_test() {
        val repo = mock<ClaimRepo>()
        val viewModel = ClaimViewModel(repo)

        val testClaim = ClaimModel(
            itemId = "item123",
            itemName = "Lost Keys",
            claimantId = "user456",
            claimantName = "John Doe",
            message = "These are my keys",
            founderId = "user789"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to submit claim")
            null
        }.`when`(repo).submitClaim(any(), any())

        var successResult = true
        var messageResult = ""

        viewModel.submitClaim(testClaim) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertFalse(successResult)
        assertEquals("Failed to submit claim", messageResult)
        verify(repo).submitClaim(any(), any())
    }

    @Test
    fun updateClaimStatus_approved_test() {
        val repo = mock<ClaimRepo>()
        val viewModel = ClaimViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Claim approved")
            null
        }.`when`(repo).updateClaimStatus(eq("claim123"), eq("approved"), any())

        var successResult = false
        var messageResult = ""

        viewModel.updateClaimStatus("claim123", "approved") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Claim approved", messageResult)
        verify(repo).updateClaimStatus(eq("claim123"), eq("approved"), any())
    }
}