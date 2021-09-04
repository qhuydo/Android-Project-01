package hcmus.android.gallery1.helpers

import android.content.Context
import hcmus.android.gallery1.R
import hcmus.android.gallery1.repository.PreferenceRepository.Companion.validTabs
import hcmus.android.gallery1.repository.PreferenceRepository.Companion.validViews
import hcmus.android.gallery1.repository.PreferenceRepository.Companion.validViewsLimited

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
    }
    else 0
}

fun Context.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    }
    else 0
}