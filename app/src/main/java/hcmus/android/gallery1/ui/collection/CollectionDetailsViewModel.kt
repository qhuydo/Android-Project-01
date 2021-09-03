package hcmus.android.gallery1.ui.collection

import androidx.lifecycle.*
import hcmus.android.gallery1.repository.PhotoRepository
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.Item
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class CollectionDetailsViewModel(private val photoRepository: PhotoRepository): ViewModel() {

    private val _collection = MutableLiveData<Collection>()
    val collection: LiveData<Collection> = _collection

    private val _photos = MutableLiveData<MutableList<Item>>()
    val photos: LiveData<MutableList<Item>>
        get() = _photos

    fun setCollection(collection: Collection) = _collection.postValue(collection)

    fun getPhotos() = viewModelScope.launch  {
         _collection.value?.let {
             _photos.value = when (it.type) {
                 Collection.TYPE_DATE -> photoRepository.getItemFromDateCollection(it.id)
                 Collection.TYPE_ALBUM -> photoRepository.getItemsFromCollection(it.id)
                 else -> emptyList()
             }.toMutableList()
         }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val photoRepository: PhotoRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CollectionDetailsViewModel::class.java)) {
                return CollectionDetailsViewModel(photoRepository) as T
            }
            throw IllegalArgumentException()
        }

    }

}