package hcmus.android.gallery1.ui.base.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Collection

abstract class CollectionListViewModel: ViewModel() {

    private var _navigateToCollectionDetails = LiveEvent<Collection>()
    val navigateToCollectionDetails: LiveData<Collection>
        get() = _navigateToCollectionDetails


    fun navigateToCollectionDetails(collection: Collection) {
        _navigateToCollectionDetails.postValue(collection)
    }

    abstract fun loadData()
}