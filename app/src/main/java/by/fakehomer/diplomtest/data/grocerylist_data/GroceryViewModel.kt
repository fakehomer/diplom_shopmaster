package by.fakehomer.diplomtest.data.grocerylist_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroceryViewModel : ViewModel() {
    private val repository = GroceryListRepository()

    private val _groceryLists = MutableLiveData<Map<String, List<List<Pair<String, Boolean>>>>>()
    val groceryLists: LiveData<Map<String, List<List<Pair<String, Boolean>>>>> = _groceryLists

    init {
        loadGroceryLists()
    }

    fun loadGroceryLists() {
        repository.loadListsFromFirestore { lists ->
            _groceryLists.postValue(lists)
        }
    }

    fun saveGroceryList(dateKey: String, lists: List<List<Pair<String, Boolean>>>) {
        repository.saveListToFirestore(dateKey, lists)
    }

    fun updateGroceryLists(newLists: Map<String, List<List<Pair<String, Boolean>>>>) {
        _groceryLists.value = newLists
    }
}
