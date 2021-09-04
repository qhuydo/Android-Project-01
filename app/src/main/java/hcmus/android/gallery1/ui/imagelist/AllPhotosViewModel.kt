package hcmus.android.gallery1.ui.imagelist

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.repository.PhotoRepository
import hcmus.android.gallery1.ui.base.imagelist.ImageListViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class AllPhotosViewModel(private val photoRepository: PhotoRepository): ImageListViewModel() {

    private var _photos = MutableLiveData<MutableList<Item>>()
    val photos: LiveData<MutableList<Item>>
        get() = _photos

    init {
        viewModelScope.launch {
            _photos.value = photoRepository.getAllPhotos().toMutableList()
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