package hcmus.android.gallery1.ui.collection.list

import androidx.lifecycle.*
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.CollectionRepository
import hcmus.android.gallery1.repository.PreferenceRepository
import hcmus.android.gallery1.ui.base.collection.CollectionListViewModel
import kotlinx.coroutines.launch

class AlbumViewModel private constructor(
    private val collectionRepository: CollectionRepository,
    preferenceRepository: PreferenceRepository
) : CollectionListViewModel(TAB.ALBUM, preferenceRepository) {

    fun init() {
        loadData()
    }

    override fun loadData(callback: (() -> Unit)?) {
        viewModelScope.launch {
            _collections.value = collectionRepository.getAllCollections().toMutableList()
            callback?.invoke()
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val collectionRepository: CollectionRepository,
        private val preferenceRepository: PreferenceRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
                return AlbumViewModel(collectionRepository, preferenceRepository) as T
            }
            throw IllegalArgumentException()
        }

    }

}