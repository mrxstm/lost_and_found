package com.example.lost_and_found.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.lost_and_found.R
import com.example.lost_and_found.repository.UserRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.viewmodel.UserViewModel

class ForgetPassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(this, R.color.blackshade)
            ),
            navigationBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(this, R.color.blackshade)
            )
        )
        setContent {
            ForgetPasswordBody()
        }
    }
}

@Composable
fun ForgetPasswordBody() {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var emailSent by remember { mutableStateOf(false) }

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold { padding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = colorResource(R.color.blackshade))
                .padding(top = 50.dp)
        ) {
            item {

                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { activity.finish() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Logo
                Image(
                    painter = painterResource(R.drawable.lostandfoundlogo),
                    modifier = Modifier.size(100.dp),
                    contentDescription = null
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    "Password Reset",
                    fontSize = 28.sp,
                    fontFamily = Ruluko,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.greenshade)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Enter your email and we'll send\nyou a reset link",
                    fontSize = 13.sp,
                    fontFamily = Ruluko,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(40.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        "Email",
                        fontSize = 16.sp,
                        fontFamily = Ruluko,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text("abc@gmail.com", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1F2937),
                            unfocusedContainerColor = Color(0xFF1F2937),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = colorResource(R.color.greenshade),
                            unfocusedIndicatorColor = Color(0xFF374151),
                            cursorColor = colorResource(R.color.greenshade),
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth()
                    )

                    Spacer(Modifier.height(24.dp))

                    if (!emailSent) {
                        Button(
                            onClick = {
                                when {
                                    email.isEmpty() -> {
                                        Toast.makeText(
                                            context,
                                            "Please enter your email",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                                        .matches() -> {
                                        Toast.makeText(
                                            context,
                                            "Please enter a valid email",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else -> {
                                        isLoading = true
                                        userViewModel.forgetPassword(email) { success, message ->
                                            isLoading = false
                                            if (success) {
                                                emailSent = true
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.greenshade),
                                contentColor = Color.Black
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Send Reset Link",
                                    fontFamily = Ruluko,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_check_circle_24),
                                contentDescription = null,
                                tint = colorResource(R.color.greenshade),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "Reset link sent!",
                                color = colorResource(R.color.greenshade),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFamily = Ruluko
                            )
                            Text(
                                "Check your inbox at\n$email",
                                color = Color(0xFF9CA3AF),
                                fontSize = 13.sp,
                                fontFamily = Ruluko,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(8.dp))

                            // ✅ Back to login button
                            Button(
                                onClick = { activity.finish() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.greenshade),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    "Back to Login",
                                    fontFamily = Ruluko,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgetPasswordPreview() {
    ForgetPasswordBody()
}