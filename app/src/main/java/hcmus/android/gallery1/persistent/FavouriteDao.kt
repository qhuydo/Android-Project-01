package hcmus.android.gallery1.persistent

import androidx.room.*
import hcmus.android.gallery1.data.Favourite

@Dao
interface FavouriteDao {

    // Favourite object has `date_added` attribute which is used to track
    // the time when a media item was added to the favourite collection.
    // Therefore, conflict handling strategy to be used is IGNORE.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favourite: Favourite): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(favourites: List<Favourite>)

    @Delete
    suspend fun delete(vararg favourite: Favourite)

    @Query("select count(*) from favourite where id=:id")
    suspend fun containsId(id: Long): Boolean

    @Query("select * from favourite")
    suspend fun getAll(): List<Favourite>
}