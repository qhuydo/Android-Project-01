package hcmus.android.gallery1.ui.image.list

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.repository.PhotoRepository
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import kotlinx.coroutines.launch

class AllPhotosViewModel private constructor(private val photoRepository: PhotoRepository) :
    ImageListViewModel() {

    private var _photos = MutableLiveData<MutableList<Item>>()
    val photos: LiveData<MutableList<Item>>
        get() = _photos

    fun init() {
        loadData()
    }

    override fun loadData() {
        viewModelScope.launch {
            val newList = photoRepository.getAllPhotos().toMutableList()
            _photos.value?.let {
                it.clear()
                it.addAll(newList)
            } ?: run { _photos.value = newList }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val photoRepository: PhotoRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AllPhotosViewModel::class.java)) {
                return AllPhotosViewModel(photoRepository) as T
            }
            throw IllegalArgumentException()
        }

    }

}