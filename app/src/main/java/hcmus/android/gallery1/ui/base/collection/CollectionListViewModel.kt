package hcmus.android.gallery1.ui.base.collection

import android.view.View
import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.PreferenceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val CLICK_DELAY = 1000L

abstract class CollectionListViewModel(
    val tab: TAB,
    preferenceRepository: PreferenceRepository
) : ViewModel() {

    val viewMode = preferenceRepository.getViewMode(tab)

    protected var mutableCollections = MutableLiveData<MutableList<Collection>>()
    val collections: LiveData<MutableList<Collection>> by ::mutableCollections

    private var _navigateToCollectionDetails = LiveEvent<Collection>()
    val navigateToCollectionDetails: LiveData<Collection> by ::_navigateToCollectionDetails

    val placeholderVisibility = Transformations.map(collections) {
        if (collections.value?.isNotEmpty() == true) View.GONE else View.VISIBLE
    }

    private var shouldNavigateToImageView: Boolean = true

    fun navigateToCollectionDetails(collection: Collection) {
        if (shouldNavigateToImageView) {
            shouldNavigateToImageView = false

            _navigateToCollectionDetails.postValue(collection)

            viewModelScope.launch {
                delay(CLICK_DELAY)
                shouldNavigateToImageView = true
            }
        }
    }

    abstract fun loadData(callback: (() -> Unit)? = null)
}