package hcmus.android.gallery1.ui.image.list

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.PhotoRepository
import hcmus.android.gallery1.repository.PreferenceRepository
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.main.MainViewModel
import kotlinx.coroutines.launch

class AllPhotosViewModel private constructor(
    private val photoRepository: PhotoRepository,
    preferenceRepository: PreferenceRepository
) : ImageListViewModel(TAB.ALL, preferenceRepository) {

    private var _photos = MutableLiveData<MutableList<Item>>()
    val photos: LiveData<MutableList<Item>>
        get() = _photos

    fun init() {
        loadData()
    }

    override fun loadData(callback: (() -> Unit)?) {
        viewModelScope.launch {
            val newList = photoRepository.getAllPhotos().toMutableList()
            _photos.value?.let {
                it.clear()
                it.addAll(newList)
            } ?: run { _photos.value = newList }
            callback?.invoke()
        }
    }

    override fun setCurrentDisplayingList(sharedViewModel: MainViewModel) {
        sharedViewModel.currentDisplayingList = _photos.value
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val photoRepository: PhotoRepository,
        private val preferenceRepository: PreferenceRepository
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AllPhotosViewModel::class.java)) {
                return AllPhotosViewModel(photoRepository, preferenceRepository) as T
            }
            throw IllegalArgumentException()
        }

    }

}