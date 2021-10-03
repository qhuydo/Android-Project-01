package hcmus.android.gallery1.ui.collection.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.CollectionRepository
import hcmus.android.gallery1.repository.PreferenceRepository
import hcmus.android.gallery1.ui.base.collection.CollectionListViewModel
import kotlinx.coroutines.launch

class DateCollectionViewModel private constructor(
    private val collectionRepository: CollectionRepository,
    preferenceRepository: PreferenceRepository
) : CollectionListViewModel(TAB.DATE, preferenceRepository) {

    fun init() {
        loadData()
    }

    override fun loadData(callback: (() -> Unit)?) {
        viewModelScope.launch {
            _collections.value = collectionRepository.getAllDateCollections().toMutableList()
            callback?.invoke()
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val collectionRepository: CollectionRepository,
        private val preferenceRepository: PreferenceRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DateCollectionViewModel::class.java)) {
                return DateCollectionViewModel(collectionRepository, preferenceRepository) as T
            }
            throw IllegalArgumentException()
        }

    }

}