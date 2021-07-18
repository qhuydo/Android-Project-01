package hcmus.android.gallery1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.preference.PreferenceManager
import hcmus.android.gallery1.fragments.MainFragment
import hcmus.android.gallery1.helpers.LANG_FOLLOW_SYSTEM
import hcmus.android.gallery1.helpers.PreferenceFacility
import java.util.*

lateinit var globalFragmentManager: FragmentManager
lateinit var globalPrefs: PreferenceFacility
lateinit var globalContext: Context

const val PERMISSION_REQUEST_CODE = 100

class Activity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Late init but quite early
        globalFragmentManager = supportFragmentManager
        //globalPrefs = PreferenceFacility(getPreferences(MODE_PRIVATE))
        globalPrefs = PreferenceFacility(
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        globalContext = applicationContext

        // Theme and language
        setTheme(globalPrefs.themeR)
        setLanguageOnActivityRestart()

        // Layout
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)
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

        // Insert first piece of fragment
        globalFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container, MainFragment())
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
        if (globalFragmentManager.backStackEntryCount > 0) {
            globalFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    // https://stackoverflow.com/a/2900144
    fun setLanguageOnActivityRestart() {
        val newConfig = resources.configuration
        val languageOption = globalPrefs.language
        if (languageOption != LANG_FOLLOW_SYSTEM) {
            newConfig.setLocale(Locale(languageOption.toLowerCase(Locale.ROOT)))
            resources.updateConfiguration(newConfig, resources.displayMetrics)
        }
    }

    fun changeLanguage(lang: String) {
        if (lang in globalPrefs.validLanguages && globalPrefs.language != lang) {
            globalPrefs.language = lang
            restartSelf()
        }
    }

    fun changeTheme(theme: String) {
        if (theme in globalPrefs.validThemes && globalPrefs.theme != theme) {
            globalPrefs.theme = theme
            restartSelf()
        }
    }

    private fun restartSelf() {
        finish()
        startActivity(Intent(this, this::class.java))
    }
}
