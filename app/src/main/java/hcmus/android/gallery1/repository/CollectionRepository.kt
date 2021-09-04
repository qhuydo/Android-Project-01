package hcmus.android.gallery1.repository

import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

interface CollectionRepository {
    suspend fun getAllCollections(): List<Collection>
    suspend fun getAllDateCollections(): List<Collection>
}

class CollectionRepositoryImpl(
    private val mediaStoreSource: DataSource
): CollectionRepository {

    companion object {
        @Volatile
        private var INSTANCE: CollectionRepository? = null

        fun getInstance(
            mediaStoreSource: DataSource
        ): CollectionRepository {

            return INSTANCE ?: synchronized(this) {
                CollectionRepositoryImpl(
                    mediaStoreSource
                ).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val scope = Job() + Dispatchers.IO

    override suspend fun getAllCollections(): List<Collection> = withContext(scope) {
        return@withContext mediaStoreSource.getCollections()
    }

    override suspend fun getAllDateCollections(): List<Collection> = withContext(scope) {
        return@withContext mediaStoreSource.getCollectionsByDate().sortedByDescending { it.id }
    }
}