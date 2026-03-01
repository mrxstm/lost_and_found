package com.example.lost_and_found

import com.example.lost_and_found.model.ItemModel
import com.example.lost_and_found.repository.ItemRepo
import com.example.lost_and_found.viewmodel.ItemViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ItemViewModelTest {

    @Test
    fun addItem_success_test() {
        val repo = mock<ItemRepo>()
        val viewModel = ItemViewModel(repo)

        val testItem = ItemModel(
            itemName = "Lost Keys",
            description = "Black car keys",
            category = "Keys",
            location = "Block A",
            status = "lost",
            reportedBy = "user123"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Item reported successfully")
            null
        }.`when`(repo).addItem(any(), any())

        var successResult = false
        var messageResult = ""

        viewModel.addItem(testItem) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Item reported successfully", messageResult)
        verify(repo).addItem(any(), any())
    }

    @Test
    fun deleteItem_success_test() {
        val repo = mock<ItemRepo>()
        val viewModel = ItemViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Item deleted successfully")
            null
        }.`when`(repo).deleteItem(eq("item123"), any())

        var successResult = false
        var messageResult = ""

        viewModel.deleteItem("item123") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Item deleted successfully", messageResult)
        verify(repo).deleteItem(eq("item123"), any())
    }

    @Test
    fun deleteItem_failure_test() {
        val repo = mock<ItemRepo>()
        val viewModel = ItemViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to delete item")
            null
        }.`when`(repo).deleteItem(eq("item123"), any())

        var successResult = true
        var messageResult = ""

        viewModel.deleteItem("item123") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertFalse(successResult)
        assertEquals("Failed to delete item", messageResult)
        verify(repo).deleteItem(eq("item123"), any())
    }
}