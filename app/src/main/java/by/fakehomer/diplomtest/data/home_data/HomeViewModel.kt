package by.fakehomer.diplomtest.data.home_data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel : ViewModel() {

    val isUserLoggedIn: MutableLiveData<Boolean> = MutableLiveData()

    private val _navigationItems = MutableLiveData(
        listOf(
            NavigationItem("Желания", Icons.Filled.Favorite),
            NavigationItem("План покупок", Icons.Filled.ShoppingCart),
            NavigationItem("Профиль", Icons.Filled.Person)
        )
    )
    val navigationItems: LiveData<List<NavigationItem>> = _navigationItems

    init {
        checkForActiveSession()
    }

    fun checkForActiveSession() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            Log.d(TAG, "Valid session")
            isUserLoggedIn.value = true
        } else {
            Log.d(TAG, "User is not logged in")
            isUserLoggedIn.value = false
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector)
