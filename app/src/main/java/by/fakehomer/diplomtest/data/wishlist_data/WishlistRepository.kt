package by.fakehomer.diplomtest.data.wishlist_data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import android.util.Log

class WishlistRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    fun uploadImageToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        user?.let {
            val storageRef = storage.reference.child("users/${it.uid}/images/${imageUri.lastPathSegment}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }.addOnFailureListener { exception ->
                        onFailure(exception)
                    }
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }

    fun saveWishlistToFirestore(dateKey: String, wishlists: List<Wishlist>) {
        if (user != null) {
            val wishlistData = wishlists.map { wishlist ->
                mapOf(
                    "id" to wishlist.id,
                    "name" to wishlist.name,
                    "items" to wishlist.items.map { item ->
                        mapOf(
                            "id" to item.id,
                            "name" to item.name,
                            "price" to item.price,
                            "link" to item.link,
                            "imageUrl" to item.imageUrl,
                            "currency" to item.currency
                        )
                    }
                )
            }

            db.collection("users").document(user.uid)
                .collection("wishlists").document(dateKey)
                .set(mapOf("wishlists" to wishlistData))
                .addOnSuccessListener {
                    Log.d("Firestore", "Wishlist successfully saved!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error saving wishlist", e)
                }
        }
    }

    fun loadWishlistsFromFirestore(onWishlistsLoaded: (Map<String, List<Wishlist>>) -> Unit) {
        if (user != null) {
            db.collection("users").document(user.uid)
                .collection("wishlists")
                .get()
                .addOnSuccessListener { result ->
                    val wishlists = mutableMapOf<String, List<Wishlist>>()
                    for (document in result) {
                        val wishlistsData = document["wishlists"] as List<Map<String, Any>>
                        val parsedWishlists = wishlistsData.map { wishlistData ->
                            val itemsData = wishlistData["items"] as List<Map<String, Any>>
                            val items = itemsData.map { itemData ->
                                WishItem(
                                    id = itemData["id"] as String,
                                    name = itemData["name"] as String,
                                    price = itemData["price"] as Double,
                                    link = itemData["link"] as String,
                                    imageUrl = itemData["imageUrl"] as String?,
                                    currency = itemData["currency"] as String
                                )
                            }
                            Wishlist(
                                id = wishlistData["id"] as String,
                                name = wishlistData["name"] as String,
                                items = items
                            )
                        }
                        wishlists[document.id] = parsedWishlists
                    }
                    onWishlistsLoaded(wishlists)
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error loading wishlists", e)
                }
        }
    }
}
