package hcmus.android.gallery1.helpers.extensions

import com.google.android.material.bottomsheet.BottomSheetBehavior

fun BottomSheetBehavior<*>.collapse() {
    state = BottomSheetBehavior.STATE_COLLAPSED
}

fun BottomSheetBehavior<*>.expand() {
    state = BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetBehavior<*>.isCollapsed() = state == BottomSheetBehavior.STATE_COLLAPSED
fun BottomSheetBehavior<*>.isNotCollapsed() = state != BottomSheetBehavior.STATE_COLLAPSED

fun BottomSheetBehavior<*>.toggleCollapseState() {
    if (isCollapsed()) expand()
    else if (isNotCollapsed()) collapse()
}