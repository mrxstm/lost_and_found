package com.example.lost_and_found.view.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.lost_and_found.R
import com.example.lost_and_found.ui.theme.Ruluko

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1F2937),
        title = {
            Text(
                text = title,
                color = Color.White,
                fontFamily = Ruluko
            )
        },
        text = {
            Text(
                text = message,
                color = Color(0xFF9CA3AF),
                fontFamily = Ruluko
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) Color(0xFFEF4444)
                    else colorResource(R.color.greenshade),
                    contentColor = if (isDestructive) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(confirmText, fontFamily = Ruluko)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = Color(0xFF9CA3AF), fontFamily = Ruluko)
            }
        }
    )
}