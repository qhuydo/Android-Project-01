package hcmus.android.gallery1.repository

import hcmus.android.gallery1.data.DataSource
import hcmus.android.gallery1.data.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

interface PhotoRepository {
    suspend fun getAllPhotos(): List<Item>
    suspend fun getItemsFromCollection(collectionId: Long): List<Item>
    suspend fun getItemFromDateCollection(timeInMillis: Long): List<Item>
    suspend fun getItem(mediaStoreId: Long): Item?
    suspend fun getItemFromIds(ids: List<Long>): List<Item>
}

class PhotoRepositoryImpl private constructor(
    private val mediaStoreSource: DataSource
) : PhotoRepository {

    companion object {
        @Volatile
        private var INSTANCE: PhotoRepository? = null

        fun getInstance(
            mediaStoreSource: DataSource
        ): PhotoRepository {

            return INSTANCE ?: synchronized(this) {
                PhotoRepositoryImpl(
                    mediaStoreSource
                ).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val scope = Job() + Dispatchers.IO

    override suspend fun getAllPhotos(): List<Item> = withContext(scope) {
        return@withContext mediaStoreSource.getItems()
    }

    override suspend fun getItemsFromCollection(collectionId: Long): List<Item> =
        withContext(scope) {
            return@withContext mediaStoreSource.getItems(collectionId)
        }

    override suspend fun getItemFromDateCollection(timeInMillis: Long): List<Item> =
        withContext(scope) {
            return@withContext mediaStoreSource.getItemsByDate(timeInMillis)
        }

    override suspend fun getItem(mediaStoreId: Long): Item? = withContext(scope) {
        return@withContext mediaStoreSource.getItemById(mediaStoreId)
    }

    override suspend fun getItemFromIds(ids: List<Long>): List<Item> = withContext(scope) {
        return@withContext mediaStoreSource.getItemById(ids)
    }
}