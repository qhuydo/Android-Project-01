package hcmus.android.gallery1.helpers.extensions

import android.view.Surface
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.TAB

fun Int.isHorizontalRotation() = this == Surface.ROTATION_90 || this == Surface.ROTATION_270

fun Int.bottomNavIdToTabPosition() = when (this) {
    R.id.menu_tab_all -> TAB.ALL.ordinal
    R.id.menu_tab_album -> TAB.ALBUM.ordinal
    R.id.menu_tab_date -> TAB.DATE.ordinal
    R.id.menu_tab_favorites -> TAB.FAV.ordinal
    else -> TAB.ALL.ordinal
}