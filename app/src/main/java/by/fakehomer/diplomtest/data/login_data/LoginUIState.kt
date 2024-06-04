package by.fakehomer.diplomtest.data.login_data

data class LoginUIState(
    val email: String = "",
    val password: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val loginErrorMessage: String? = null
)
