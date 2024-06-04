package by.fakehomer.diplomtest.data.signup_data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun storeUserInfo(user: FirebaseUser?,
                      firstName: String,
                      lastName: String,
                      email: String,
                      onSuccess: () -> Unit,
                      onFailure: (Exception) -> Unit) {
        if (user == null) {
            onFailure(Exception("User is null"))
            return
        }

        val dateKey = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val userInfo = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email
        )

        db.collection("users").document(user.uid)
            .collection("user_info").document(dateKey)
            .set(userInfo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}


