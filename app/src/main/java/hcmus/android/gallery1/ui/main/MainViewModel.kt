package hcmus.android.gallery1.ui.main

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.IntentSender
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.repository.CollectionRepository
import hcmus.android.gallery1.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

typealias ContentChangeState = Pair<Uri?, Int>
typealias RemovedItemInfo = Triple<Item, Long, String>

class MainViewModel private constructor(
    application: Application,
    private val repository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
) : AndroidViewModel(application) {

    private var contentObserver: ContentObserver? = null
    private var _contentChange = LiveEvent<ContentChangeState>()
    val contentChange: LiveData<ContentChangeState>
        get() = _contentChange

    var currentDisplayingList: MutableList<Item>? = null
        internal set
    var currentDisplayingItemPos: Int = 0
        internal set
    var itemListFromTab: TAB? = null
        internal set

    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete

    private var pendingRemovedItem: RemovedItemInfo? = null

    private var _removedItem = MutableLiveData<RemovedItemInfo>()
    val removedItem: LiveData<RemovedItemInfo>
        get() = _removedItem

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

    fun deleteItem(item: Item, fragmentName: String) {
        viewModelScope.launch {
            performDeletingItem(item, fragmentName)
        }
    }

    private suspend fun performDeletingItem(
        item: Item,
        fragmentName: String
    ) = withContext(Dispatchers.IO) {

        try {
            val rowsAffected = getApplication<Application>().contentResolver.delete(
                item.getUri(),
                null,
                null
            )

            Timber.d("performDeletingImage - delete item ${item.getUri()}")

            if (rowsAffected > 0) {
                _removedItem.postValue(
                    RemovedItemInfo(item, System.currentTimeMillis(), fragmentName)
                )
            }
        } catch (securityException: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException =
                    securityException as? RecoverableSecurityException
                        ?: throw securityException

                pendingRemovedItem = RemovedItemInfo(item, System.currentTimeMillis(), fragmentName)
                _permissionNeededForDelete.postValue(
                    recoverableSecurityException.userAction.actionIntent.intentSender
                )

            } else {
                throw securityException
            }
        }
    }

    fun deletePendingItem() {
        pendingRemovedItem?.let {
            val (item, _, fragmentName) = it
            deleteItem(item, fragmentName)
            pendingRemovedItem = null
        }
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