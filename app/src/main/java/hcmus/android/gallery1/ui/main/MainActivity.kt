package hcmus.android.gallery1.ui.main

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.WindowManager
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
import hcmus.android.gallery1.ui.base.BaseFragment
import hcmus.android.gallery1.ui.collection.list.AlbumViewModel
import hcmus.android.gallery1.ui.collection.list.DateCollectionViewModel
import hcmus.android.gallery1.ui.image.list.AllPhotosViewModel
import hcmus.android.gallery1.ui.image.list.FavouritesViewModel
import timber.log.Timber

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

    // the navigation bar is on the side
    var isSideNavigationBar: Boolean = false
        private set

    var isRightSideNavigationBar: Boolean = false
        private set

    // the navigation bar is at the bottom
    var isBottomNavigationBar: Boolean = false
        private set

    // the height of the navigation bar in pixels
    var navigationBarHeight: Int = 0
        private set

    private var scheduledToRecreate = false

    private val statusBarHeight by lazy {
        statusBarHeight()
    }

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

        // navigation bar
        initNavigationBarProperties()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideFullScreen()

        configNavigationBarColour()

        registerOrientationEventListener()
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
            isSideNavigationBar -> {
//                window?.attributes?.flags?.let {
//                    window?.attributes?.flags =
//                        (it or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
//                                or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                }
//                window?.navigationBarColor = Color.TRANSPARENT
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    window?.decorView?.systemUiVisibility?.let {
//                        window?.decorView?.systemUiVisibility =
//                            it and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
//
//                    }
//                }
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

    private fun initNavigationBarProperties() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val currentDisplay = windowManager.defaultDisplay

        val appUsableSize = Point()
        val realScreenSize = Point()
        currentDisplay?.apply {
            getSize(appUsableSize)
            getRealSize(realScreenSize)
        }

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            navigationBarHeight = realScreenSize.x - appUsableSize.x
            isSideNavigationBar = true

            if (windowManager.defaultDisplay.rotation == Surface.ROTATION_90) {
                isRightSideNavigationBar = true
            }
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            navigationBarHeight = realScreenSize.y - appUsableSize.y
            isBottomNavigationBar = true
        }

    }

    private fun registerOrientationEventListener() {
        orientationEventListener = object : OrientationEventListener(this) {

            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return

                if (scheduledToRecreate) return

                val rotation = windowManager.defaultDisplay.rotation
                val application = application as GalleryOneApplication
                if (application.lastRotation != rotation) {

                    // TODO: add comment
                    if (application.lastRotation.isHorizontalRotation()
                        && rotation.isHorizontalRotation()
                    ) {
                        Timber.i("Rotation = ${rotation}, recreate()}")
                        // mainFragment.collapseDrawer()
                        recreate()
                        scheduledToRecreate = true
                    }

                    application.lastRotation = rotation
                }

            }
        }
        if (orientationEventListener?.canDetectOrientation() == true) {
            orientationEventListener?.enable()
        }
    }

    @Deprecated("Use View.applySystemWindowInsetsPadding instead")
    fun setViewPaddingWindowInset(view: View) {
        setViewPaddingInNavigationBarSide(view)
        setViewPaddingInStatusBarSide(view)
    }

    @Deprecated("Use View.applySystemWindowInsetsPadding instead")
    fun setViewPaddingInNavigationBarSide(
        view: View,
        usePaddingBottomNavigationBar: Boolean = true,
        usePaddingRightSideNavigationBar: Boolean = true,
        usePaddingLeftSideSideNavigationBar: Boolean = true,
    ) {
        val px = navigationBarHeight
        var left = view.paddingLeft
        var right = view.paddingRight
        val top = view.paddingTop
        var bottom = view.paddingBottom

        when {
            isBottomNavigationBar && usePaddingBottomNavigationBar -> bottom = px
            isRightSideNavigationBar && usePaddingRightSideNavigationBar -> right = px
            isSideNavigationBar && usePaddingLeftSideSideNavigationBar -> left = px
        }

        view.padding(left, top, right, bottom)
    }

    @Deprecated("Use View.applySystemWindowInsetsPadding instead")
    fun setViewPaddingInStatusBarSide(view: View?) = view?.let {
        val px = statusBarHeight
        view.setPadding(view.paddingLeft, px, view.paddingRight, view.paddingBottom)
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