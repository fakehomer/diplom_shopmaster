package by.fakehomer.diplomtest.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.fakehomer.diplomtest.R
import by.fakehomer.diplomtest.ui.theme.GrayColor
import by.fakehomer.diplomtest.ui.theme.Primary
import by.fakehomer.diplomtest.ui.theme.TextColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import by.fakehomer.diplomtest.screens.ProfileScreen
import by.fakehomer.diplomtest.ui.theme.Secondary
import java.util.Calendar

@Composable
fun IntroTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth(),
        //.heightIn(min = 80.dp),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Thin,
            fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )
}

@Composable
fun NormalTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(labelValue: String,
                painterResource: Painter,
                onTextSelected: (String) -> Unit,
                errorStatus: Boolean = false){
    val textValue = remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "")
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            cursorColor = Primary,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        maxLines = 1,
        value = textValue.value,
        onValueChange = {
            textValue.value = it
            onTextSelected(it)
        },
        isError = !errorStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(labelValue: String, painterResource: Painter, onTextSelected: (String) -> Unit, errorStatus: Boolean = false){
    val localFocusManager = LocalFocusManager.current
    val password = remember { mutableStateOf("") }

    val passwordVisible = remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "")
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            cursorColor = Primary,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        singleLine = true,
        maxLines = 1,
        keyboardActions = KeyboardActions{
            localFocusManager.clearFocus()
        },
        value = password.value,
        onValueChange = {
            password.value = it
            onTextSelected(it)
        },
        trailingIcon = {
            val iconImage = if(passwordVisible.value){
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }
            val description = if(passwordVisible.value){
                stringResource(id = R.string.hide_password)
            } else {
                stringResource(id = R.string.show_password)
            }
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        isError = !errorStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField2(
    labelValue: String,
    painterResource: Painter,
    onTextSelected: (String) -> Unit,
    errorStatus: Boolean = false
) {
    val localFocusManager = LocalFocusManager.current
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = labelValue) },
            leadingIcon = { Icon(painter = painterResource, contentDescription = "") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary,
                focusedLabelColor = Primary,
                cursorColor = Primary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            singleLine = true,
            maxLines = 1,
            keyboardActions = KeyboardActions { localFocusManager.clearFocus() },
            value = password.value,
            onValueChange = {
                password.value = it
                onTextSelected(it)
            },
            trailingIcon = {
                val iconImage = if (passwordVisible.value) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                val description = if (passwordVisible.value) {
                    stringResource(id = R.string.hide_password)
                } else {
                    stringResource(id = R.string.show_password)
                }
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = iconImage, contentDescription = description)
                }
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            isError = !errorStatus
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = getPasswordStrengthMessage(password.value))
    }
}

@Composable
fun getPasswordStrengthMessage(password: String): String {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasMinimumLength = password.length >= 8

    return when {
        !hasMinimumLength -> stringResource(id = R.string.password_too_short)
        !hasUpperCase -> stringResource(id = R.string.password_needs_uppercase)
        !hasLowerCase -> stringResource(id = R.string.password_needs_lowercase)
        !hasDigit -> stringResource(id = R.string.password_needs_digit)
        else -> stringResource(id = R.string.password_strong)
    }
}

@Composable
fun CheckboxComponent(value: String, onTextSelected: (String) -> Unit, onCheckedChange: (Boolean) -> Unit){
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(66.dp),
        verticalAlignment = Alignment.CenterVertically){
        val checkedState = remember{
            mutableStateOf(false)
        }
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = !checkedState.value
                onCheckedChange.invoke(it)
            })
        ClickableTextComponent(value = value, onTextSelected)
    }
}


@Composable
fun ClickableTextComponent(value: String, onTextSelected: (String) -> Unit){
    val initialText = "Продолжая вы подтверждаете, что ознакомились с"
    val termsOfUse = " Правилами использования"
    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = Primary)){
            pushStringAnnotation(tag = termsOfUse, annotation = termsOfUse)
            append(termsOfUse)
        }
    }
    ClickableText(text = annotatedString, onClick = {
        offset -> annotatedString.getStringAnnotations(offset, offset)
            .firstOrNull()?.also{ span ->
            Log.d("ClickableTextComponent", "{$span}")
            if(span.item == termsOfUse){
                onTextSelected(span.item)
            }
    }
    })
}

@Composable
fun ButtonComponent(value: String, onButtonClicked: () -> Unit, isEnabled: Boolean = false){
    Button(
        onClick = {
            onButtonClicked.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        enabled = isEnabled,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(
                brush = Brush.horizontalGradient(listOf(Secondary, Primary)),
            ),
            contentAlignment = Alignment.Center
            ){
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DividerTextComponent(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = GrayColor,
            thickness = 1.dp
            )
        Text(
            modifier = Modifier.padding(8.dp),
            text = "или",
            fontSize = 18.sp,
            color = TextColor
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = GrayColor,
            thickness = 1.dp
        )
    }
}

@Composable
fun ClickableLoginTextComponent(tryingToLogin: Boolean = true, onTextSelected: (String) -> Unit){
    val initialText = if(tryingToLogin) "Уже есть аккаунт?" else ""
    val loginText = if(tryingToLogin) " Войти" else "Регистрация"
    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = Primary)){
            pushStringAnnotation(tag = loginText, annotation = loginText)
            append(loginText)
        }
    }
    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 21.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center
        ),
        text = annotatedString, onClick = {
            offset -> annotatedString.getStringAnnotations(offset, offset)
        .firstOrNull()?.also{ span ->
            Log.d("ClickableTextComponent", "{$span}")
            if(span.item == loginText){
                onTextSelected(span.item)
            }
        }
    })
}

@Composable
fun UnderLinedNormalTextComponent(value: String, onClick: () -> Unit) {
    ClickableText(
        text = AnnotatedString(value),
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            color = colorResource(id = R.color.colorGray),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )
    )
}