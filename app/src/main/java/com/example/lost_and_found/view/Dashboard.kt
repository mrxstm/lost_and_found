package com.example.lost_and_found.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lost_and_found.R
import com.example.lost_and_found.ui.theme.Sniglet
import com.example.lost_and_found.view.ui.theme.Lost_and_foundTheme

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lost_and_foundTheme {
              DashboardBody()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {

    data class NavItem(val label: String, val icon:Int)

    var selectedIndex by remember { mutableStateOf(0) }

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
                title = {Text("Lost&Found", fontFamily = Sniglet, color = colorResource(R.color.greenshade))},
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_notifications_24),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.message),
                            contentDescription = null
                        )
                    }
                }

            )

        },
        bottomBar = {
            NavigationBar(
                containerColor = colorResource(R.color.blackshade),
                tonalElevation = 0.dp
            ) {
                listItems.forEachIndexed{ index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(item.label)
                        },
                        onClick = {
                            selectedIndex = index
                        },
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
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when(selectedIndex) {
                0 -> HomeScreen()
                1 -> SearchScreen()
                2 -> ReportScreen()
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