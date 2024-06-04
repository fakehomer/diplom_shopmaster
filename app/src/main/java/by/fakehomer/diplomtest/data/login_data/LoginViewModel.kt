package by.fakehomer.diplomtest.data.login_data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import by.fakehomer.diplomtest.data.rules.Validator
import by.fakehomer.diplomtest.navigation.Screen
import by.fakehomer.diplomtest.navigation.ShoppingPlannerAppRouter
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val TAG = LoginViewModel::class.simpleName

    var loginUIState = mutableStateOf(LoginUIState())

    var allValidationsPassed = mutableStateOf(false)

    var loginInProgress = mutableStateOf(false)

    fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email,
                    loginErrorMessage = null
                )
            }

            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password,
                    loginErrorMessage = null
                )
            }

            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }
        }
        validateLoginUIDataWithRules()
    }

    private fun validateLoginUIDataWithRules() {
        val emailResult = Validator.validateEmail(
            email = loginUIState.value.email
        )

        val passwordResult = Validator.validatePassword(
            password = loginUIState.value.password
        )

        loginUIState.value = loginUIState.value.copy(
            emailError = !emailResult.status,
            passwordError = !passwordResult.status
        )

        allValidationsPassed.value = emailResult.status && passwordResult.status
    }

    private fun login() {
        loginInProgress.value = true
        val email = loginUIState.value.email
        val password = loginUIState.value.password

        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.d(TAG,"Inside_login_success")
                Log.d(TAG,"${it.isSuccessful}")

                if(it.isSuccessful){
                    loginInProgress.value = false
                    ShoppingPlannerAppRouter.navigateTo(Screen.HomeScreen)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_login_failure")
                Log.d(TAG,"${it.localizedMessage}")

                loginInProgress.value = false
                loginUIState.value = loginUIState.value.copy(
                    loginErrorMessage = "Введены неверная почта или пароль"
                )
            }
    }

    fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()

        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "Inside sign out success")
                ShoppingPlannerAppRouter.navigateTo(Screen.LoginScreen)
            } else {
                Log.d(TAG, "Inside sign out is not complete")
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            // Handle empty email case (show error or notification)
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset email sent.")
                } else {
                    Log.d(TAG, "Error sending password reset email: ${task.exception?.localizedMessage}")
                }
            }
    }
}
