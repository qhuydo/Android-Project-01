package hcmus.android.gallery1.data

import androidx.room.*

data class CustomAlbum(
    @Embedded val albumInfo: CustomAlbumInfo,

    @Relation(
        parentColumn = "id", // `id` column from `custom_album_info`
        entityColumn = "id", // `id` column from `custom_album_item`
        associateBy = Junction(
            CustomAlbumCrossRef::class,
            parentColumn = "album_id",
            entityColumn = "item_id"
        ),
        entity = CustomAlbumItem::class,
    )
    val albumItems: List<CustomAlbumItem>
)

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

@Entity(tableName = "custom_album_info")
data class CustomAlbumInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    val name: String,

    @ColumnInfo(name = "date_created")
    var dateCreated: Long = System.currentTimeMillis(),

    //
    // @ColumnInfo(name = "date_modified")
    // var dateModified: Long = System.currentTimeMillis(),

    // Id of the MediaItem to be used as a thumbnail of the album
    // retrieved from BaseColumns._ID column in MediaStore
    @ColumnInfo(name = "thumbnail_id")
    var thumbnailId: Long = -1 // -1: not assigning value yet
)

@Entity(tableName = "custom_album_item")
data class CustomAlbumItem(
    // Id of the MediaItem
    // retrieved from BaseColumns._ID column in MediaStore
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
)