package hcmus.android.gallery1.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_album")
data class CustomAlbumInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    val name: String,

    @ColumnInfo(name = "date_created")
    var dateCreated: Long = System.currentTimeMillis(),

    // Id of the MediaItem to be used as a thumbnail of the album
    // retrieved from BaseColumns._ID column in MediaStore
    @ColumnInfo(name = "thumbnail_id")
    var thumbnailId: Long
)