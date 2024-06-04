
package by.fakehomer.diplomtest.data.signup_data

data class RegistrationUIState(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var termsOfUsesAccepted: Boolean = false,

    var firstNameError: Boolean = false,
    var lastNameError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,
    var termsOfUsesError: Boolean = false
)
