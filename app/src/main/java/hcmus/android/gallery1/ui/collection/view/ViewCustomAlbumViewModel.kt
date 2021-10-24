package hcmus.android.gallery1.ui.collection.view

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.CustomAlbum.Companion.INVALID_ID
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.*
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.main.MainViewModel
import kotlinx.coroutines.launch

class ViewCustomAlbumViewModel private constructor(
    private val photoRepository: PhotoRepository,
    private val customAlbumRepository: CustomAlbumRepository,
    preferenceRepository: PreferenceRepository
) : ImageListViewModel(TAB.ALL, preferenceRepository) {

    private val _customAlbum: MutableLiveData<Collection?> = MutableLiveData()
    val customAlbum: LiveData<Collection?> by ::_customAlbum

    private var _listStateChangeEvent = LiveEvent<RecyclerViewListState>()
    val listStateChangeEvent: LiveData<RecyclerViewListState> by ::_listStateChangeEvent

    override fun loadData(callback: (() -> Unit)?) {}

    override fun setCurrentDisplayingList(sharedViewModel: MainViewModel) {
        sharedViewModel.currentDisplayingList = _photos.value
    }

    fun setCollection(collectionId: Long) {
        customAlbumRepository.getCustomAlbum(collectionId)
            .asLiveData(viewModelScope.coroutineContext)
            .apply {
                observeForever { collection ->
                    viewModelScope.launch {
                        _customAlbum.value = collection
                        val photoList = photoRepository
                            .getItemFromIds(collection?.itemIds ?: emptyList())
                            .toMutableList()

                        _photos.value?.let {
                            it.clear()
                            it.addAll(photoList)
                            _listStateChangeEvent.value = RecyclerViewListState.DataSetChanged

                        } ?: run {
                            _photos.value = photoList
                        }
                    }
                }
            }
    }

    fun removeCollection(): LiveData<RemoveAlbumResult> = customAlbumRepository
        .removeAlbum(_customAlbum.value?.id ?: INVALID_ID)
        .asLiveData(viewModelScope.coroutineContext)

    fun renameCollection(newName: String): LiveData<RenameAlbumResult> = customAlbumRepository
        .renameAlbum(_customAlbum.value?.id ?: INVALID_ID, newName)
        .asLiveData(viewModelScope.coroutineContext)

    fun removeItemFromCustomAlbum(item: Item) = liveData(viewModelScope.coroutineContext) {
        _customAlbum.value?.let { collection ->
            emit(removeItem(item, collection).also { _listStateChangeEvent.value = it })
        }
    }

    fun addItemsIntoCustomAlbums(items: List<Long>) {
        viewModelScope.launch {
            customAlbum.value?.let { collection ->
                customAlbumRepository.addItemsToAlbum(items, collection.id)
                _listStateChangeEvent.value =
                    RecyclerViewListState.ItemInserted(collection.itemIds.size)
            }
        }
    }

    private suspend fun removeItem(
        item: Item,
        collection: Collection
    ): RecyclerViewListState.ItemRemoved {

        val newList = _photos.value ?: mutableListOf()
        val idxToRemoved = newList.indexOfFirst { it.id == item.id }
        if (idxToRemoved != -1) {
            newList.removeAt(idxToRemoved)
        }
        customAlbumRepository.removeItemFromAlbum(item.id, collection.id)

        return RecyclerViewListState.ItemRemoved(idxToRemoved)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val photoRepository: PhotoRepository,
        private val customAlbumRepository: CustomAlbumRepository,
        private val preferenceRepository: PreferenceRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ViewCustomAlbumViewModel::class.java)) {
                return ViewCustomAlbumViewModel(
                    photoRepository,
                    customAlbumRepository,
                    preferenceRepository
                ) as T
            }
            throw IllegalArgumentException()
        }
    }

}