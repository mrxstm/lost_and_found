package com.example.lost_and_found.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.lost_and_found.R
import com.example.lost_and_found.repository.UserRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.view.ui.theme.Lost_and_foundTheme
import com.example.lost_and_found.viewmodel.UserViewModel

class ForgetPassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this,R.color.blackshade)),
            navigationBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this, R.color.blackshade))
        )
        setContent {
            ForgetPasswordBody()
        }
    }
}

@Composable
fun ForgetPasswordBody() {

    var email by remember { mutableStateOf("") }
    var userViewModel = remember { UserViewModel(UserRepoImpl()) }
    var context = LocalContext.current;

    Scaffold { padding->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = colorResource(R.color.blackshade))
        ) {
            item {

                    Image(
                        painter = painterResource(R.drawable.lostandfoundlogo),
                        modifier = Modifier
                            .size(150.dp)
                            .padding(top = 40.dp),
                        contentDescription = null
                    )

                Spacer(Modifier.height(20.dp))
                    Text(
                        "Password Reset",
                        fontSize = 25.sp,
                        fontFamily = Ruluko,
                        color = colorResource(R.color.greenshade,

                        ))


                // email field

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 35.dp)
                ) {
                    Text(
                        "Email",
                        fontSize = 20.sp,
                        fontFamily = Ruluko,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {email = it},
                        placeholder = {
                            Text("abc@gmail.com", fontSize = 12.sp)
                        },
                        colors = TextFieldDefaults.colors(

                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                            .padding(end = 12.dp)
                    )
                }

                Spacer(Modifier.height(40.dp))

                // Button
                    Button(
                        onClick = {
                            userViewModel.forgetPassword(email) { success, message ->
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Password reset link sent to $email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to send password reset link",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.greenshade)
                        )
                    ) {
                        Text("Send reset link", color = Color.Black)
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