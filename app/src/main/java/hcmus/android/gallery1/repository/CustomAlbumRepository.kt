package hcmus.android.gallery1.repository

import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.persistent.CustomAlbumDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface CustomAlbumRepository {
    fun getCustomAlbum(): Flow<List<Collection>>
}

class CustomAlbumRepositoryImpl private constructor(
    private val customAlbumDao: CustomAlbumDao
) : CustomAlbumRepository {

    companion object {
        @Volatile
        private var INSTANCE: CustomAlbumRepository? = null

        fun getInstance(
            customAlbumDao: CustomAlbumDao
        ): CustomAlbumRepository {

            return INSTANCE ?: synchronized(this) {
                CustomAlbumRepositoryImpl(
                    customAlbumDao
                ).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val scope = Job() + Dispatchers.IO

    override fun getCustomAlbum() = customAlbumDao
        .getAllAlbumsAsFlow()
        .map { it.map { album -> album.toCollection() } }
        .flowOn(scope)
}