package hcmus.android.gallery1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Collection(
    val id: Long,
    val name: String,
    val type: String,
    val thumbnailUri: String,
    var itemCount: Int,

    // Lazy-load fields
    var items: List<Item> = emptyList()
) : Parcelable {

    companion object {
        const val TYPE_ALBUM = "album"
        const val TYPE_DATE = "date"
    }
}
