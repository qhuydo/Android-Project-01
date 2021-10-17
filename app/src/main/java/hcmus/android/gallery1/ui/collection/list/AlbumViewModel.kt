package hcmus.android.gallery1.ui.collection.list

import android.view.View
import androidx.lifecycle.*
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.CollectionRepository
import hcmus.android.gallery1.repository.CustomAlbumRepository
import hcmus.android.gallery1.repository.PreferenceRepository
import hcmus.android.gallery1.ui.base.collection.CollectionListViewModel
import kotlinx.coroutines.launch

class AlbumViewModel private constructor(
    private val collectionRepository: CollectionRepository,
    private val customAlbumRepository: CustomAlbumRepository,
    preferenceRepository: PreferenceRepository
) : CollectionListViewModel(TAB.ALBUM, preferenceRepository) {

    fun init() {
        loadData()
    }

    val customAlbums = customAlbumRepository
        .getCustomAlbum()
        .asLiveData(viewModelScope.coroutineContext)

    val customAlbumVisibility = Transformations.switchMap(customAlbums) {
        liveData {
            emit(if (it.isNotEmpty()) View.VISIBLE else View.GONE)
        }
    }

    override fun loadData(callback: (() -> Unit)?) {
        viewModelScope.launch {
            mutableCollections.value = collectionRepository.getAllCollections().toMutableList()
            callback?.invoke()
        }
    }

    fun addItemIntoCustomAlbums(item: Item, customAlbums: List<Long>) {
        customAlbumRepository.addItemToAlbum(item, customAlbums)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val collectionRepository: CollectionRepository,
        private val customAlbumRepository: CustomAlbumRepository,
        private val preferenceRepository: PreferenceRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
                return AlbumViewModel(
                    collectionRepository,
                    customAlbumRepository,
                    preferenceRepository
                ) as T
            }
            throw IllegalArgumentException()
        }

    }

}