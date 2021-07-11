package hcmus.android.gallery1.data

import android.content.ContentResolver
import android.provider.MediaStore
import hcmus.android.gallery1.globalPrefs
import java.util.*
import androidx.core.database.getIntOrNull

 /**
 * Parameters for ContentResolver.query() (it's essentially a SQL cursor)
 *     - Content URI
 *     - Projection (columns to choose)
 *     - Selection (the "WHERE" clause)
 *     - Selection arguments (leave it null for now)
 *     - Sort order (the "ORDER BY" clause)
 */

// Get all collections by name (default) or by date
fun ContentResolver.getCollections() : List<Collection> {
    val r : MutableSet<Collection> = mutableSetOf()

     val msCursor = this.query(
         DEFAULT_CONTENT_URI,
         arrayOf(
             MediaStore.Files.FileColumns._ID,
             MediaStore.Files.FileColumns.BUCKET_ID,
             MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
         ),
         SELECTION_ONLY_IMAGES_OR_VIDEO,
         null,
         DEFAULT_SORT_ORDER_COLLECTIONS
     )

     msCursor?.use {
         while (it.moveToNext()) {
             val bucketId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID))
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
             }
             else {
                 val bucketDisplayName = it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME))
                 val bucketThumbnailUri = "$DEFAULT_CONTENT_URI/${it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))}"
                 r += Collection(
                     id = bucketId,
                     name = bucketDisplayName,
                     thumbnailUri = bucketThumbnailUri,
                     itemCount = 1,
                     type = "album"
                 )
             }
         }
     }

    return r.toList()
}

// Get collections for tab "Date"
fun ContentResolver.getCollectionsByDate() : List<Collection> {
    val r : MutableSet<Collection> = mutableSetOf()

    val msCursor = this.query(
        DEFAULT_CONTENT_URI,
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATE_MODIFIED,

        ),
        SELECTION_ONLY_IMAGES_OR_VIDEO,
        null,
        DEFAULT_SORT_ORDER_COLLECTIONS
    )

    msCursor?.use {
        while (it.moveToNext()) {
            // Get month and year from field DATE_MODIFIED
            // Log.e("SAMPLE", "${it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))}")
            val rawDate = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED))
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
            }
            else {
                val bucketDisplayName = "${rawYear}, ${MAP_INT_TO_MONTH[rawMonth + 1]}"
                val bucketThumbnailUri = "$DEFAULT_CONTENT_URI/${it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))}"
                r += Collection(
                    id = bucketId,
                    name = bucketDisplayName,
                    thumbnailUri = bucketThumbnailUri,
                    itemCount = 1,
                    type = "date"
                )
            }
        }
    }

    return r.toList()
}

// Get items (images/videos) in a collection
fun ContentResolver.getItems(collectionId: Long? = null) : List<Item> {
    val r : MutableSet<Item> = mutableSetOf()

    val customSelection = if (collectionId != null) {
        "($SELECTION_ONLY_IMAGES_OR_VIDEO) AND (${MediaStore.Files.FileColumns.BUCKET_ID} = $collectionId)"
    }
    else {
        SELECTION_ONLY_IMAGES_OR_VIDEO
    }

    val msCursor = this.query(
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
    )

    msCursor?.use {
        while (it.moveToNext()) {
            val c = Item(
                id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
                dateModified=it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)),
                fileName = it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)),
                filePath = it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)),
                fileSize = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
            )
            c.getUri()
            r += c
        }
    }

    return r.toList()
}

// Get items grouped by date
fun ContentResolver.getItemsByDate(id: Long? = null) : List<Item> {
    val r : MutableSet<Item> = mutableSetOf()

    // Convert given "id" to year and month
    val targetCal = GregorianCalendar.getInstance().apply {
        clear()
        if (id != null) { timeInMillis = id * 1000 }
    }
    val targetYear = targetCal.get(Calendar.YEAR)
    val targetMonth = targetCal.get(Calendar.MONTH)

    val msCursor = this.query(
        DEFAULT_CONTENT_URI,
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        ),
        SELECTION_ONLY_IMAGES_OR_VIDEO,
        null,
        DEFAULT_SORT_ORDER_ITEMS
    )

    msCursor?.use {
        while (it.moveToNext()) {
            // Convert timestamp to year and month
            val currentCal = GregorianCalendar.getInstance().apply {
                clear()
                timeInMillis = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)) * 1000
            }
            val currentYear = currentCal.get(Calendar.YEAR)
            val currentMonth = currentCal.get(Calendar.MONTH)

            // Compare, then add if matched
            if (currentYear == targetYear && currentMonth == targetMonth) {
                val c = Item(
                    id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
                    fileName = it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                )
                c.getUri()
                r += c
            }
        }
    }

    return r.toList()
}

// Get favorite items
fun ContentResolver.getFavorites() : List<Item> {
    val r : MutableSet<Item> = mutableSetOf()

    val rawId = globalPrefs.getFavorites()
    for (each in rawId) {
        r += Item(id = each)
    }

    return r.toList()
}
