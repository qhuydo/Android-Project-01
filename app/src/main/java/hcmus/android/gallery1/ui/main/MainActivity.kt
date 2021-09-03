package hcmus.android.gallery1.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.preference.PreferenceManager
import com.bumptech.glide.manager.SupportRequestManagerFragment
import hcmus.android.gallery1.GalleryOneApplication
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.DataSource
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.ActivityMainBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.persistent.AppDatabase.Companion.getDatabaseInstance
import hcmus.android.gallery1.repository.CollectionRepositoryImpl
import hcmus.android.gallery1.repository.FavouriteRepositoryImpl
import hcmus.android.gallery1.repository.PhotoRepositoryImpl
import hcmus.android.gallery1.ui.base.BaseFragment
import hcmus.android.gallery1.ui.image.ViewImageFragment
import hcmus.android.gallery1.ui.splash.SplashActivity
import java.util.*

lateinit var globalPrefs: PreferenceFacility

const val PERMISSION_REQUEST_CODE = 100

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mainFragment: MainFragment

    private val mediaStoreSource by lazy { DataSource.getInstance(applicationContext) }
    private val database by lazy { getDatabaseInstance() }
    val favouriteRepository by lazy { FavouriteRepositoryImpl.getInstance(mediaStoreSource, database.favouriteDao) }
    val photoRepository by lazy { PhotoRepositoryImpl.getInstance(mediaStoreSource) }
    val collectionRepository by lazy { CollectionRepositoryImpl.getInstance(mediaStoreSource) }

    val orientation by lazy { resources.configuration.orientation }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        //globalPrefs = PreferenceFacility(getPreferences(MODE_PRIVATE))
        globalPrefs = PreferenceFacility(
            PreferenceManager.getDefaultSharedPreferences(this)
        )

        // Theme and language
        configTheme(globalPrefs, null)
        setTheme(globalPrefs.themeR)
        setLanguageOnActivityRestart()

        // Layout
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initNavigationBarProperties()
        initSystemUiVisibility()

        registerOrientationEventListener()

        // A really simple check. Part of the permission workaround.
        // (the official method always return Permission Denied, yet the app actually has the permission.)
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
            Toast.makeText(
                this,
                resources.getString(R.string.please_grant_permission),
                Toast.LENGTH_LONG
            ).show()
        }

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
            mainFragment = MainFragment()
            supportFragmentManager.commit {
                add(R.id.fragment_container, mainFragment, MainFragment::class.java.name)
            }
        } else {
            mainFragment = supportFragmentManager.findFragmentByTag(MainFragment::class.java.name)
                    as MainFragment
        }
    }

    /* override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Please restart the app once.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    } */

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

    private fun getCurrentFragment(): Fragment? {
        val fragmentList = supportFragmentManager.fragments
        val lastFragment = fragmentList.lastOrNull()
        if (lastFragment !is SupportRequestManagerFragment) {
            return lastFragment
        }
        return fragmentList.firstOrNull()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configTheme(globalPrefs, newConfig.uiMode)
        recreate()
    }

    // https://stackoverflow.com/a/2900144
    private fun setLanguageOnActivityRestart() {
        val newConfig = resources.configuration
        val languageOption = globalPrefs.language
        val locale = if (languageOption != LANG_FOLLOW_SYSTEM) {
            Locale(languageOption.lowercase())
        } else {
            Locale.getDefault()
        }
        newConfig.setLocale(locale)
        resources.updateConfiguration(newConfig, resources.displayMetrics)
    }

    fun changeLanguage(lang: String) {
        if (lang in PreferenceFacility.validLanguages && globalPrefs.language != lang) {
            globalPrefs.language = lang
            restartSelf()
        }
    }

    fun changeTheme(theme: String) {
        if (theme in PreferenceFacility.validThemes && globalPrefs.theme != theme) {
            globalPrefs.theme = theme
            configTheme(globalPrefs, null)
            restartSelf()
        }
    }

    private fun restartSelf() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    // Temporarily turn on "lights out" mode for status bar and navigation bar.
    // This usually means hiding nearly everything and leaving with only the clock and battery status.
    // https://stackoverflow.com/a/44433844
    @Suppress("DEPRECATION")
    fun setLowProfileUI(isLowProfile: Boolean) {
        val flag = window?.decorView?.systemUiVisibility
        flag?.let {
            if (isLowProfile) {
                window?.decorView?.systemUiVisibility = flag or View.SYSTEM_UI_FLAG_LOW_PROFILE
            } else {
                window?.decorView?.systemUiVisibility = (flag
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv())
            }
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

    private fun initSystemUiVisibility() {
        hideFullScreen()
    }

    private fun registerOrientationEventListener() {
        val orientationEventListener = object : OrientationEventListener(this) {

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
                        Log.i(MainActivity::class.java.name, "Rotation = ${rotation}, recreate()}")
                        // mainFragment.collapseDrawer()
                        recreate()
                        scheduledToRecreate = true
                    }

                    application.lastRotation = rotation
                }

            }
        }
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable()
        }
    }

    @Suppress("DEPRECATION")
    fun showFullScreen() {
        val flag = window?.decorView?.systemUiVisibility
        flag?.let {
            window?.decorView?.systemUiVisibility = (flag
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
        }

    }

    @Suppress("DEPRECATION")
    fun hideFullScreen() {
        val flag = window?.decorView?.systemUiVisibility
        flag?.let {
            window?.decorView?.systemUiVisibility = (flag
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
                    and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
                    and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
                    and View.SYSTEM_UI_FLAG_IMMERSIVE.inv())
        }
    }

    fun pushViewImageFragment(item: Item) {
        val fm = supportFragmentManager
        val bundle = Bundle().apply {
            putParcelable(ViewImageFragment.BUNDLE_ITEM, item)
        }
        val tag = ViewImageFragment::class.java.name
        val fragmentToBeHidden = fm.findFragmentById(R.id.fragment_container)
        fm.commit {
            fragmentToBeHidden?.let { hide(it) }
            add(R.id.fragment_container, ViewImageFragment::class.java, bundle, tag)
            addToBackStack(tag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
    }

    fun setViewPaddingWindowInset(view: View) {
        setViewPaddingInNavigationBarSide(view)
        setViewPaddingInStatusBarSide(view)
    }

    fun setViewPaddingInNavigationBarSide(view: View) {
        val px = navigationBarHeight
        val left = view.paddingLeft
        val right = view.paddingRight
        val top = view.paddingTop
        val bottom = view.paddingBottom

        when {
            isBottomNavigationBar -> view.setPadding(left, top, right, px)
            isRightSideNavigationBar -> view.setPadding(left, top, px, bottom)
            isSideNavigationBar -> view.setPadding(px, top, right, bottom)
        }
    }

    private fun setViewPaddingInStatusBarSide(view: View) {
        val px = statusBarHeight
        view.setPadding(view.paddingLeft, px, view.paddingRight, view.paddingBottom)
    }
}