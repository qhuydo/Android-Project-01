package hcmus.android.gallery1.persistence

import hcmus.android.gallery1.data.Favourite
import hcmus.android.gallery1.utilities.testFavouriteItem
import hcmus.android.gallery1.persistent.FavouriteDao
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class FavouriteDaoTest: LocalDatabase() {

    private lateinit var favouriteDao: FavouriteDao

    @Before
    fun initDao() {
        runBlocking {
            favouriteDao = db.favouriteDao
            favouriteDao.insert(testFavouriteItem)
        }
    }

    @Test
    fun insertFavourite() = runBlocking {
        val favourite = Favourite(1L)
        favouriteDao.insert(favourite)
        assertThat(favouriteDao.getAll().size, equalTo(2))

        favouriteDao.insert(favourite)
        assertThat(favouriteDao.getAll().size, equalTo(2))
    }

    @Test
    fun insertExistingFavourite() = runBlocking {
        // favourite object has the same id with the existing one in the database,
        // but different dateAdded
        val favourite = testFavouriteItem.copy(dateAdded = System.currentTimeMillis())
        favouriteDao.insert(favourite)

        assertThat(favouriteDao.getAll().first().dateAdded, equalTo(testFavouriteItem.dateAdded))
    }

}