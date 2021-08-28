package hcmus.android.gallery1.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

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