package hcmus.android.gallery1.ui.base.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Item

open class ImageListViewModel : ViewModel() {

    private var _navigateToImageView = LiveEvent<Item>()
    val navigateToImageView: LiveData<Item>
        get() = _navigateToImageView

    fun navigateToImageView(item: Item) {
        _navigateToImageView.postValue(item)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ImageListViewModel::class.java)) {
                return ImageListViewModel() as T
            }
            throw IllegalArgumentException("")
        }

    }
}
