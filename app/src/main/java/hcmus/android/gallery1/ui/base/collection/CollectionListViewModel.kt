package hcmus.android.gallery1.ui.base.collection

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.PreferenceRepository

abstract class CollectionListViewModel(
    val tab: TAB,
    preferenceRepository: PreferenceRepository
) : ViewModel() {

    val viewMode = preferenceRepository.getViewMode(tab)

    protected var _collections = MutableLiveData<MutableList<Collection>>()
    val collections: LiveData<MutableList<Collection>>
        get() = _collections

    private var _navigateToCollectionDetails = LiveEvent<Collection>()
    val navigateToCollectionDetails: LiveData<Collection>
        get() = _navigateToCollectionDetails

    val placeholderVisibility = Transformations.map(collections) {
        if (collections.value?.isNotEmpty() == true) View.GONE else View.VISIBLE
    }

    fun navigateToCollectionDetails(collection: Collection) {
        _navigateToCollectionDetails.postValue(collection)
    }

    abstract fun loadData(callback: (() -> Unit)? = null)
}