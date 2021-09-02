package hcmus.android.gallery1.data

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import hcmus.android.gallery1.data.DataSource.Companion.DEFAULT_CONTENT_URI
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val id: Long,
    var isPopulated: Boolean = false,

    // Lazy-load fields
    private var uri: String = "",
    var dateModified: Long = 0,
    var fileName: String = "",
    var fileSize: Long = 0,
    var filePath: String = "",
    var width: Int = 0,
    var height: Int = 0
) : Parcelable {
    // Fetch URI
    fun getUri(): String {
        if (uri.isEmpty()) {
            uri = "$DEFAULT_CONTENT_URI/$id"
        }
        return uri
    }

    // Populate all other fields in this Item itself
    fun populate() {
        if (!isPopulated) {
            isPopulated = true
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Item> () {
            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}
