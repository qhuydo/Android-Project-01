package hcmus.android.gallery1.persistent

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import hcmus.android.gallery1.data.CustomAlbumCrossRef
import hcmus.android.gallery1.data.CustomAlbumInfo
import hcmus.android.gallery1.data.CustomAlbumItem
import hcmus.android.gallery1.data.Favourite

@Database(
    entities = [
        Favourite::class,
        CustomAlbumItem::class,
        CustomAlbumInfo::class,
        CustomAlbumCrossRef::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = AppDatabase.MigrationSpec1To2::class)
    ]
)
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

    @DeleteColumn(
        tableName = "custom_album_info",
        columnName = "thumbnail_id"
    )
    class MigrationSpec1To2 : AutoMigrationSpec
}