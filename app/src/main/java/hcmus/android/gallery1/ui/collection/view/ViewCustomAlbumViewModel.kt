package hcmus.android.gallery1.ui.collection.view

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.CustomAlbum.Companion.INVALID_ID
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.*
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.main.MainViewModel
import kotlinx.coroutines.launch

class ViewCustomAlbumViewModel private constructor(
    private val photoRepository: PhotoRepository,
    private val customAlbumRepository: CustomAlbumRepository,
    preferenceRepository: PreferenceRepository
) : ImageListViewModel(TAB.ALL, preferenceRepository) {

    private val _customAlbum: MutableLiveData<Collection?> = MutableLiveData()
    val customAlbum: LiveData<Collection?> by this::_customAlbum

    override fun loadData(callback: (() -> Unit)?) {}

    override fun setCurrentDisplayingList(sharedViewModel: MainViewModel) {
        sharedViewModel.currentDisplayingList = _photos.value
    }

    fun setCollection(collectionId: Long) {
        customAlbumRepository.getCustomAlbum(collectionId)
            .asLiveData(viewModelScope.coroutineContext)
            .apply {
                observeForever {
                    viewModelScope.launch {
                        _customAlbum.value = it
                        _photos.value = photoRepository
                            .getItemFromIds(it?.itemIds ?: emptyList())
                            .toMutableList()
                    }
                }
            }
    }

    fun removeCollection(): LiveData<RemoveAlbumResult> = customAlbumRepository
        .removeAlbum(_customAlbum.value?.id ?: INVALID_ID)
        .asLiveData(viewModelScope.coroutineContext)

    fun renameCollection(newName: String): LiveData<RenameAlbumResult> = customAlbumRepository
        .renameAlbum(_customAlbum.value?.id ?: INVALID_ID, newName)
        .asLiveData(viewModelScope.coroutineContext)

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val photoRepository: PhotoRepository,
        private val customAlbumRepository: CustomAlbumRepository,
        private val preferenceRepository: PreferenceRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ViewCustomAlbumViewModel::class.java)) {
                return ViewCustomAlbumViewModel(
                    photoRepository,
                    customAlbumRepository,
                    preferenceRepository
                ) as T
            }
            throw IllegalArgumentException()
        }
    }

}