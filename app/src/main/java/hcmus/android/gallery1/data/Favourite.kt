package hcmus.android.gallery1.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class Favourite(

    // Id of the MediaItem
    // retrieved from BaseColumns._ID column in MediaStore
    @PrimaryKey
    var id: Long,

    @ColumnInfo(name = "date_added")
    var dateAdded: Long = System.currentTimeMillis()
)