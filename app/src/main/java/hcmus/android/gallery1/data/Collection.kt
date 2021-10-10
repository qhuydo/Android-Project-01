package hcmus.android.gallery1.data

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Collection(
    val id: Long,
    val name: String,
    val type: String,
    val thumbnailUri: String,
    var itemCount: Int,
    val dateCreated: Long = 0L,

    // Lazy-load fields
    var itemIds: List<Long> = emptyList()
) : Parcelable {

    fun toCustomAlbum() = CustomAlbum(
        CustomAlbumInfo(
            id = id,
            name = name,
            dateCreated = dateCreated,
            thumbnailUri = thumbnailUri
        ),
        albumItems = itemIds.map { id -> CustomAlbumItem(id) }
    )

    companion object {
        const val TYPE_ALBUM = "album"
        const val TYPE_DATE = "date"
        const val TYPE_CUSTOM = "custom"

        val diffCallback = object : DiffUtil.ItemCallback<Collection> () {
            override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
                return oldItem == newItem
            }
        }
    }
}
