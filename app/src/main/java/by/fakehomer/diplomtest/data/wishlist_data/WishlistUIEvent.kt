package by.fakehomer.diplomtest.data.wishlist_data

sealed class WishlistUIEvent {
    data class AddWishlist(val name: String) : WishlistUIEvent()
    data class DeleteWishlist(val id: String) : WishlistUIEvent()
    object LoadWishlists : WishlistUIEvent()
}

