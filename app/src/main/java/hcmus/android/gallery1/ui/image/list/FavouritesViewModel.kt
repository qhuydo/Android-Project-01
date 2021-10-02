package hcmus.android.gallery1.ui.image.list

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.repository.FavouriteRepository
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.main.MainViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class FavouritesViewModel private constructor(private val favoriteRepository: FavouriteRepository) :
    ImageListViewModel() {

    private var _favourites = MutableLiveData<MutableList<Item>>()
    val favourites: LiveData<MutableList<Item>>
        get() = _favourites

    private var _listStateChangeEvent = LiveEvent<RecyclerViewListState>()
    val listStateChangeEvent: LiveData<RecyclerViewListState>
        get() = _listStateChangeEvent

    fun init() {
        loadData()
    }

    override fun loadData(callback: (() -> Unit)?) {
        viewModelScope.launch {
            _favourites.value = favoriteRepository.getFavourites().toMutableList()
        }
    }

    override fun setCurrentDisplayingList(sharedViewModel: MainViewModel) {
        sharedViewModel.currentDisplayingList = _favourites.value
    }

    private suspend fun addFavourite(item: Item): RecyclerViewListState.ItemInserted {
        val begin = System.currentTimeMillis()
        val insertedItem = favoriteRepository.insert(item)

        val newFavourites = _favourites.value ?: mutableListOf()
        newFavourites.add(insertedItem)

        _favourites.value = newFavourites
        Timber.d("${System.currentTimeMillis() - begin}ms")

        return RecyclerViewListState.ItemInserted(newFavourites.lastIndex)

    }

    private suspend fun removeFavourite(item: Item): RecyclerViewListState.ItemRemoved {

        favoriteRepository.remove(item)

        val newFavourites = _favourites.value ?: mutableListOf()
        val idxToRemoved = newFavourites.indexOfFirst { it.id == item.id }
        newFavourites.removeAt(idxToRemoved)

        _favourites.value = newFavourites
        return RecyclerViewListState.ItemRemoved(idxToRemoved)
    }

    fun toggleFavourite(item: Item) = liveData(viewModelScope.coroutineContext) {
        val state = if (!favoriteRepository.isFavourite(item)) {
            addFavourite(item)
        } else {
            removeFavourite(item)
        }
        _listStateChangeEvent.value = state
        emit(state)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val favoriteRepository: FavouriteRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
                return FavouritesViewModel(favoriteRepository) as T
            }
            throw IllegalArgumentException("")
        }

    }
}