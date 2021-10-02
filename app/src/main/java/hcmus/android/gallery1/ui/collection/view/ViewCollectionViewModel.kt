package hcmus.android.gallery1.ui.collection.view

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.repository.PhotoRepository
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.main.MainViewModel
import kotlinx.coroutines.launch

class ViewCollectionViewModel private constructor(private val photoRepository: PhotoRepository) :
    ImageListViewModel() {

    private val _collection = MutableLiveData<Collection>()
    val collection: LiveData<Collection> = _collection

    private val _photos = MutableLiveData<MutableList<Item>>()
    val photos: LiveData<MutableList<Item>>
        get() = _photos

    override fun loadData(callback: (() -> Unit)?) {
        getPhotos()
    }

    override fun setCurrentDisplayingList(sharedViewModel: MainViewModel) {
        sharedViewModel.currentDisplayingList = _photos.value
    }

    fun setCollection(collection: Collection) = _collection.postValue(collection)

    fun getPhotos() = viewModelScope.launch {
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
            if (modelClass.isAssignableFrom(ViewCollectionViewModel::class.java)) {
                return ViewCollectionViewModel(photoRepository) as T
            }
            throw IllegalArgumentException()
        }

    }

}