package by.fakehomer.diplomtest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.fakehomer.diplomtest.components.NormalTextComponent
import by.fakehomer.diplomtest.R
import by.fakehomer.diplomtest.components.ButtonComponent
import by.fakehomer.diplomtest.components.ClickableLoginTextComponent
import by.fakehomer.diplomtest.components.DividerTextComponent
import by.fakehomer.diplomtest.components.HeadingTextComponent
import by.fakehomer.diplomtest.components.MyTextField
import by.fakehomer.diplomtest.components.PasswordTextField2
import by.fakehomer.diplomtest.data.signup_data.SignUpViewModel
import by.fakehomer.diplomtest.data.signup_data.SignUpUIEvent
import by.fakehomer.diplomtest.navigation.Screen
import by.fakehomer.diplomtest.navigation.ShoppingPlannerAppRouter

@Composable
fun SignUpScreen(signUpViewModel: SignUpViewModel = viewModel()) {
    LaunchedEffect(signUpViewModel.emailVerificationSent.value) {
        if (signUpViewModel.emailVerificationSent.value) {
            ShoppingPlannerAppRouter.navigateTo(Screen.EmailVerificationScreen)
        }
    }

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
                NormalTextComponent(value = stringResource(id = R.string.hello))
                HeadingTextComponent(value = stringResource(id = R.string.create_account))
                Spacer(modifier = Modifier.height(20.dp))

                MyTextField(
                    stringResource(id = R.string.first_name),
                    painterResource(id = R.drawable.person96),
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.FirstNameChanged(it))
                    },
                    signUpViewModel.registrationUIState.value.firstNameError
                )
                MyTextField(
                    stringResource(id = R.string.second_name),
                    painterResource(id = R.drawable.person96),
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.LastNameChanged(it))
                    },
                    signUpViewModel.registrationUIState.value.lastNameError
                )
                MyTextField(
                    stringResource(id = R.string.email),
                    painterResource(id = R.drawable.email),
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.EmailChanged(it))
                    },
                    signUpViewModel.registrationUIState.value.emailError
                )
                PasswordTextField2(
                    stringResource(id = R.string.password),
                    painterResource(id = R.drawable.password),
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.PasswordChanged(it))
                    },
                    errorStatus = signUpViewModel.registrationUIState.value.passwordError
                )


                Spacer(modifier = Modifier.height(200.dp))
                ButtonComponent(
                    value = stringResource(id = R.string.register),
                    onButtonClicked = {
                            signUpViewModel.onEvent(SignUpUIEvent.RegisterButtonClicked)
                    },
                    isEnabled = signUpViewModel.allValidationsPassed.value
                )
                Spacer(modifier = Modifier.height(10.dp))
                DividerTextComponent()
                ClickableLoginTextComponent(onTextSelected = {
                    ShoppingPlannerAppRouter.navigateTo(Screen.LoginScreen)
                })
            }
        }

        if (signUpViewModel.signUpInProgress.value) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
fun DefaultPreviewOfSignUpScreen() {
    SignUpScreen()
}
