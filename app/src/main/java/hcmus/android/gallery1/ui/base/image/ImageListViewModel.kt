package hcmus.android.gallery1.ui.base.image

import android.opengl.Visibility
import android.view.View
import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.PreferenceRepository
import hcmus.android.gallery1.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class ImageListViewModel(
    tab: TAB,
    preferenceRepository: PreferenceRepository
) : ViewModel() {

    protected var _photos = MutableLiveData<MutableList<Item>>()
    val photos: LiveData<MutableList<Item>>
        get() = _photos

    val placeholderVisibility = Transformations.map(photos) {
        if (photos.value?.isNotEmpty() == true) View.GONE else View.VISIBLE
    }

    val viewMode = preferenceRepository.getViewMode(tab)

    private var _navigateToImageView = LiveEvent<Int>()
    val navigateToImageView: LiveData<Int>
        get() = _navigateToImageView

    fun navigateToImageView(itemPosition: Int) {
        _navigateToImageView.postValue(itemPosition)
    }

    abstract fun loadData(callback: (() -> Unit)? = null)
    abstract fun setCurrentDisplayingList(sharedViewModel: MainViewModel)

    fun removeItemFromList(item: Item, callback: ((itemPosition: Int) -> Unit)?) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _photos.value
                    ?.indexOfFirst { it.id == item.id }
                    ?.takeIf { index -> index >= 0 }
                    ?.also { index ->
                        _photos.value?.removeAt(index)
                        withContext(Dispatchers.Main) {
                            callback?.invoke(index)
                        }
                    }
            }
        }
    }
}
