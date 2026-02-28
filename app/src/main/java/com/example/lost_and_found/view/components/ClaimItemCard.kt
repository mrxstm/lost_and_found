package com.example.lost_and_found.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lost_and_found.R
import com.example.lost_and_found.model.ClaimModel
import com.example.lost_and_found.ui.theme.Ruluko

@Composable
fun ClaimItemCard(
    claim: ClaimModel,
    onApprove: (() -> Unit)? = null,   // null = claimer view (no approve/reject)
    onReject: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1F2937))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Claimant info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (claim.claimantPhotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = claim.claimantPhotoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF374151)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.account),
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column {
                Text(
                    claim.claimantName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Ruluko
                )
                val statusColor = when (claim.status) {
                    "approved" -> Color(0xFF22C55E)
                    "rejected" -> Color(0xFFEF4444)
                    else -> Color(0xFFEAB308)
                }
                Text(
                    claim.status.replaceFirstChar { it.uppercase() },
                    color = statusColor,
                    fontSize = 11.sp,
                    fontFamily = Ruluko
                )
            }
        }

        // Message
        Text(
            claim.message,
            color = Color(0xFF9CA3AF),
            fontSize = 13.sp,
            fontFamily = Ruluko,
            lineHeight = 20.sp
        )

        // Proof image
        if (claim.proofImageUrl.isNotEmpty()) {
            AsyncImage(
                model = claim.proofImageUrl,
                contentDescription = "Proof",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        // Approve/Reject — only if owner passed the callbacks and claim is pending
        if (claim.status == "pending" && onApprove != null && onReject != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, Color(0xFFEF4444)
                    )
                ) {
                    Text("Reject", fontSize = 13.sp, fontFamily = Ruluko)
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.greenshade),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Approve", fontSize = 13.sp, fontFamily = Ruluko)
                }
            }
        }
    }
}