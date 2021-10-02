package hcmus.android.gallery1.data

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import hcmus.android.gallery1.data.DataSource.Companion.DEFAULT_CONTENT_URI
import hcmus.android.gallery1.helpers.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val id: Long,

    // Lazy-load fields
    private var uri: String = "",
    var dateModified: Long = 0,
    var fileName: String = "",
    var fileSize: Long = 0,
    var filePath: String = "",
    var width: Int = 0,
    var height: Int = 0,
    private val mimeType: String?
) : Parcelable {

    fun getType() = when (mimeType) {
        in imageMimeTypes -> ItemType.STATIC_IMAGE
        in videoMimeTypes -> ItemType.VIDEO
        in gifMimeTypes -> ItemType.GIF
        in svgMimeTypes -> ItemType.SVG
        else -> ItemType.UNKNOWN
    }

    fun isExifImage() = getType() == ItemType.STATIC_IMAGE && mimeType in exifMimeTypes

    // Fetch URI
    fun getUri(): String {
        if (uri.isEmpty()) {
            uri = "$DEFAULT_CONTENT_URI/$id"
        }
        return uri
    }

    companion object {

        val diffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}

enum class ItemType {
    STATIC_IMAGE,
    VIDEO,
    GIF,
    SVG,
    UNKNOWN
}
