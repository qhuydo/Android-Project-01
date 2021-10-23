package hcmus.android.gallery1.repository

import hcmus.android.gallery1.data.*
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.persistent.CustomAlbumDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

interface CustomAlbumRepository {
    fun getCustomAlbum(): Flow<List<Collection>>
    fun getCustomAlbum(id: Long): Flow<Collection?>
    fun insertNewAlbum(name: String): Flow<InsertAlbumResult>
    fun removeAlbum(id: Long): Flow<RemoveAlbumResult>
    fun renameAlbum(id: Long, newName: String): Flow<RenameAlbumResult>
    fun addItemToAlbum(item: Item, collectionIds: List<Long>)
    suspend fun addItemsToAlbum(items: List<Long>, collectionId: Long)
    suspend fun removeItemFromAlbum(itemId: Long, albumId: Long)
}

class CustomAlbumRepositoryImpl private constructor(
    private val customAlbumDao: CustomAlbumDao,
    private val mediaStoreSource: DataSource
) : CustomAlbumRepository {

    companion object {
        @Volatile
        private var INSTANCE: CustomAlbumRepository? = null

        fun getInstance(
            customAlbumDao: CustomAlbumDao,
            mediaStoreSource: DataSource
        ): CustomAlbumRepository {

            return INSTANCE ?: synchronized(this) {
                CustomAlbumRepositoryImpl(
                    customAlbumDao,
                    mediaStoreSource
                ).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val dispatcher = Dispatchers.IO
    private val coroutineContext = Job() + dispatcher
    private val scope = CoroutineScope(coroutineContext)

    override fun getCustomAlbum() = customAlbumDao
        .getAllAlbumsAsFlow()
        .flowOn(dispatcher)
        .map { it.map { album -> album.toCollection() } }
        .flowOn(Dispatchers.Default)

    override fun getCustomAlbum(id: Long) = customAlbumDao
        .getAlbumAsFlow(id)
        .map { it.firstOrNull()?.toCollection() }
        .flowOn(dispatcher)

    override fun insertNewAlbum(name: String): Flow<InsertAlbumResult> {
        return flow {
            if (name.isBlank()) {
                emit(InsertAlbumResult.FAILED_BLANK_NAME)
                return@flow
            }

            val albumInfo = CustomAlbumInfo(
                name = name,
                dateCreated = System.currentTimeMillis()
            )

            if (customAlbumDao.containsName(name)) {
                emit(InsertAlbumResult.FAILED_EXISTED_NAME)
            } else {
                val id = customAlbumDao.insertInfo(albumInfo)
                customAlbumDao.getAlbumInfo(id).firstOrNull()?.let {
                    Timber.d("New album added: name = $name, id = $it.id")
                    emit(InsertAlbumResult.SUCCEED)
                } ?: emit(InsertAlbumResult.FAILED_OTHER)
            }

        }.flowOn(dispatcher)
    }

    override fun removeAlbum(id: Long): Flow<RemoveAlbumResult> {
        return flow { emit(customAlbumDao.deleteAlbum(id)) }
            .map { if (it > 0) RemoveAlbumResult.SUCCEED else RemoveAlbumResult.FAILED }
            .flowOn(dispatcher)
    }

    override fun renameAlbum(id: Long, newName: String): Flow<RenameAlbumResult> {
        return flow {
            if (newName.isBlank()) {
                emit(RenameAlbumResult.FAILED_BLANK_NAME)
                return@flow
            }

            when {
                customAlbumDao.containsNameExcludingId(newName, id) -> {
                    emit(RenameAlbumResult.FAILED_EXISTED_NAME)
                }
                customAlbumDao.updateAlbumName(newName, id) > 0 -> {
                    emit(RenameAlbumResult.SUCCEED)
                }
                else -> {
                    emit(RenameAlbumResult.FAILED_OTHER)
                }
            }
        }
    }

    override suspend fun addItemsToAlbum(items: List<Long>, collectionId: Long) {
        withContext(coroutineContext) {
            if (items.isEmpty()) return@withContext
            customAlbumDao.insertItemIntoAlbum(
                items.map { CustomAlbumItem(it) },
                collectionId
            )
            updateThumbnail(collectionId)
        }
    }

    override fun addItemToAlbum(item: Item, collectionIds: List<Long>) {
        scope.launch {
            if (collectionIds.isEmpty()) return@launch
            customAlbumDao.insertItemIntoAlbum(
                CustomAlbumItem(item.id),
                collectionIds
            )
            customAlbumDao.updateAlbumThumbnail(item.getUri().toString(), collectionIds)
        }
    }

    override suspend fun removeItemFromAlbum(itemId: Long, albumId: Long) {
        withContext(coroutineContext) {
            customAlbumDao.deleteItemFromAlbum(listOf(itemId), albumId)
            updateThumbnail(albumId)
        }
    }

    private fun updateThumbnail(albumId: Long) {
        val itemId = customAlbumDao.getLastAddedItem(albumId)
        val item = itemId?.let {
            mediaStoreSource.getItemById(itemId)
        }
        customAlbumDao.updateAlbumThumbnail(
            item?.getUri()?.toString() ?: "", listOf(albumId)
        )
    }
}

enum class InsertAlbumResult {
    SUCCEED,
    FAILED_BLANK_NAME,
    FAILED_EXISTED_NAME,
    FAILED_OTHER
}

enum class RenameAlbumResult {
    SUCCEED,
    FAILED_BLANK_NAME,
    FAILED_EXISTED_NAME,
    FAILED_OTHER
}

enum class RemoveAlbumResult {
    SUCCEED,
    FAILED
}