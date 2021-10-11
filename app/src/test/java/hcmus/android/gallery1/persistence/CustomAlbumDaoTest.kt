package hcmus.android.gallery1.persistence

import android.database.sqlite.SQLiteException
import hcmus.android.gallery1.data.CustomAlbumCrossRef
import hcmus.android.gallery1.data.CustomAlbumInfo
import hcmus.android.gallery1.data.CustomAlbumItem
import hcmus.android.gallery1.persistent.CustomAlbumDao
import hcmus.android.gallery1.utilities.testCustomAlbum1
import hcmus.android.gallery1.utilities.testCustomAlbum2
import hcmus.android.gallery1.utilities.testCustomAlbums
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class CustomAlbumDaoTest : LocalDatabase() {
    lateinit var dao: CustomAlbumDao

    @Before
    fun initDao() {
        runBlocking {
            dao = db.customAlbumDao
            testCustomAlbums.forEach { dao.insert(it) }
        }
    }

    @Test
    fun getCustomAlbum() = runBlocking {
        val albums = dao.getAllAlbums()
        assertThat(albums.size, equalTo(2))

        assertThat(albums.sumOf { it.albumItems.size }, equalTo(6))
        assertThat(albums[0], equalTo(testCustomAlbum1))
        assertThat(albums[1], equalTo(testCustomAlbum2))
    }

    @Test
    fun getAllAlbumIds() = runBlocking {
        val albumIds = dao.allAlbumIds()

        assertThat(albumIds.size, equalTo(2))
        assertThat(albumIds[0], equalTo(1L))
        assertThat(albumIds[1], equalTo(2L))
    }

    @Test
    fun insertCustomAlbum() = runBlocking {
        val newAlbum = CustomAlbumInfo(name = "new_album")
        val albumId = dao.insertInfo(newAlbum)

        assertThat(dao.getAllAlbums().size, equalTo(3))
    }

    @Test
    fun insertCustomAlbum_nameExisted() {
        runBlocking {
            // same name with testCustomAlbum1
            val newAlbum = CustomAlbumInfo(name = "a")

            val albumId = dao.insertInfo(newAlbum)
            val items = dao.getAllAlbums().firstOrNull { it.albumInfo.name == "a" }?.albumItems
            assertThat(items?.size, equalTo(3))
        }
    }

    @Test
    fun insertItemIntoAlbum() = runBlocking {
        val newItem = CustomAlbumItem(id = System.currentTimeMillis())
        val albumId = dao.getAllAlbums().first().albumInfo.id
        dao.insertItemIntoAlbum(newItem, albumId)

        assertThat(dao.getAllAlbums().first().albumItems.size, equalTo(4))
    }

    @Test
    fun insertItemIntoAlbum_hasConflict() = runBlocking {
        val duplicatedItem = CustomAlbumItem(id = 1L)
        val albumId = dao.getAllAlbums().first().albumInfo.id
        dao.insertItemIntoAlbum(duplicatedItem, albumId)

        assertThat(dao.getAllAlbums().first().albumItems.size, equalTo(3))
    }

    @Test
    fun insertItemIntoManyAlbums() = runBlocking {
        val newItem = CustomAlbumItem(id = System.currentTimeMillis())
        val albumIds = dao.allAlbumIds()

        dao.insertItemIntoAlbum(newItem, albumIds)
        val albums = dao.getAllAlbums()
        assertThat(albums.sumOf { it.albumItems.size }, equalTo(8))
        assertTrue(albums[0].albumItems.contains(newItem))
        assertTrue(albums[1].albumItems.contains(newItem))
    }

    @Test
    fun insertItemIntoManyAlbums_hasConflict() = runBlocking {
        val existedItem = CustomAlbumItem(1L)
        val albumIds = dao.allAlbumIds()

        assertThat(
            dao.getAllAlbums().first().albumItems.contains(existedItem),
            equalTo(true)
        )

        dao.insertItemIntoAlbum(existedItem, albumIds)
        val albums = dao.getAllAlbums()

        assertThat(albums.sumOf { it.albumItems.size }, equalTo(7))

        assertTrue(albums[0].albumItems.contains(existedItem))
        assertTrue(albums[1].albumItems.contains(existedItem))
    }

    @Test
    fun insertManyItemsIntoManyAlbums() = runBlocking {
        val newItems = listOf(
            CustomAlbumItem(id = System.currentTimeMillis()),
            CustomAlbumItem(id = System.currentTimeMillis() + 1),
        )
        val albumIds = dao.allAlbumIds()

        dao.insertItemIntoAlbum(newItems, albumIds)
        val albums = dao.getAllAlbums()
        assertThat(albums.sumOf { it.albumItems.size }, equalTo(10))
        assertTrue(albums[0].albumItems.containsAll(newItems))
        assertTrue(albums[1].albumItems.containsAll(newItems))
    }

    @Test
    fun insertManyItemsIntoManyAlbums_hasConflicts() = runBlocking {
        val existedItems = listOf(
            /** item existed in [testCustomAlbum1] */
            CustomAlbumItem(id = 1L),
            /** item existed in [testCustomAlbum2] */
            CustomAlbumItem(id = 4L),
        )

        val albumListBeforeInsertion = dao.getAllAlbums()
        assertTrue(albumListBeforeInsertion[0].albumItems.contains(existedItems[0]))
        assertTrue(albumListBeforeInsertion[1].albumItems.contains(existedItems[1]))


        val albumIds = dao.allAlbumIds()
        dao.insertItemIntoAlbum(existedItems, albumIds)

        val albums = dao.getAllAlbums()
        assertThat(albums.sumOf { it.albumItems.size }, equalTo(8))
        assertTrue(albums[0].albumItems.containsAll(existedItems))
        assertTrue(albums[1].albumItems.containsAll(existedItems))
    }

    @Test
    fun insertCustomAlbumCrossRef_invalid() {
        runBlocking {
            val customAlbumCrossRef =
                CustomAlbumCrossRef(albumId = 0L, itemId = System.currentTimeMillis())

            assertThrows(SQLiteException::class.java) {
                runBlocking {
                    dao.insertCustomAlbumCrossRef(customAlbumCrossRef)
                }
            }
        }
    }

    @Test
    fun removeAnAlbum() = runBlocking {
        val albumListBeforeDeletion = dao.getAllAlbums()
        assertThat(albumListBeforeDeletion.size, equalTo(2))

        dao.deleteAlbum(albumListBeforeDeletion[0].albumInfo)

        val albumList = dao.getAllAlbums()
        assertThat(albumList.size, equalTo(1))
        assertThat(albumList.sumOf { it.albumItems.size }, equalTo(3))

        assertFalse(dao.containsItemInAlbum(1L, 1L))
        assertFalse(dao.containsItemInAlbum(2L, 1L))
        assertFalse(dao.containsItemInAlbum(3L, 1L))
    }

    @Test
    fun removeAnAlbum_notExist() = runBlocking {
        val albumListBeforeDeletion = dao.getAllAlbums()
        assertThat(albumListBeforeDeletion.size, equalTo(2))

        dao.deleteAlbum(CustomAlbumInfo(id = System.currentTimeMillis(), name = "not_exist"))

        val albumList = dao.getAllAlbums()
        assertThat(albumList.size, equalTo(2))
    }

    @Test
    fun removeAnItem() = runBlocking {
        val item = CustomAlbumItem(id = System.currentTimeMillis())
        var albumList = dao.getAllAlbums()
        dao.insertItemIntoAlbum(item, albumList.map { it.albumInfo.id })

        dao.getAllAlbums().forEach {
            assertTrue(dao.containsItemInAlbum(item.id, it.albumInfo.id))
        }

        dao.deleteAlbumItem(item)
        albumList = dao.getAllAlbums()
        albumList.forEach {
            assertFalse(dao.containsItemInAlbum(item.id, it.albumInfo.id))
        }

    }

    @Test
    fun removeManyItemsFromAnAlbum() = runBlocking {
        val itemsToRemoved = listOf(1L, 3L, 5L)

        dao.deleteItemFromAlbum(itemsToRemoved, 1L)
        val albums = dao.getAllAlbums()
        val album1 = albums[0]
        val album2 = albums[1]

        assertThat(album1.albumItems.size, equalTo(1))
        assertThat(album2.albumItems.size, equalTo(3))

        assertFalse(album1.albumItems.contains(CustomAlbumItem(1L)))
        assertFalse(album1.albumItems.contains(CustomAlbumItem(3L)))
        assertFalse(album1.albumItems.contains(CustomAlbumItem(5L)))

        assertTrue(album2.albumItems.contains(CustomAlbumItem(5L)))
    }

}