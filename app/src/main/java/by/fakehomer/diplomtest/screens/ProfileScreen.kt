package by.fakehomer.diplomtest.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import by.fakehomer.diplomtest.R
import by.fakehomer.diplomtest.data.login_data.LoginViewModel
import by.fakehomer.diplomtest.navigation.Screen
import by.fakehomer.diplomtest.navigation.ShoppingPlannerAppRouter
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "No Email"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {
        // Background Image with email
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.default_profile),
                contentDescription = "Profile Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = userEmail,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
            )
        }

        // Options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            ProfileOption(
                icon = R.drawable.setting,
                text = "Настройки",
                onClick = { ShoppingPlannerAppRouter.navigateTo(Screen.SettingsScreen) }
            )
            Divider(color = Color.Gray, thickness = 1.dp)
            ProfileOption(
                icon = R.drawable.support,
                text = "Связаться с нами",
                onClick = { sendEmail(context) }
            )
            Divider(color = Color.Gray, thickness = 1.dp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = { loginViewModel.logout() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFF68B2C),
                contentColor = Color.White
            )
        ) {
            Text("Выйти")
        }
    }
}

@Composable
fun ProfileOption(icon: Int, text: String, onClick: () -> Unit) {
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

fun sendEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("shop_master@internet.ru"))
        putExtra(Intent.EXTRA_SUBJECT, "Сообщение из Shopmaster")
    }
    context.startActivity(Intent.createChooser(intent, "Send Email"))
}
