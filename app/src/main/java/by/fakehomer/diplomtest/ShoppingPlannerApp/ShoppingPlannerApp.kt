package by.fakehomer.diplomtest.ShoppingPlannerApp



import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import by.fakehomer.diplomtest.navigation.Screen
import by.fakehomer.diplomtest.navigation.ShoppingPlannerAppRouter
import by.fakehomer.diplomtest.screens.EmailVerificationScreen
import by.fakehomer.diplomtest.screens.GroceryPlannerScreen
import by.fakehomer.diplomtest.screens.LoginScreen
import by.fakehomer.diplomtest.screens.ProfileScreen
import by.fakehomer.diplomtest.screens.SignUpScreen
import by.fakehomer.diplomtest.screens.WishlistItemsScreen
import by.fakehomer.diplomtest.screens.WishlistScreen
import by.fakehomer.diplomtest.screens.SettingsScreen
import by.fakehomer.diplomtest.screens.HomeScreen
import by.fakehomer.diplomtest.data.home_data.HomeViewModel


@Composable
fun ShoppingPlannerApp(homeViewModel: HomeViewModel = viewModel()){

    homeViewModel.checkForActiveSession()

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ){

        if(homeViewModel.isUserLoggedIn.value == true){
            ShoppingPlannerAppRouter.navigateTo(Screen.HomeScreen)
        }

        Crossfade(targetState = ShoppingPlannerAppRouter.currentScreen, label = "") { currentState ->
            when(currentState.value){
                is Screen.SignUpScreen ->{
                    SignUpScreen()
                }
                is Screen.LoginScreen -> {
                    LoginScreen()
                }
                is Screen.HomeScreen -> {
                    HomeScreen()
                }
                is Screen.GroceryPlannerScreen -> {
                    GroceryPlannerScreen()
                }
                is Screen.ProfileScreen -> {
                    ProfileScreen()
                }
                is Screen.WishlistScreen -> {
                    WishlistScreen()
                }
                is Screen.SettingsScreen -> {
                    SettingsScreen()
                }
                is Screen.EmailVerificationScreen -> {
                    EmailVerificationScreen()
                }
                is Screen.WishlistItemsScreen -> {
                    val wishlistId = (currentState.value as Screen.WishlistItemsScreen).wishlistId
                    WishlistItemsScreen(wishlistId = wishlistId)
                }
            }
        }
    }
}