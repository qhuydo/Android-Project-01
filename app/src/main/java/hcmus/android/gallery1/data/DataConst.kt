package hcmus.android.gallery1.data

import android.provider.MediaStore

// Default content URI prefix for fetching files
val DEFAULT_CONTENT_URI = MediaStore.Files.getContentUri("external")


// Default WHERE clause for filtering only image/video items
val SELECTION_ONLY_IMAGES_OR_VIDEO = "" +
        "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}" +
        " OR " +
        "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}"


// Default ORDER BY clause for items in a collection:
// latest file first
val DEFAULT_SORT_ORDER_ITEMS = "" +
        "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"


// Default ORDER BY clause for collections:
// A-Z on Collection Name; latest file first on Collection First URI
// @RequiresApi(Build.VERSION_CODES.Q)
val DEFAULT_SORT_ORDER_COLLECTIONS = "" +
        "${MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME} ASC," +
        "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"


// Constant int->month map
val MAP_INT_TO_MONTH = mapOf(
    1 to "January",
    2 to "February",
    3 to "March",
    4 to "April",
    5 to "May",
    6 to "June",
    7 to "July",
    8 to "August",
    9 to "September",
    10 to "October",
    11 to "November",
    12 to "December"
)
