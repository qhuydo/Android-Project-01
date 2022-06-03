package hcmus.android.gallery1.ui.adapters.binding

import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.*

@BindingAdapter("buttonViewModeItemState")
fun MaterialButtonToggleGroup.setButtonViewModeItemState(viewMode: String?) {
    if (viewMode != null) {
        check(
            when (viewMode) {
                VIEW_LIST -> R.id.btn_viewmode_item_list
                VIEW_ITEM_GRID_L -> R.id.btn_viewmode_item_grid_3
                VIEW_ITEM_GRID_M -> R.id.btn_viewmode_item_grid_4
                VIEW_ITEM_GRID_S -> R.id.btn_viewmode_item_grid_5
                else -> BTN_TAB_ITEM_FALLBACK
            }
        )
    }
}

@BindingAdapter("buttonViewModeCollectionState")
fun MaterialButtonToggleGroup.setButtonViewModeCollectionState(viewMode: String?) {
    if (viewMode != null) {
        check(
            when (viewMode) {
                VIEW_LIST -> R.id.btn_viewmode_collection_list
                VIEW_COLLECTION_GRID -> R.id.btn_viewmode_collection_grid_2
                else -> BTN_TAB_COLLECTION_FALLBACK
            }
        )
    }
}