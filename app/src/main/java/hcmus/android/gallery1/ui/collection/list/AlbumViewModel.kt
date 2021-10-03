package hcmus.android.gallery1.ui.collection.list

import androidx.lifecycle.*
import hcmus.android.gallery1.repository.CollectionRepository
import hcmus.android.gallery1.ui.base.collection.CollectionListViewModel
import kotlinx.coroutines.launch

class AlbumViewModel private constructor(private val collectionRepository: CollectionRepository) :
    CollectionListViewModel() {

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
    class Factory(private val collectionRepository: CollectionRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
                return AlbumViewModel(collectionRepository) as T
            }
            throw IllegalArgumentException()
        }

    }

}