package hcmus.android.gallery1.helpers.extensions

import android.view.Surface
import androidx.annotation.IdRes
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.*

fun Int.isHorizontalRotation() = this == Surface.ROTATION_90 || this == Surface.ROTATION_270

fun @receiver:IdRes Int.bottomNavIdToTabPosition() = when (this) {
    R.id.menu_tab_all -> TAB.ALL.ordinal
    R.id.menu_tab_album -> TAB.ALBUM.ordinal
    R.id.menu_tab_date -> TAB.DATE.ordinal
    R.id.menu_tab_favorites -> TAB.FAV.ordinal
    else -> TAB.ALL.ordinal
}

fun @receiver:IdRes Int.viewIdToViewMode(): String = when (this) {
    R.id.btn_viewmode_item_grid_3 -> VIEW_ITEM_GRID_L
    R.id.btn_viewmode_item_grid_4 -> VIEW_ITEM_GRID_M
    R.id.btn_viewmode_item_grid_5 -> VIEW_ITEM_GRID_S
    R.id.btn_viewmode_collection_grid_2 -> VIEW_COLLECTION_GRID
    else -> VIEW_LIST
}