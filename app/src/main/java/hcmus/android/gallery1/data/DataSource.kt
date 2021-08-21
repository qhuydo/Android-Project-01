package hcmus.android.gallery1.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import android.text.format.DateUtils
import hcmus.android.gallery1.ui.main.globalPrefs
import java.util.*


/**
 * Parameters for ContentResolver.query() (it's essentially a SQL cursor)
 *     - Content URI
 *     - Projection (columns to choose)
 *     - Selection (the "WHERE" clause)
 *     - Selection arguments (leave it null for now)
 *     - Sort order (the "ORDER BY" clause)
 */
// Get all collections by name (default) or by date
@SuppressLint("InlinedApi")
fun ContentResolver.getCollections(): List<Collection> {
    val rows: MutableSet<Collection> = mutableSetOf()

    query(
        DEFAULT_CONTENT_URI,
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
        ),
        SELECTION_ONLY_IMAGES_OR_VIDEO,
        null,
        DEFAULT_SORT_ORDER_COLLECTIONS
    )?.use { cursor ->

        val idRow = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val bucketIdRow = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)
        val bucketDisplayRow = cursor.getColumnIndexOrThrow(
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
        )

        while (cursor.moveToNext()) {

            val bucketId = cursor.getLong(bucketIdRow)
            var collectionExists = false

            // Does the current collection exist in the returning set "r"?
            for (each in rows) {
                if (each.id == bucketId) {
                    each.itemCount += 1
                    collectionExists = true
                    break
                }
            }

            // Again, does the current collection exist in the returning set "r"?
            if (collectionExists) {
                continue
            } else {
                val bucketDisplayName = cursor.getString(bucketDisplayRow)
                val id = cursor.getLong(idRow)

                val bucketThumbnailUri = "$DEFAULT_CONTENT_URI/$id"
                rows += Collection(
                    id = bucketId,
                    name = bucketDisplayName,
                    thumbnailUri = bucketThumbnailUri,
                    itemCount = 1,
                    type = Collection.TYPE_ALBUM
                )
            }
        }
    }

    return rows.toList()
}

// Get collections for tab "Date"
fun Context.getCollectionsByDate(): List<Collection> {
    val r: MutableSet<Collection> = mutableSetOf()

    contentResolver.query(
        DEFAULT_CONTENT_URI,
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
        ),

        SELECTION_ONLY_IMAGES_OR_VIDEO,
        null,
        DEFAULT_SORT_ORDER_COLLECTIONS
    )?.use {

        val dateModifiedRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
        val idRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)

        val flags = DateUtils.FORMAT_SHOW_YEAR or
                DateUtils.FORMAT_ABBREV_MONTH or
                DateUtils.FORMAT_NO_MONTH_DAY

        while (it.moveToNext()) {

            // Get month and year from field DATE_MODIFIED
            // Log.e("SAMPLE", "${it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))}")
            val rawDate = it.getLong(dateModifiedRow)
            val rawCal = GregorianCalendar.getInstance().apply {
                clear()
                timeInMillis = rawDate * 1000 // Calendar API works in ms by default, NOT secs
            }
            val rawYear = rawCal.get(Calendar.YEAR)
            val rawMonth = rawCal.get(Calendar.MONTH)

            // Trim to just year and month, and set that as new bucket ID
            val targetCal = GregorianCalendar.getInstance().apply {
                clear()
                set(Calendar.YEAR, rawYear)
                set(Calendar.MONTH, rawMonth)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val bucketId = targetCal.timeInMillis / 1000

            var collectionExists = false

            // Does the current collection exist in the returning set "r"?
            for (each in r) {
                if (each.id == bucketId) {
                    each.itemCount += 1
                    collectionExists = true
                    break
                }
            }

            // Again, does the current collection exist in the returning set "r"?
            if (collectionExists) {
                continue
            } else {
                val bucketDisplayName = DateUtils.formatDateTime(this, targetCal.timeInMillis, flags)
                // val bucketDisplayName = "${rawYear}, ${MAP_INT_TO_MONTH[rawMonth + 1]}"
                val id = it.getLong(idRow)

                val bucketThumbnailUri = "$DEFAULT_CONTENT_URI/$id"
                r += Collection(
                    id = bucketId,
                    name = bucketDisplayName,
                    thumbnailUri = bucketThumbnailUri,
                    itemCount = 1,
                    type = Collection.TYPE_DATE
                )
            }
        }
    }

    return r.toList()
}


/**
 * Get items (images/videos) in a collection or all items from the device
 */
@SuppressLint("InlinedApi")
fun ContentResolver.getItems(collectionId: Long? = null): List<Item> {
    val r: MutableSet<Item> = mutableSetOf()

    val customSelection = if (collectionId != null) {
        "($SELECTION_ONLY_IMAGES_OR_VIDEO) AND (${MediaStore.Files.FileColumns.BUCKET_ID} = $collectionId)"
    } else {
        SELECTION_ONLY_IMAGES_OR_VIDEO
    }

    query(
        DEFAULT_CONTENT_URI,
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATA
        ),
        customSelection,
        null,
        DEFAULT_SORT_ORDER_ITEMS
    )?.use {

        val idRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val dateModifiedRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
        val nameRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val dataRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        val sizeRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

        while (it.moveToNext()) {
            val c = Item(
                id = it.getLong(idRow),
                dateModified = it.getLong(dateModifiedRow),
                fileName = it.getString(nameRow),
                filePath = it.getString(dataRow),
                fileSize = it.getLong(sizeRow)
            )
            c.getUri()
            r += c
        }
    }

    return r.toList()
}

// Get items grouped by date
fun ContentResolver.getItemsByDate(id: Long? = null): List<Item> {
    val r: MutableSet<Item> = mutableSetOf()

    // Convert given "id" to year and month
    val targetCal = GregorianCalendar.getInstance().apply {
        clear()
        if (id != null) {
            timeInMillis = id * 1000
        }
    }
    val targetYear = targetCal.get(Calendar.YEAR)
    val targetMonth = targetCal.get(Calendar.MONTH)

    query(
        DEFAULT_CONTENT_URI,
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATA
        ),
        SELECTION_ONLY_IMAGES_OR_VIDEO,
        null,
        DEFAULT_SORT_ORDER_ITEMS
    )?.use {
        val idRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val dateModifiedRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
        val nameRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val dataRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        val sizeRow = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

        while (it.moveToNext()) {
            // Convert timestamp to year and month
            val currentCal = GregorianCalendar.getInstance().apply {
                clear()
                timeInMillis = it.getLong(dateModifiedRow) * 1000
            }
            val currentYear = currentCal.get(Calendar.YEAR)
            val currentMonth = currentCal.get(Calendar.MONTH)

            // Compare, then add if matched
            if (currentYear == targetYear && currentMonth == targetMonth) {
                val c = Item(
                    id = it.getLong(idRow),
                    dateModified = it.getLong(dateModifiedRow),
                    fileName = it.getString(nameRow),
                    filePath = it.getString(dataRow),
                    fileSize = it.getLong(sizeRow)
                )
                c.getUri()
                r += c
            }
        }
    }

    return r.toList()
}

// Get favorite items
fun ContentResolver.getFavorites(): List<Item> {
    val r: MutableSet<Item> = mutableSetOf()

    val rawId = globalPrefs.getFavorites()
    for (each in rawId) {
        r += Item(id = each)
    }

    return r.toList()
}
