package hcmus.android.gallery1.ui.image.view

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.repository.FavouriteRepository
import hcmus.android.gallery1.repository.PhotoRepository
import kotlinx.coroutines.launch

class ViewImageViewModel private constructor(
    private val photoRepository: PhotoRepository,
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {

    private val _item = MutableLiveData<Item>(null)
    val item: LiveData<Item>
        get() = _item

    val isFavourite = Transformations.switchMap(_item) {
        favouriteRepository.isFavourite2(it.id)
    }

    fun setItem(item: Item) = viewModelScope.launch {
        _item.value = photoRepository.getItem(item.id)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val photoRepository: PhotoRepository,
        private val favouriteRepository: FavouriteRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ViewImageViewModel::class.java)) {
                return ViewImageViewModel(photoRepository, favouriteRepository) as T
            }
            throw IllegalArgumentException("")
        }

    }
}