package hcmus.android.gallery1.persistent

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hcmus.android.gallery1.data.Favourite

@Database(entities = [Favourite::class], version = 0, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract val favouriteDao: FavouriteDao

    abstract val customAlbumDao: CustomAlbumDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun Context.getDatabaseInstance(): AppDatabase {

            return INSTANCE ?: synchronized(AppDatabase::class) {

                Room.databaseBuilder(this, AppDatabase::class.java, "gallery_one.db")
                    .fallbackToDestructiveMigration()
                    .build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}