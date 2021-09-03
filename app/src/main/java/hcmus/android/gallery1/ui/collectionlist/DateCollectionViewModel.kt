package hcmus.android.gallery1.ui.collectionlist

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.repository.CollectionRepository
import hcmus.android.gallery1.ui.base.CollectionListViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class DateCollectionViewModel(collectionRepository: CollectionRepository) : CollectionListViewModel() {

    private var _collections = MutableLiveData<MutableList<Collection>>()
    val collections: LiveData<MutableList<Collection>>
        get() = _collections

    init {
        viewModelScope.launch {
            _collections.value = collectionRepository.getAllDateCollections().toMutableList()
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val collectionRepository: CollectionRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DateCollectionViewModel::class.java)) {
                return DateCollectionViewModel(collectionRepository) as T
            }
            throw IllegalArgumentException()
        }

    }

}