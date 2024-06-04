package by.fakehomer.diplomtest.ShoppingPlannerApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import by.fakehomer.diplomtest.ShoppingPlannerApp.ShoppingPlannerApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingPlannerApp()
        }
    }
}
