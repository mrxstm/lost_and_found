package com.example.lost_and_found.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.lost_and_found.R
import com.example.lost_and_found.repository.UserRepoImpl
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.ui.theme.Sniglet
import com.example.lost_and_found.viewmodel.UserViewModel

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this,R.color.blackshade)),
            navigationBarStyle = SystemBarStyle.dark(ContextCompat.getColor(this, R.color.blackshade))
        )
        setContent {
               LoginBody()

        }
    }
}

@Composable
fun LoginBody() {

    var userViewModel = remember { UserViewModel(UserRepoImpl()) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}
    var checked by remember { mutableStateOf(false) }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current;
    val activity = context as Activity;
    Scaffold { padding ->
        LazyColumn (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .background(color = colorResource(R.color.blackshade))
                .fillMaxSize()
                .padding(top = 50.dp)
        ) {
            item {
                Image(
                    painter = painterResource(R.drawable.lostandfoundlogo),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.height(25.dp))
                Text(
                    "Login",
                    fontFamily = Ruluko,
                    fontSize = 30.sp,
                    color = colorResource(R.color.greenshade)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 35.dp)
                ) {

                    //Email
                    Text(
                        "Email",
                        fontSize = 18.sp,
                        fontFamily = Ruluko,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(12.dp))

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

                    Spacer(Modifier.height(20.dp))

                    //Password

                    Text(
                        "Password",
                        fontSize = 18.sp,
                        fontFamily = Ruluko,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {password = it},
                        placeholder = {
                            Text("******")
                        },
                        colors = TextFieldDefaults.colors(

                        ),
                        visualTransformation = if(visibility) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                visibility = !visibility
                            }) {
                                Icon(
                                    painter = if(visibility)
                                        painterResource(R.drawable.baseline_visibility_24)
                                    else painterResource(R.drawable.baseline_visibility_off_24),
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                            .padding(end = 12.dp)
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "remember me",
                                color = Color.White,
                                fontFamily = Ruluko
                            )
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {checked = it},
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colorResource(R.color.greenshade),
                                    uncheckedColor = Color.White
                                )

                            )
                        }
                        Spacer(Modifier.width(60.dp))
                        Text(
                            "forgot password?",
                            fontFamily = Ruluko,
                            color = colorResource(R.color.blueshade),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable(onClick = {
                                    val intent = Intent(context, ForgetPassword::class.java)
                                    activity.startActivity(intent)
                                })
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            userViewModel.login(email, password) {
                                success, message ->
                                if(success) {
                                    val intent = Intent(context, Dashboard::class.java)
                                    context.startActivity(intent)

                                    activity.finish()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.greenshade),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Text("Login")
                    }

                    Spacer(Modifier.height(30.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "or sign in with",
                            color = colorResource(R.color.greyshade),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.google),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Google", color = Color.Black)

                        }

                    }
                    Spacer(Modifier.height(30.dp))

                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Don't have an account ?", color = colorResource(R.color.greyshade))
                        Text(
                            " Sign up",
                            modifier = Modifier.clickable(onClick = {
                                val intent = Intent(context, SignupScreen::class.java)
                                context.startActivity(intent)
                            }),
                            color = colorResource(R.color.blueshade)
                        )
                    }



                }
            }
            
        }

    }

}

@Preview(showBackground = true)
@Composable
fun LoginBodyPreview() {
    LoginBody()
}