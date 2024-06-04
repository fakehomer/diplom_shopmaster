package by.fakehomer.diplomtest.ShoppingPlannerApp

import android.app.Application
import com.google.firebase.FirebaseApp

class AppInitializer : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

