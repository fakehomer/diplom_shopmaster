package by.fakehomer.diplomtest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.fakehomer.diplomtest.data.home_data.HomeViewModel

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {

    val items by homeViewModel.navigationItems.observeAsState(emptyList())
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.White,
                contentColor = Color.Black
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = {
                            Text(
                                text = item.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 10.sp
                            )
                        },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (items[selectedItem]) {
                items[0] -> WishlistScreen()
                items[1] -> GroceryPlannerScreen()
                items[2] -> ProfileScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen()
    }
}
