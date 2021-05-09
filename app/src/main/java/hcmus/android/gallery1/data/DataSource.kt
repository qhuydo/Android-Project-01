 package hcmus.android.gallery1.data

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.annotation.RequiresApi

/**
 * Parameters for ContentResolver.query() (it's essentially a SQL cursor)
 *     - Content URI
 *     - Projection (columns to choose)
 *     - Selection (the "WHERE" clause)
 *     - Selection arguments (leave it null for now)
 *     - Sort order (the "ORDER BY" clause)
 */

fun ContentResolver.getCollections() : List<Collection> {
    val r : MutableSet<Collection> = mutableSetOf()

    val msCursor = this.query(
        DEFAULT_CONTENT_URI,
        arrayOf(MediaStore.Files.FileColumns.BUCKET_ID, MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME),
        SELECTION_ONLY_IMAGES_OR_VIDEO,
        null,
        DEFAULT_SORT_ORDER_COLLECTIONS
    )

    msCursor?.use {
        while (it.moveToNext()) {
            r += Collection(
                id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)),
                name = it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME))
            )
        }
    }

    return r.toList()
}

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
        arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME),
        customSelection,
        null,
        DEFAULT_SORT_ORDER_ITEMS
    )

    msCursor?.use {
        while (it.moveToNext()) {
            val c = Item(
                id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)),
                fileName = it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
            )
            c.getUri()
            r += c
        }
    }

    return r.toList()
}

/********************************************************************************/

/*
fun ContentResolver.getAllItems() : List<Item> {
    val r : MutableList<Item> = mutableListOf()

    val msCursor = this.query(
        DEFAULT_CONTENT_URI,
        arrayOf(BaseColumns._ID),
        SELECTION_ONLY_IMAGES_OR_VIDEO,
        null,
        DEFAULT_SORT_ORDER_ITEMS
    )

    msCursor?.use {
        val idColIndex = it.getColumnIndexOrThrow(BaseColumns._ID)
        while (it.moveToNext()) {
            r += Item(it.getLong(idColIndex))
        }
    }

    return r
}

fun ContentResolver.getOneItem(id: Long) : Item? {
    val msCursor = this.query(
        MediaStore.Files.getContentUri("external"),

        // no WHERE clause, since we're getting it all.
        arrayOf(
            BaseColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME
        ),

        "(" +
                SELECTION_CLAUSE_ONLY_IMAGES_OR_VIDEO +
                ") AND (" +
                "${BaseColumns._ID} = $id" +
                ")",

        null,

        "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
    )

    msCursor?.use {
        val colIndexID = it.getColumnIndexOrThrow(BaseColumns._ID)
        val colIndexFilename = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)

        while (it.moveToNext())
        {
            return Item(
                id = it.getLong(colIndexID),
                name = it.getString(colIndexFilename),
                uri = ContentUris.withAppendedId(
                    MediaStore.Files.getContentUri("external"),
                    it.getLong(colIndexID)
                ).toString()
            )
        }
    }

    return null
}

fun ContentResolver.getAllCollections(): List<Collection> {
    val r : MutableSet<Collection> = mutableSetOf()

    val msCursor = this.query(
        MediaStore.Files.getContentUri("external"),

        // no WHERE clause, since we're getting it all.
        arrayOf(
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
        ),

        SELECTION_CLAUSE_ONLY_IMAGES_OR_VIDEO,

        null,

        "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
    )

    msCursor?.use {
        val colIndexBucketID = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)
        val colIndexBucketName = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)

        while (it.moveToNext()) {
            r += Collection(
                id = it.getLong(colIndexBucketID),
                name = it.getString(colIndexBucketName)
            )
        }
    }

    return r.toList()
}

fun ContentResolver.getOneCollection(bucketId: Long) : List<Item> {
    val r : MutableList<Item> = mutableListOf()

    val msCursor = this.query(
        MediaStore.Files.getContentUri("external"),
        arrayOf(BaseColumns._ID),
        "${MediaStore.Files.FileColumns.BUCKET_ID} = ? AND $SELECTION_CLAUSE_ONLY_IMAGES_OR_VIDEO",
        null,
        "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
    )

    msCursor?.use {
        val idColIndex = it.getColumnIndexOrThrow(BaseColumns._ID)
        while (it.moveToNext()) {
            r += Item(it.getLong(idColIndex))
        }
    }

    return r
}
*/