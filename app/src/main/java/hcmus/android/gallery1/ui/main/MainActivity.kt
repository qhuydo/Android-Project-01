package hcmus.android.gallery1.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.preference.PreferenceManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.ui.splash.SplashActivity
import hcmus.android.gallery1.databinding.ActivityMainBinding
import hcmus.android.gallery1.helpers.LANG_FOLLOW_SYSTEM
import hcmus.android.gallery1.helpers.PreferenceFacility
import hcmus.android.gallery1.helpers.configTheme
import hcmus.android.gallery1.ui.collection.ViewCollectionFragment
import hcmus.android.gallery1.ui.image.ViewImageFragment
import java.util.*

lateinit var globalPrefs: PreferenceFacility

const val PERMISSION_REQUEST_CODE = 100

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainFragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        //globalPrefs = PreferenceFacility(getPreferences(MODE_PRIVATE))
        globalPrefs = PreferenceFacility(
            PreferenceManager.getDefaultSharedPreferences(this)
        )

        configTheme(globalPrefs, null)
        // Theme and language
        setTheme(globalPrefs.themeR)
        setLanguageOnActivityRestart()

        // Layout
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO Bottom drawer

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
        }
        else {
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
        // super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
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
        }
        else {
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
    fun setLowProfileUI(isLowProfile: Boolean) {
        val flag = this.window?.decorView?.systemUiVisibility
        flag?.let {
            if (isLowProfile) {
                this.window?.decorView?.systemUiVisibility = flag or View.SYSTEM_UI_FLAG_LOW_PROFILE
            } else {
                this.window?.decorView?.systemUiVisibility =
                    flag or View.SYSTEM_UI_FLAG_LAYOUT_STABLE and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
            }
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
}
