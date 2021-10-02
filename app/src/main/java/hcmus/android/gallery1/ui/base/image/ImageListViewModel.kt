package hcmus.android.gallery1.ui.base.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.ui.main.MainViewModel

abstract class ImageListViewModel : ViewModel() {

    private var _navigateToImageView = LiveEvent<Int>()
    val navigateToImageView: LiveData<Int>
        get() = _navigateToImageView

    fun navigateToImageView(itemPosition: Int) {
        _navigateToImageView.postValue(itemPosition)
    }

    abstract fun loadData(callback: (() -> Unit)? = null)
    abstract fun setCurrentDisplayingList(sharedViewModel: MainViewModel)
}
