package hcmus.android.gallery1.helpers.extensions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.manager.SupportRequestManagerFragment
import hcmus.android.gallery1.ui.main.MainActivity
import hcmus.android.gallery1.ui.start.StartActivity
import hcmus.android.gallery1.ui.start.StartActivity.Companion.DEFAULT_SETTINGS_REQUEST_CODE

fun AppCompatActivity.getCurrentFragment(): Fragment? {
    val fragmentList = supportFragmentManager.fragments
    val lastFragment = fragmentList.lastOrNull()
    if (lastFragment !is SupportRequestManagerFragment) {
        return lastFragment
    }
    return fragmentList.firstOrNull()
}

fun AppCompatActivity.restartSelf() {
    finish()
    overridePendingTransition(0, 0)
    startActivity(intent?.apply {
        addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    })
}

// Temporarily turn on "lights out" mode for status bar and navigation bar.
// This usually means hiding nearly everything and leaving with only the clock and battery status.
// https://stackoverflow.com/a/44433844
@Suppress("DEPRECATION")
fun AppCompatActivity.setLowProfileUI(isLowProfile: Boolean) {
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

fun AppCompatActivity.toMainActivity() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
}


fun AppCompatActivity.toStartActivity() {
    startActivity(Intent(this, StartActivity::class.java))
    finish()
}

fun AppCompatActivity.goToAppSetting() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addCategory(Intent.CATEGORY_DEFAULT)
        // addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }.also { intent ->
        startActivityForResult(intent, DEFAULT_SETTINGS_REQUEST_CODE)
    }
}
