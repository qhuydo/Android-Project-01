package hcmus.android.gallery1.helpers

import android.content.Context
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.PreferenceFacility.Companion.validTabs
import hcmus.android.gallery1.helpers.PreferenceFacility.Companion.validViews
import hcmus.android.gallery1.helpers.PreferenceFacility.Companion.validViewsLimited

fun Context.getSpanCountOf(tab: String, viewMode: String): Int {
    if (tab !in validTabs || (viewMode !in validViews && viewMode !in validViewsLimited)) return 0

    return when (viewMode) {
        VIEW_COLLECTION_GRID -> resources.getInteger(R.integer.collection_grid)
        VIEW_ITEM_GRID_L -> resources.getInteger(R.integer.item_grid_l)
        VIEW_ITEM_GRID_M -> resources.getInteger(R.integer.item_grid_m)
        VIEW_ITEM_GRID_S -> resources.getInteger(R.integer.item_grid_s)
        else -> 1
    }
}