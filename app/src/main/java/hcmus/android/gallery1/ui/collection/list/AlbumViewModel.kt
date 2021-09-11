package hcmus.android.gallery1.ui.collection.list

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.repository.CollectionRepository
import hcmus.android.gallery1.ui.base.collection.CollectionListViewModel
import kotlinx.coroutines.launch

class AlbumViewModel private constructor(private val collectionRepository: CollectionRepository) :
    CollectionListViewModel() {

    private var _collections = MutableLiveData<MutableList<Collection>>()
    val collections: LiveData<MutableList<Collection>>
        get() = _collections

    fun init() {
        loadData()
    }

    override fun loadData() {
        viewModelScope.launch {
            _collections.value = collectionRepository.getAllCollections().toMutableList()
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