package by.fakehomer.diplomtest.data.wishlist_data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WishlistViewModel : ViewModel() {
    private val repository = WishlistRepository()
    private val _uiState = MutableStateFlow(WishlistUIState())
    val uiState: StateFlow<WishlistUIState> = _uiState

    init {
        loadWishlists()
    }

    fun loadWishlists() {
        repository.loadWishlistsFromFirestore { wishlists ->
            _uiState.value = WishlistUIState(wishlists = wishlists.values.flatten())
        }
    }

    fun saveWishlists(dateKey: String, wishlists: List<Wishlist>) {
        repository.saveWishlistToFirestore(dateKey, wishlists)
    }

    fun addWishlist(name: String) {
        viewModelScope.launch {
            val newWishlist = Wishlist(id = generateId(), name = name)
            _uiState.value = _uiState.value.copy(
                wishlists = _uiState.value.wishlists + newWishlist
            )
            saveWishlists(generateDateKey(), _uiState.value.wishlists)
        }
    }

    fun removeWishlist(wishlistId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                wishlists = _uiState.value.wishlists.filterNot { it.id == wishlistId }
            )
            saveWishlists(generateDateKey(), _uiState.value.wishlists)
        }
    }

    fun addItemToWishlist(wishlistId: String, name: String, price: Double, link: String, imageUrl: String?, currency: String) {
        viewModelScope.launch {
            Log.d("WishlistViewModel", "Adding item to wishlist: $wishlistId")
            val imageUri = imageUrl?.let { Uri.parse(it) }
            if (imageUri != null) {
                Log.d("WishlistViewModel", "Uploading image: $imageUri")
                repository.uploadImageToFirebaseStorage(imageUri, onSuccess = { url ->
                    Log.d("WishlistViewModel", "Image uploaded: $url")
                    addItem(wishlistId, name, price, link, url, currency)
                }, onFailure = { exception ->
                    Log.e("WishlistViewModel", "Image upload failed", exception)
                })
            } else {
                addItem(wishlistId, name, price, link, imageUrl, currency)
            }
        }
    }

    private fun addItem(wishlistId: String, name: String, price: Double, link: String, imageUrl: String?, currency: String) {
        viewModelScope.launch {
            val updatedWishlists = _uiState.value.wishlists.map { wishlist ->
                if (wishlist.id == wishlistId) {
                    val newItem = WishItem(id = generateId(), name = name, price = price, link = link, imageUrl = imageUrl, currency = currency)
                    wishlist.copy(items = wishlist.items + newItem)
                } else {
                    wishlist
                }
            }
            _uiState.value = _uiState.value.copy(wishlists = updatedWishlists)
            saveWishlists(generateDateKey(), _uiState.value.wishlists)
        }
    }

    fun updateItemInWishlist(wishlistId: String, itemId: String, name: String, price: Double, link: String, imageUrl: String?, currency: String) {
        viewModelScope.launch {
            val imageUri = imageUrl?.let { Uri.parse(it) }
            if (imageUri != null) {
                repository.uploadImageToFirebaseStorage(imageUri, onSuccess = { url ->
                    updateItem(wishlistId, itemId, name, price, link, url, currency)
                }, onFailure = {
                    // Handle failure
                })
            } else {
                updateItem(wishlistId, itemId, name, price, link, imageUrl, currency)
            }
        }
    }

    private fun updateItem(wishlistId: String, itemId: String, name: String, price: Double, link: String, imageUrl: String?, currency: String) {
        viewModelScope.launch {
            val updatedWishlists = _uiState.value.wishlists.map { wishlist ->
                if (wishlist.id == wishlistId) {
                    val updatedItems = wishlist.items.map { item ->
                        if (item.id == itemId) {
                            item.copy(name = name, price = price, link = link, imageUrl = imageUrl, currency = currency)
                        } else {
                            item
                        }
                    }
                    wishlist.copy(items = updatedItems)
                } else {
                    wishlist
                }
            }
            _uiState.value = _uiState.value.copy(wishlists = updatedWishlists)
            saveWishlists(generateDateKey(), _uiState.value.wishlists)
        }
    }

    fun removeItemFromWishlist(wishlistId: String, itemId: String) {
        viewModelScope.launch {
            val updatedWishlists = _uiState.value.wishlists.map { wishlist ->
                if (wishlist.id == wishlistId) {
                    wishlist.copy(items = wishlist.items.filterNot { it.id == itemId })
                } else {
                    wishlist
                }
            }
            _uiState.value = _uiState.value.copy(wishlists = updatedWishlists)
            saveWishlists(generateDateKey(), _uiState.value.wishlists)
        }
    }

    private fun generateId(): String {
        return System.currentTimeMillis().toString()
    }

    private fun generateDateKey(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
