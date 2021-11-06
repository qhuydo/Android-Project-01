package hcmus.android.gallery1.helpers.extensions

import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialSharedAxis

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

fun View.addPadding(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0
) {
    val l = left + paddingLeft
    val t = top + paddingTop
    val r = right + paddingRight
    val b = bottom + paddingBottom

    setPadding(l, t, r, b)
}

fun ViewGroup?.animateFadeUp() = this?.let {
    TransitionManager.endTransitions(it)
    val sharedAxis = MaterialSharedAxis(MaterialSharedAxis.Y, true)
    TransitionManager.beginDelayedTransition(it, sharedAxis)
}