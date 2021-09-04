package hcmus.android.gallery1.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import hcmus.android.gallery1.persistent.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
abstract class LocalDatabase {
    lateinit var db: AppDatabase

    @Before
    fun initDatabase() {
        runBlocking {
            val appContext = getApplicationContext<Context>()
            db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        }
    }

    @After
    fun closeDatabase() {
        db.close()
    }
}