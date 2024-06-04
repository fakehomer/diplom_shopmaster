package by.fakehomer.diplomtest.data.rules

import androidx.compose.ui.res.stringResource
import by.fakehomer.diplomtest.R

object Validator {

    fun validateFirstName(fName: String): ValidationResult {
        return ValidationResult(
            (!fName.isNullOrEmpty() && fName.length <= 20)
        )
    }

    fun validateLastName(lName: String): ValidationResult {
        return ValidationResult(
            (!lName.isNullOrEmpty() && lName.length <= 20)
        )
    }

    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            (!email.isNullOrEmpty())
        )
    }

    fun validatePassword(password: String): ValidationResult {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasMinimumLength = password.length >= 8

        val isValid = hasUpperCase && hasLowerCase && hasDigit && hasMinimumLength

        return ValidationResult(isValid)
    }

}

data class ValidationResult(
    val status: Boolean = false
)

