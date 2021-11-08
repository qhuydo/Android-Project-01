package hcmus.android.gallery1.helpers.extensions

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import com.vmadalin.easypermissions.EasyPermissions
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.repository.PreferenceRepository.Companion.validTabs
import hcmus.android.gallery1.repository.PreferenceRepository.Companion.validViews
import hcmus.android.gallery1.repository.PreferenceRepository.Companion.validViewsLimited
import hcmus.android.gallery1.ui.start.StartActivity
import java.util.*
import kotlin.math.roundToInt

fun Context.getSpanCountOf(tab: String, viewMode: String): Int {
    if (tab !in validTabs || (viewMode !in validViews && viewMode !in validViewsLimited)) return 1

    return when (viewMode) {
        VIEW_COLLECTION_GRID -> resources.getInteger(R.integer.collection_grid)
        VIEW_ITEM_GRID_L -> resources.getInteger(R.integer.item_grid_l)
        VIEW_ITEM_GRID_M -> resources.getInteger(R.integer.item_grid_m)
        VIEW_ITEM_GRID_S -> resources.getInteger(R.integer.item_grid_s)
        else -> 1
    }
}

/**
 * Get the height of navigation bar
 * Return the height in pixel or 0 when not found the resource identifiers indicating
 * the height of navigation bar
 */
fun Context.navigationBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun Context.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun configTheme(theme: String) {

    when (theme) {

        THEME_DAY -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        THEME_NIGHT -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        else -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}

//private fun configDefaultTheme(uiMode: Int) {
//    when (uiMode and Configuration.UI_MODE_NIGHT_MASK) {
//        Configuration.UI_MODE_NIGHT_NO -> {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//
//        }
//        Configuration.UI_MODE_NIGHT_YES -> {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }
//    }
//}


fun Context.configLanguage(locale: Locale?): Context {
    val config = resources.configuration
    val currentLocale = ConfigurationCompat.getLocales(config)[0]
    val systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]

    val newLocale = locale ?: systemLocale
    return if (newLocale == currentLocale) {
        this
    } else {
        Locale.setDefault(newLocale)
        config.setLocale(newLocale)
        resources.updateConfiguration(config, resources.displayMetrics)
        createConfigurationContext(config)
    }
}

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, resId, duration).show()

fun Context.hasReadExternalPermission(): Boolean =
    EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)

fun Context.hasWriteExternalPermission(): Boolean {
    return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE)
    } else true
}

fun Context.requestReadWriteExternalPermission() {
    val perms = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        listOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )
    } else {
        listOf(READ_EXTERNAL_STORAGE)
    }

    EasyPermissions.requestPermissions(
        host = this as AppCompatActivity,
        rationale = getString(R.string.please_grant_permission),
        requestCode = StartActivity.PERMISSION_REQUEST_CODE,
        *perms.toTypedArray()
    )
}

fun Context.dpToPixel(dp: Int): Int {
    val density = resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}