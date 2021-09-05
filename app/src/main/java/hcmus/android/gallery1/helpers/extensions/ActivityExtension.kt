package hcmus.android.gallery1.helpers.extensions

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.bumptech.glide.manager.SupportRequestManagerFragment
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

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
    overridePendingTransition( 0, 0)
    startActivity(intent?.apply {
        addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    })
}


@Suppress("DEPRECATION")
fun AppCompatActivity.hideFullScreen() {
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

@Suppress("DEPRECATION")
fun AppCompatActivity.showFullScreen() {
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