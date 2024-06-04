package by.fakehomer.diplomtest.data.signup_data;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel : ViewModel() {

    private val _emailVerified = MutableStateFlow(false)
    val emailVerified: StateFlow<Boolean> = _emailVerified

    init {
        checkEmailVerified()
    }

    private fun checkEmailVerified() {
        viewModelScope.launch {
            while (true) {
                val user = FirebaseAuth.getInstance().currentUser
                user?.reload()
                if (user?.isEmailVerified == true) {
                    _emailVerified.value = true
                    break
                }
                delay(5000)
            }
        }
    }
}
