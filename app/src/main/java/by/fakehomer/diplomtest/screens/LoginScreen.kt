package by.fakehomer.diplomtest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.fakehomer.diplomtest.R
import by.fakehomer.diplomtest.components.*
import by.fakehomer.diplomtest.data.login_data.LoginUIEvent
import by.fakehomer.diplomtest.data.login_data.LoginViewModel
import by.fakehomer.diplomtest.navigation.Screen
import by.fakehomer.diplomtest.navigation.ShoppingPlannerAppRouter
import by.fakehomer.diplomtest.navigation.SystemBackButtonHandler

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HeadingTextComponent(value = stringResource(id = R.string.ask_for_login))
                Spacer(modifier = Modifier.height(20.dp))
                MyTextField(
                    stringResource(id = R.string.email),
                    painterResource(id = R.drawable.email),
                    onTextSelected = {
                        email = it
                        loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.value.emailError
                )
                PasswordTextField(
                    stringResource(id = R.string.password),
                    painterResource(id = R.drawable.password),
                    onTextSelected = {
                        loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.value.passwordError
                )
                Spacer(modifier = Modifier.height(15.dp))
                loginViewModel.loginUIState.value.loginErrorMessage?.let { errorMessage ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                UnderLinedNormalTextComponent(
                    value = stringResource(id = R.string.forgot_password),
                    onClick = { showForgotPasswordDialog = true }
                )
                ButtonComponent(
                    value = stringResource(id = R.string.login),
                    onButtonClicked = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnabled = loginViewModel.allValidationsPassed.value
                )
                DividerTextComponent()
                ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                    ShoppingPlannerAppRouter.navigateTo(Screen.SignUpScreen)
                })
            }
        }
        if (loginViewModel.loginInProgress.value) {
            CircularProgressIndicator()
        }
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            email = email,
            onDismiss = { showForgotPasswordDialog = false },
            onConfirm = {
                showForgotPasswordDialog = false
                loginViewModel.sendPasswordResetEmail(email)
            }
        )
    }

    SystemBackButtonHandler {
        ShoppingPlannerAppRouter.navigateTo(Screen.SignUpScreen)
    }
}

@Composable
fun ForgotPasswordDialog(email: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Забыли пароль") },
        text = {
            Column {
                Text(text = "Хотите сбросить ваш пароль?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Мы отправим письмо для сброса и смены пароля на ваш email: $email")
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Да")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Нет")
            }
        }
    )
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
