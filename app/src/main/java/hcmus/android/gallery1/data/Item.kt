package hcmus.android.gallery1.data

import android.content.ContentUris
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import androidx.recyclerview.widget.DiffUtil
import hcmus.android.gallery1.helpers.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val id: Long,

    // Lazy-load fields
    private var uri: Uri? = null,
    val dateModified: Long = 0,
    val fileName: String = "",
    val fileSize: Long = 0,
    val filePath: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val mimeType: String
) : Parcelable {

    fun getType() = when {
        mimeType.checkVideoMimeType() -> ItemType.VIDEO
        mimeType in imageMimeTypes -> ItemType.STATIC_IMAGE
        mimeType in gifMimeTypes -> ItemType.GIF
        mimeType in svgMimeTypes -> ItemType.SVG
        else -> ItemType.UNKNOWN
    }

    // fun isExifImage() = getType() == ItemType.STATIC_IMAGE && mimeType in exifMimeTypes

    // Fetch URI
    fun getUri(): Uri = uri ?: uriFromMimeType().also { uri = it }

    private fun uriFromMimeType(): Uri {
        val contentUri = when {
            mimeType.checkImageMimeType() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mimeType.checkVideoMimeType() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }

        // id = 33
        // MediaStore.Files.getContentUri("external") -> content://media/external/file/33
        // MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> content://media/external/video/media/33
        return ContentUris.withAppendedId(contentUri, id)
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
