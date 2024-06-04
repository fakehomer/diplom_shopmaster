package by.fakehomer.diplomtest.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.fakehomer.diplomtest.R
import by.fakehomer.diplomtest.data.signup_data.EmailVerificationViewModel
import by.fakehomer.diplomtest.navigation.Screen
import by.fakehomer.diplomtest.navigation.ShoppingPlannerAppRouter

@Composable
fun EmailVerificationScreen(emailVerificationViewModel: EmailVerificationViewModel = viewModel()) {
    val emailVerified by emailVerificationViewModel.emailVerified.collectAsState()
    val logoPainter = painterResource(id = R.drawable.diploma_logo_square)

    LaunchedEffect(emailVerified) {
        if (emailVerified) {
            ShoppingPlannerAppRouter.navigateTo(Screen.HomeScreen)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            JumpingLogo(logoPainter)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Письмо подтверждения было отправлено. \nПожалуйста проверьте вашу почту.",
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun JumpingLogo(painter: Painter) {
    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Image(
        painter = painter,
        contentDescription = "App Logo",
        modifier = Modifier
            .size(100.dp)
            .offset(y = offset.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun EmailVerificationPreview() {
    MaterialTheme {
        EmailVerificationScreen()
    }
}