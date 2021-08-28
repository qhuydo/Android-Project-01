package hcmus.android.gallery1.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "custom_album_cross_ref",
    primaryKeys = ["album_id", "item_id"],
    foreignKeys = [
        ForeignKey(
            entity = CustomAlbumInfo::class,
            parentColumns = ["id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CustomAlbumItem::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CustomAlbumCrossRef(
    @ColumnInfo(name = "album_id")
    val albumId: Long,

    // Id of the MediaItem
    // retrieved from BaseColumns._ID column in MediaStore
    @ColumnInfo(name = "item_id", index = true)
    val itemId: Long
)