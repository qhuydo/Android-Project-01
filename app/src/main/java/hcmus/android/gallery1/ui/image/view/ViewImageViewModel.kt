package hcmus.android.gallery1.ui.image.view

import android.app.Application
import androidx.lifecycle.*
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.repository.FavouriteRepository
import hcmus.android.gallery1.repository.PhotoRepository
import hcmus.android.gallery1.repository.PreferenceRepository
import kotlinx.coroutines.launch

class ViewImageViewModel private constructor(
    application: Application,
    private val photoRepository: PhotoRepository,
    private val favouriteRepository: FavouriteRepository,
    private val preferenceRepository: PreferenceRepository
) : AndroidViewModel(application) {

    private val _item = MutableLiveData<Item>(null)
    val item: LiveData<Item>
        get() = _item

    val isFavourite = Transformations.switchMap(_item) {
        it?.let { favouriteRepository.isFavourite2(it.id) }
    }

    val isAudioMuted = preferenceRepository.muteAudioLiveData

    fun changeAudioMuteStatus()  {
        preferenceRepository.muteAudio = !preferenceRepository.muteAudio
    }


    fun setItem(item: Item) = viewModelScope.launch {
        _item.value = photoRepository.getItem(item.id)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val application: Application,
        private val photoRepository: PhotoRepository,
        private val favouriteRepository: FavouriteRepository,
        private val preferenceRepository: PreferenceRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ViewImageViewModel::class.java)) {
                return ViewImageViewModel(
                    application,
                    photoRepository,
                    favouriteRepository,
                    preferenceRepository
                ) as T
            }
            throw IllegalArgumentException("")
        }

    }
}