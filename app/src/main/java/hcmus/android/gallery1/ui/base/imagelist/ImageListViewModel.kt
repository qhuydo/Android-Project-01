package hcmus.android.gallery1.ui.base.imagelist

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Item
import java.lang.IllegalArgumentException

open class ImageListViewModel : ViewModel() {

    private var _navigateToImageView = LiveEvent<Item>()
    val navigateToImageView: LiveData<Item>
        get() = _navigateToImageView

    fun navigateToImageView(item: Item) {
        _navigateToImageView.postValue(item)
    }

    class Factory : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ImageListViewModel::class.java)) {
                return ImageListViewModel() as T
            }
            throw IllegalArgumentException("")
        }

    }
}
