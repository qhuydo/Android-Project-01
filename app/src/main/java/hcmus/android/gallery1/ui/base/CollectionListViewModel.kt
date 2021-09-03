package hcmus.android.gallery1.ui.base

import androidx.lifecycle.ViewModel
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.helpers.livedata.SingleLiveEvent

open class CollectionListViewModel: ViewModel() {

    private var _navigateToCollectionDetails = SingleLiveEvent<Collection>()
    val navigateToCollectionDetails: SingleLiveEvent<Collection>
        get() = _navigateToCollectionDetails


    fun navigateToCollectionDetails(collection: Collection) {
        _navigateToCollectionDetails.postValue(collection)
    }
}