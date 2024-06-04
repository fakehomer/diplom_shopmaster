package by.fakehomer.diplomtest.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen(){
    object SignUpScreen : Screen()
    object LoginScreen : Screen()
    object HomeScreen : Screen()
    object ProfileScreen : Screen()
    object GroceryPlannerScreen : Screen()
    object WishlistScreen : Screen()
    object SettingsScreen : Screen()
    object EmailVerificationScreen : Screen()
    data class WishlistItemsScreen(val wishlistId: String) : Screen()
}

object ShoppingPlannerAppRouter{
    val currentScreen : MutableState<Screen> = mutableStateOf(Screen.SignUpScreen)
    fun navigateTo(destination : Screen){
        currentScreen.value = destination
    }
}


