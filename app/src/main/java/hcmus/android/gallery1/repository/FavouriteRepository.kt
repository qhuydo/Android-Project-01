package hcmus.android.gallery1.repository

import androidx.lifecycle.LiveData
import hcmus.android.gallery1.data.DataSource
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.data.toFavourite
import hcmus.android.gallery1.persistent.FavouriteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import timber.log.Timber

interface FavouriteRepository {
    suspend fun isFavourite(item: Item): Boolean
    suspend fun getFavourites(): List<Item>
    suspend fun insert(item: Item): Item
    suspend fun remove(item: Item)
    fun isFavourite2(itemId: Long): LiveData<Boolean>
}

class FavouriteRepositoryImpl private constructor(
    private val mediaStoreSource: DataSource,
    private val favouriteDao: FavouriteDao
) : FavouriteRepository {

    companion object {
        @Volatile
        private var INSTANCE: FavouriteRepository? = null

        fun getInstance(
            mediaStoreSource: DataSource,
            favouriteDao: FavouriteDao
        ): FavouriteRepository {

            return INSTANCE ?: synchronized(FavouriteRepositoryImpl::class) {
                FavouriteRepositoryImpl(
                    mediaStoreSource,
                    favouriteDao
                ).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val scope = Job() + Dispatchers.IO

    override suspend fun isFavourite(item: Item): Boolean = withContext(scope) {
        return@withContext favouriteDao.containsId(item.id)
    }

    override suspend fun getFavourites(): List<Item> = withContext(scope) {
        val begin = System.currentTimeMillis()
        val favouriteItems = favouriteDao.getAll().map { it.id to it.dateAdded }.toMap()

        val items = mediaStoreSource.getItems { item -> item.id in favouriteItems }.apply {
            sortBy { item -> favouriteItems[item.id] }
        }
        Timber.d("getFavourites ${System.currentTimeMillis() - begin}ms")
        return@withContext items
    }

    override suspend fun insert(item: Item): Item = withContext(scope) {
        favouriteDao.insert(item.toFavourite())
         return@withContext mediaStoreSource.getItemById(item.id) ?: item.copy()
    }

    override suspend fun remove(item: Item) = withContext(scope) {
        favouriteDao.delete(item.toFavourite())
    }

    override fun isFavourite2(itemId: Long) = favouriteDao.containsId2(itemId)
}