package by.fakehomer.diplomtest.data.grocerylist_data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class GroceryListRepository {

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    fun saveListToFirestore(dateKey: String, lists: List<List<Pair<String, Boolean>>>) {
        if (user != null) {
            val listData = lists.flatMapIndexed { listIndex, list ->
                list.map { mapOf("name" to it.first, "checked" to it.second, "listIndex" to listIndex) }
            }

            db.collection("users").document(user.uid)
                .collection("lists").document(dateKey)
                .set(mapOf("lists" to listData))
                .addOnSuccessListener {
                    Log.d("Firestore", "List successfully saved!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error saving list", e)
                }
        }
    }

    fun loadListsFromFirestore(onListsLoaded: (Map<String, List<List<Pair<String, Boolean>>>>) -> Unit) {
        if (user != null) {
            db.collection("users").document(user.uid)
                .collection("lists")
                .get()
                .addOnSuccessListener { result ->
                    val lists = mutableMapOf<String, List<List<Pair<String, Boolean>>>>()
                    for (document in result) {
                        val listsData = document["lists"] as List<Map<String, Any>>
                        val grouped = listsData.groupBy { it["listIndex"] as Long }
                        val listsParsed = grouped.values.map { list ->
                            list.map { Pair(it["name"] as String, it["checked"] as Boolean) }
                        }
                        lists[document.id] = listsParsed
                    }
                    onListsLoaded(lists)
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error loading lists", e)
                }
        }
    }
}
