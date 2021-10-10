package hcmus.android.gallery1.persistent

import androidx.lifecycle.LiveData
import androidx.room.*
import hcmus.android.gallery1.data.CustomAlbum
import hcmus.android.gallery1.data.CustomAlbumCrossRef
import hcmus.android.gallery1.data.CustomAlbumInfo
import hcmus.android.gallery1.data.CustomAlbumItem
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CustomAlbumDao {

    @Transaction
    @Query("select * from custom_album_info")
    abstract suspend fun getAllAlbums(): List<CustomAlbum>

    @Transaction
    @Query("select * from custom_album_info")
    abstract fun getAllAlbumsAsLiveData(): LiveData<List<CustomAlbum>>

    @Transaction
    @Query("select * from custom_album_info")
    abstract fun getAllAlbumsAsFlow(): Flow<List<CustomAlbum>>

    suspend fun insert(customAlbum: CustomAlbum) {

        val albumId = insertInfo(customAlbum.albumInfo)
        customAlbum.albumInfo.id = albumId

        val items = customAlbum.albumItems
        insertAllItems(items)

        insertAllCustomAlbumCrossRefs(items.map {
            CustomAlbumCrossRef(
                albumId = albumId,
                itemId = it.id
            )
        })
    }

    suspend fun insertItemIntoAlbum(items: List<CustomAlbumItem>, albumId: Long) {
        insertAllItems(items)
        insertAllCustomAlbumCrossRefs(items.map {
            CustomAlbumCrossRef(
                albumId = albumId,
                itemId = it.id
            )
        })
    }

    suspend fun insertItemIntoAlbum(item: CustomAlbumItem, albumIds: List<Long>) {
        insertItem(item)
        insertAllCustomAlbumCrossRefs(albumIds.map {
            CustomAlbumCrossRef(
                albumId = it,
                itemId = item.id
            )
        })
    }

    /**
     * inefficient, don't use this
     */
    suspend fun insertItemIntoAlbum(items: List<CustomAlbumItem>, albumIds: List<Long>) {
        insertAllItems(items)
        items.forEach { item ->
            insertAllCustomAlbumCrossRefs(albumIds.map { albumId ->
                CustomAlbumCrossRef(albumId, item.id)
            })
        }
    }

    suspend fun insertItemIntoAlbum(item: CustomAlbumItem, albumId: Long) {
        insertItem(item)
        insertCustomAlbumCrossRef(CustomAlbumCrossRef(albumId = albumId, itemId = item.id))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllItems(items: List<CustomAlbumItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertItem(customAlbumItems: CustomAlbumItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertInfo(customAlbumInfo: CustomAlbumInfo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllCustomAlbumCrossRefs(
        customAlbumCrossRefs: List<CustomAlbumCrossRef>
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCustomAlbumCrossRef(customAlbumCrossRef: CustomAlbumCrossRef)

    @Transaction
    @Delete
    abstract suspend fun deleteAlbum(customAlbumInfo: CustomAlbumInfo)

    @Transaction
    @Delete
    abstract suspend fun deleteAlbumItem(item: CustomAlbumItem)

    @Query("delete from custom_album_cross_ref where album_id=:albumId and item_id in (:itemIds)")
    abstract suspend fun deleteItemFromAlbum(itemIds: List<Long>, albumId: Long)

    @Query("select count(*) from custom_album_info where name=:name")
    abstract suspend fun containsName(name: String): Boolean

    @Query("select id from custom_album_info")
    abstract suspend fun allAlbumIds(): List<Long>

    @Query("select count(*) from custom_album_cross_ref where album_id=:albumId and item_id=:itemId")
    abstract suspend fun containsItemInAlbum(itemId: Long, albumId: Long): Boolean
}