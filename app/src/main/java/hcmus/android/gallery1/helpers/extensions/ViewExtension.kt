package hcmus.android.gallery1.helpers.extensions

import android.view.View

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