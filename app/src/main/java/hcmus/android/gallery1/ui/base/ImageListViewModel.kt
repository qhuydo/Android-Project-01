package hcmus.android.gallery1.ui.base

import androidx.lifecycle.*
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.livedata.SingleLiveEvent
import java.lang.IllegalArgumentException

open class ImageListViewModel : ViewModel() {

    private var _navigateToImageView = SingleLiveEvent<Item>()
    val navigateToImageView: SingleLiveEvent<Item>
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
