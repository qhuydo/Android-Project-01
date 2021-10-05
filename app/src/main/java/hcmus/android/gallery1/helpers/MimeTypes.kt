package hcmus.android.gallery1.helpers

import java.util.*

val svgMimeTypes = arrayOf("image/svg+xml")

val imageMimeTypes = arrayOf(
    "image/*",
    "image/jpeg",
    "image/jpg",
    "image/png",
    "image/bmp",
    "image/webp",
)

val videoMimeTypes = arrayOf(
    "video/*",
    "video/mp4",
    "video/x-matroska",
    "video/webm",
    "video/avi"
)

val gifMimeTypes = arrayOf("image/gif")

val exifMimeTypes = arrayOf("image/jpeg", "image/jpg")

private fun checkExtension(path: String?, extensions: Array<String>): Boolean {
    if (path == null) {
        return false
    }
    for (i in extensions) {
        if (path.lowercase(Locale.ROOT).endsWith(i)) {
            return true
        }
    }

    return false
}

fun checkImageMimeType(mimeType: String?): Boolean {
    return mimeType?.contains("image") ?: false
}

fun checkVideoMimeType(mimeType: String?): Boolean {
    return mimeType?.contains("video") ?: false
}