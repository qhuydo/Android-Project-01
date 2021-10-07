package hcmus.android.gallery1.helpers.extensions

import android.os.Build
import android.view.View
import android.view.animation.DecelerateInterpolator
import hcmus.android.gallery1.helpers.ALPHA_VISIBLE

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.show(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

fun View.isVisible() = visibility == View.VISIBLE
fun View.isGone() = visibility == View.GONE

fun View.padding(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    val l = left ?: paddingLeft
    val t = top ?: paddingTop
    val r = right ?: paddingRight
    val b = bottom ?: paddingBottom

    setPadding(l, t, r, b)
}

fun View?.animateFadeUp() = this?.apply{
    // val animTime = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    val animTime = 100L
    alpha = 0.5f
    translationY = 32f

    animate()
        .translationY(0f)
        .setInterpolator(DecelerateInterpolator())
        .alpha(ALPHA_VISIBLE)
        .apply { duration = animTime }

}

@Suppress("DEPRECATION")
fun setLightStatusBar(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        view.systemUiVisibility = view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

@Suppress("DEPRECATION")
fun unsetLightStatusBar(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        view.systemUiVisibility = view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

@Suppress("DEPRECATION")
fun View.isLightStatusBar(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR > 0
    }
    return true
}
