package hcmus.android.gallery1.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favourite",
    indices = [Index(value = ["path"], unique = true)]
)
data class Favourite(

    // Id of the MediaItem
    // retrieved from BaseColumns._ID column in MediaStore
    @PrimaryKey
    var id: Long,

    @ColumnInfo(name = "path")
    var path: String,

    @ColumnInfo(name = "file_name")
    var fileName: String,
)