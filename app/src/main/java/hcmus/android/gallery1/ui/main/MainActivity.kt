package hcmus.android.gallery1.ui.main

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.OrientationEventListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.commit
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import hcmus.android.gallery1.GalleryOneApplication
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.DataSource
import hcmus.android.gallery1.databinding.ActivityMainBinding
import hcmus.android.gallery1.helpers.MATERIAL_3
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.persistent.AppDatabase.Companion.getDatabaseInstance
import hcmus.android.gallery1.repository.CollectionRepositoryImpl
import hcmus.android.gallery1.repository.CustomAlbumRepositoryImpl
import hcmus.android.gallery1.repository.FavouriteRepositoryImpl
import hcmus.android.gallery1.repository.PhotoRepositoryImpl
import hcmus.android.gallery1.ui.adapters.binding.doOnApplyWindowInsets
import hcmus.android.gallery1.ui.base.BaseFragment
import hcmus.android.gallery1.ui.collection.list.AlbumViewModel
import hcmus.android.gallery1.ui.collection.list.DateCollectionViewModel
import hcmus.android.gallery1.ui.image.list.AllPhotosViewModel
import hcmus.android.gallery1.ui.image.list.FavouritesViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mainFragment: MainFragment

    private val mediaStoreSource by lazy { DataSource.getInstance(applicationContext) }
    private val database by lazy { application.getDatabaseInstance() }
    val favouriteRepository by lazy {
        FavouriteRepositoryImpl.getInstance(
            mediaStoreSource,
            database.favouriteDao
        )
    }
    val photoRepository by lazy { PhotoRepositoryImpl.getInstance(mediaStoreSource) }
    val collectionRepository by lazy { CollectionRepositoryImpl.getInstance(mediaStoreSource) }
    val preferenceRepository by lazy { (application as GalleryOneApplication).preferenceRepository }
    val customAlbumRepository by lazy {
        CustomAlbumRepositoryImpl.getInstance(
            database.customAlbumDao,
            mediaStoreSource
        )
    }

    val orientation by lazy { resources.configuration.orientation }

    internal val mainViewModel by viewModels<MainViewModel> {
        MainViewModel.Factory(
            application,
            customAlbumRepository
        )
    }

    // the height of the navigation bar in pixels
    var navigationBarHeight: Int = 0
        private set

    private val windowInsetsController by lazy {
        WindowCompat.getInsetsController(window, binding.root)
    }

    private var orientationEventListener: OrientationEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasReadExternalPermission() || !hasWriteExternalPermission()) {
            toStartActivity()
            finish()
        }

        setUpUi()

        // Set animations between fragments
        /* globalFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,    // enter
                R.anim.fade_out,    // exit
                R.anim.fade_in,     // pop_enter
                R.anim.slide_out    // pop_exit
            )
        } */

        if (savedInstanceState == null) {
            // Insert first piece of fragment
            addMainFragment()
        } else {
            findMainFragment()
        }
        initViewModel()
    }

    private fun findMainFragment() {
        mainFragment = supportFragmentManager.findFragmentByTag(MainFragment::class.java.name)
                as MainFragment
    }

    private fun addMainFragment() {
        mainFragment = MainFragment()
        supportFragmentManager.commit {
            val tag = MainFragment::class.java.name
            add(R.id.fragment_container, mainFragment, tag)
            // addToBackStack(tag)
        }
    }

    private fun setUpUi() {
        // Theme and language
        configLanguage(preferenceRepository.locale)

        // Layout
        // supportActionBar?.hide()
        setTheme(preferenceRepository.themeR)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.doOnApplyWindowInsets { view, insetsCompat, _, _, _ ->
            val insets = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            navigationBarHeight = insets.bottom
        }

        // window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideFullScreen()

        configNavigationBarColour()
    }

    private fun configNavigationBarColour() {
        when {
            preferenceRepository.materialVersion == MATERIAL_3 -> {
                DynamicColors.applyIfAvailable(this)
                val surface3 = SurfaceColors.SURFACE_3.getColor(this)
                window?.navigationBarColor = surface3
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    window?.navigationBarDividerColor = surface3
                }
            }
        }
    }

    private fun initViewModel() {
        val favouritesViewModel by viewModels<FavouritesViewModel> {
            FavouritesViewModel.Factory(favouriteRepository, preferenceRepository)
        }
        val allItemViewModel by viewModels<AllPhotosViewModel> {
            AllPhotosViewModel.Factory(photoRepository, preferenceRepository)
        }
        val albumViewModel by viewModels<AlbumViewModel> {
            AlbumViewModel.Factory(
                collectionRepository,
                customAlbumRepository,
                preferenceRepository
            )
        }
        val dateViewModel by viewModels<DateCollectionViewModel> {
            DateCollectionViewModel.Factory(collectionRepository, preferenceRepository)
        }
        favouritesViewModel.init()
        allItemViewModel.init()
        albumViewModel.init()
        dateViewModel.init()
    }

    override fun onBackPressed() {
        val currentFragment = getCurrentFragment()
        if ((currentFragment as? BaseFragment<*>)?.onBackPressed() == true) {
            return
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configLanguage(preferenceRepository.locale)
        configTheme(preferenceRepository.theme)
    }

    override fun onDestroy() {
        orientationEventListener?.disable()
        super.onDestroy()
    }

    fun changeLanguage(lang: String) {
        if (lang != preferenceRepository.language) {
            preferenceRepository.language = lang
            restartSelf()
        }
    }

    fun changeTheme(theme: String, materialVersion: String) {
        val shouldRestart = (theme != preferenceRepository.theme
                || materialVersion != preferenceRepository.materialVersion)

        preferenceRepository.theme = theme
        preferenceRepository.materialVersion = materialVersion

        if (shouldRestart) {
            restartSelf()
        }
    }

    fun hideFullScreen() {
        windowInsetsController?.show(
            WindowInsetsCompat.Type.systemBars()
        )
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    fun showFullScreen() {
        windowInsetsController?.hide(
            WindowInsetsCompat.Type.systemBars()
        )
    }

}