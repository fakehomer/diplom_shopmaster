package by.fakehomer.diplomtest.screens

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import by.fakehomer.diplomtest.R
import by.fakehomer.diplomtest.navigation.Screen
import by.fakehomer.diplomtest.navigation.ShoppingPlannerAppRouter
import by.fakehomer.diplomtest.navigation.SystemBackButtonHandler

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen() {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Настройки", color = Color.White) },
                backgroundColor = Color(0xFFF68B2C)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            SettingsOption(
                icon = R.drawable.setting,
                text = "Сменить пароль",
                onClick = { showChangePasswordDialog = true }
            )
            Divider(color = Color.Gray, thickness = 1.dp)
            SettingsOption(
                icon = R.drawable.share,
                text = "Удалить аккаунт",
                onClick = { showDeleteAccountDialog = true }
            )
            Divider(color = Color.Gray, thickness = 1.dp)
        }

        if (showDeleteAccountDialog) {
            DeleteAccountDialog(
                onDismiss = { showDeleteAccountDialog = false }
            )
        }

        if (showChangePasswordDialog) {
            ChangePasswordDialog(
                onDismiss = { showChangePasswordDialog = false }
            )
        }

        SystemBackButtonHandler {
            ShoppingPlannerAppRouter.navigateTo(Screen.HomeScreen)
        }
    }
}

@Composable
fun SettingsOption(icon: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            tint = Color.Gray,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.arrow),
            contentDescription = "Arrow",
            tint = Color.Gray,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
        )
    }
}

@Composable
fun DeleteAccountDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(TextFieldValue()) }
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userEmail = firebaseUser?.email ?: ""
    var isButtonEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(email.text) {
        isButtonEnabled = email.text == userEmail
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить аккаунт") },
        text = {
            Column {
                Text("Вы уверены, что хотите удалить аккаунт?")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Введите email вашего аккаунта для подтвеждения") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ),
                        onClick = {
                    if (email.text == userEmail) {
                        firebaseUser?.delete()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User account deleted.")
                                    ShoppingPlannerAppRouter.navigateTo(Screen.SignUpScreen)
                                } else {
                                    Log.d(TAG, "Failed to delete user account.", task.exception)
                                }
                            }
                        onDismiss()
                    } else {
                        Toast.makeText(
                            context, "Email does not match", Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = true
            ) {
                Text("Удалить")
            }
        },
        dismissButton = {
            Button(colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF888888),
                contentColor = Color.White
            ), onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    var currentPassword by remember { mutableStateOf(TextFieldValue()) }
    var newPassword by remember { mutableStateOf(TextFieldValue()) }
    var repeatNewPassword by remember { mutableStateOf(TextFieldValue()) }
    var step by remember { mutableStateOf(1) }
    var isButtonEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(step, currentPassword.text, newPassword.text, repeatNewPassword.text) {
        isButtonEnabled = when (step) {
            1 -> currentPassword.text.isNotEmpty()
            2 -> newPassword.text.isNotEmpty() && newPassword.text == repeatNewPassword.text
            else -> false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Сменить пароль") },
        text = {
            Column {
                when (step) {
                    1 -> {
                        Text("Введите свой текущий пароль")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Текущий пароль") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        Text("Введите ваш новый пароль")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Новый пароль") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Повторите свой новый пароль")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = repeatNewPassword,
                            onValueChange = { repeatNewPassword = it },
                            label = { Text("Повторите свой новый пароль") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ),
                onClick = {
                    if (step == 1) {
                        val credential = EmailAuthProvider.getCredential(
                            firebaseUser?.email ?: "", currentPassword.text
                        )
                        firebaseUser?.reauthenticate(credential)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "Reauthentication successful")
                                    step = 2
                                } else {
                                    Log.d(TAG, "Reauthentication failed: ${task.exception?.message}")
                                    Toast.makeText(
                                        context, "Вы ввели неверный пароль", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else if (step == 2) {
                        firebaseUser?.updatePassword(newPassword.text)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context, "Пароль успешно изменен", Toast.LENGTH_SHORT
                                    ).show()
                                    onDismiss()
                                } else {
                                    Toast.makeText(
                                        context, "Ошибка смены пароля", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                },
                enabled = isButtonEnabled
            ) {
                Text("Далее")
            }
        },
        dismissButton = {
            Button(colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF888888),
                contentColor = Color.White
            ), onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewToast() {
    val context = LocalContext.current
    Toast.makeText(
        context, "Ошибка смены пароля", Toast.LENGTH_SHORT
    ).show()
}