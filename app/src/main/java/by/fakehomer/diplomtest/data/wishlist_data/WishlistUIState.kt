package by.fakehomer.diplomtest.data.wishlist_data

data class WishlistUIState(
    val wishlists: List<Wishlist> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class Wishlist(
    val id: String,
    val name: String,
    var items: List<WishItem> = emptyList(),
)

data class WishItem(
    val id: String,
    val name: String,
    val price: Double,
    val link: String,
    val imageUrl: String? = null,
    val currency: String = "BYN"
)



