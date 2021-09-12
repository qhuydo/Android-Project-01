package hcmus.android.gallery1.ui.base.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Item

abstract class ImageListViewModel : ViewModel() {

    private var _navigateToImageView = LiveEvent<Item>()
    val navigateToImageView: LiveData<Item>
        get() = _navigateToImageView

    fun navigateToImageView(item: Item) {
        _navigateToImageView.postValue(item)
    }

    abstract fun loadData(callback: (() -> Unit)? = null)
}
