package hcmus.android.gallery1.ui.main

import android.app.Application
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.repository.CollectionRepository
import hcmus.android.gallery1.repository.PhotoRepository
import timber.log.Timber

typealias ContentChangeState = Pair<Uri?, Int>

class MainViewModel private constructor(
    application: Application,
    private val repository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
) : AndroidViewModel(application) {

    private var contentObserver: ContentObserver? = null
    private var _contentChange = LiveEvent<ContentChangeState>()
    val contentChange: LiveData<ContentChangeState>
        get() = _contentChange

    init {

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {

            override fun onChange(
                selfChange: Boolean,
                uri: Uri?,
                flags: Int
            ) {
                Timber.d("onChange() - selfChange: $selfChange, uris: $uri, flags: $flags")
                val newValue = ContentChangeState(uri, flags)
                _contentChange.value?.let {
                    if (it != newValue) {
                        _contentChange.value = newValue
                    }
                } ?: run { _contentChange.value = newValue }

            }
        }

        getApplication<Application>().contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
        contentObserver = observer
    }

    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
        super.onCleared()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val application: Application,
        private val repository: PhotoRepository,
        private val collectionRepository: CollectionRepository,
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application, repository, collectionRepository) as T
            }
            throw IllegalArgumentException()
        }
    }
}