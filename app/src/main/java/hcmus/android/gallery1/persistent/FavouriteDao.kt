package hcmus.android.gallery1.persistent

import androidx.room.*
import hcmus.android.gallery1.data.Favourite

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg favourite: Favourite)

    @Delete
    suspend fun delete(vararg favourite: Favourite)

    @Query("select count(*) from favourite where id=:id")
    suspend fun containsId(id: Long): Boolean

    @Query("select count(*) from favourite where path=:path")
    suspend fun containsPath(path: String): Boolean

    @Query("select * from favourite")
    suspend fun getAll(): List<Favourite>
}