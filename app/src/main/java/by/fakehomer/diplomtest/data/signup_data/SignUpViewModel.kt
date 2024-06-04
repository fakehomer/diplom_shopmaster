package by.fakehomer.diplomtest.data.signup_data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import by.fakehomer.diplomtest.data.rules.Validator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpViewModel : ViewModel() {

    private val TAG = SignUpViewModel::class.simpleName
    private val userRepository = UserRepository()

    var registrationUIState = mutableStateOf(RegistrationUIState())
    var allValidationsPassed = mutableStateOf(false)
    var signUpInProgress = mutableStateOf(false)
    var emailVerificationSent = mutableStateOf(false)

    fun onEvent(event: SignUpUIEvent) {
        validateDataWithRules()
        when (event) {
            is SignUpUIEvent.FirstNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    firstName = event.firstName
                )
                validateDataWithRules()
                printState()
            }
            is SignUpUIEvent.LastNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    lastName = event.lastName
                )
                validateDataWithRules()
                printState()
            }
            is SignUpUIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )
                validateDataWithRules()
                printState()
            }
            is SignUpUIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
                validateDataWithRules()
                printState()
            }
            is SignUpUIEvent.RegisterButtonClicked -> {
                signUp()
            }

            else -> {}
        }
        validateDataWithRules()
    }

    private fun signUp() {
        Log.d(TAG, "Inside_signUp")
        printState()
        createUserInFirebase(
            email = registrationUIState.value.email,
            password = registrationUIState.value.password
        )
    }

    private fun validateDataWithRules() {
        val fNameResult = Validator.validateFirstName(fName = registrationUIState.value.firstName)
        val lNameResult = Validator.validateLastName(lName = registrationUIState.value.lastName)
        val emailResult = Validator.validateEmail(email = registrationUIState.value.email)
        val passwordResult = Validator.validatePassword(password = registrationUIState.value.password)

        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "fNameResult= $fNameResult")
        Log.d(TAG, "lNameResult= $lNameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
        )

        allValidationsPassed.value = fNameResult.status && lNameResult.status && emailResult.status && passwordResult.status
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, registrationUIState.value.toString())
    }

    private fun createUserInFirebase(email: String, password: String) {
        signUpInProgress.value = true

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d(TAG, "Inside_OnCompleteListener")
                signUpInProgress.value = false
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let {
                        userRepository.storeUserInfo(
                            user = it,
                            firstName = registrationUIState.value.firstName,
                            lastName = registrationUIState.value.lastName,
                            email = registrationUIState.value.email,
                            onSuccess = {
                                sendEmailVerification(it)
                            },
                            onFailure = { exception ->
                                Log.d(TAG, "Failed to store user info: ${exception.message}")
                            }
                        )
                    }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Inside_OnFailureListener")
                Log.d(TAG, "Exception= {${it.message}}")
                Log.d(TAG, "Exception= {${it.localizedMessage}}")
            }
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emailVerificationSent.value = true
                    Log.d(TAG, "Verification email sent to ${user.email}")
                } else {
                    Log.d(TAG, "Failed to send verification email.")
                }
            }
    }
}
