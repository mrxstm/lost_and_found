package com.example.lost_and_found.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.lost_and_found.ui.theme.Ruluko
import com.example.lost_and_found.ui.theme.Sniglet
import com.example.lost_and_found.utils.ImageUtils
import com.example.lost_and_found.view.ui.theme.Lost_and_foundTheme
import com.google.firebase.auth.FirebaseAuth

class Dashboard : ComponentActivity() {

    private lateinit var imageUtils: ImageUtils
    private var onImageSelected: ((Uri?) -> Unit)? = null

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

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            onImageSelected?.invoke(uri)
        }

        setContent {
            Lost_and_foundTheme {
                DashboardBody(
                    onPickImage = { callback ->
                        onImageSelected = callback
                        imageUtils.launchImagePicker()
                    },
                    onTakePhoto = { callback ->
                        onImageSelected = callback
                        imageUtils.launchCamera()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody(
    onPickImage: (callback: (Uri?) -> Unit) -> Unit = {},
    onTakePhoto: (callback: (Uri?) -> Unit) -> Unit = {}
) {
    data class NavItem(val label: String, val icon: Int)

    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(0) }

    var showMenu by remember { mutableStateOf(false) }

    val listItems = listOf(
        NavItem("Home", R.drawable.home),
        NavItem("Search", R.drawable.search),
        NavItem("Report", R.drawable.plus),
        NavItem("Profile", R.drawable.account),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.White,
                    titleContentColor = Color.Black,
                    containerColor = colorResource(R.color.blackshade)
                ),
                navigationIcon = {
                    Row {
                        Image(
                            painter = painterResource(R.drawable.lostandfoundlogo),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(40.dp))
                    }
                },
                title = {
                    Text(
                        "Lost&Found",
                        fontFamily = Sniglet,
                        color = colorResource(R.color.greenshade)
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                painter = painterResource(R.drawable.outline_menu_24),
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            containerColor = Color(0xFF1F2937)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Logout",
                                        color = Color(0xFFEF4444),
                                        fontFamily = Ruluko,
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_logout_24),
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(context, LoginScreen::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = Color(0xFFEF4444)
                                )
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = colorResource(R.color.blackshade),
                tonalElevation = 0.dp
            ) {
                listItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                            )
                        },
                        label = { Text(item.label) },
                        onClick = { selectedIndex = index },
                        selected = selectedIndex == index,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colorResource(R.color.greenshade),
                            selectedTextColor = colorResource(R.color.greenshade),
                            unselectedIconColor = colorResource(R.color.navbuttoncolor),
                            unselectedTextColor = colorResource(R.color.navbuttoncolor),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> SearchScreen()
                2 -> ReportScreen(
                    onPickImage = onPickImage,
                    onTakePhoto = onTakePhoto
                )
                3 -> ProfileScreen()
                else -> HomeScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardBodyPreview() {
    DashboardBody()
}