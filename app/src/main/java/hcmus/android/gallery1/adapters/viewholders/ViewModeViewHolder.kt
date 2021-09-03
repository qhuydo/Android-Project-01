package hcmus.android.gallery1.adapters.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeCollectionBinding
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeItemBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.ui.main.MainActivity

abstract class ViewModeViewHolder(view: View): RecyclerView.ViewHolder(view) {

    protected val globalPrefs by lazy { (itemView.context as MainActivity).preferenceRepository }
    lateinit var tab: TAB

    abstract fun initButtonState()

    internal fun viewIdToViewMode(id: Int) = when (id) {
        R.id.btn_viewmode_item_grid_3 -> VIEW_ITEM_GRID_L
        R.id.btn_viewmode_item_grid_4 -> VIEW_ITEM_GRID_M
        R.id.btn_viewmode_item_grid_5 -> VIEW_ITEM_GRID_S
        R.id.btn_viewmode_collection_grid_2 -> VIEW_COLLECTION_GRID
        else -> VIEW_LIST
    }
}

class ViewModeCollectionViewHolder(private val binding: ButtonGroupViewmodeCollectionBinding) :
    ViewModeViewHolder(binding.root) {

    override fun initButtonState() {

        val tabKey = tab.toTabKey()

        val viewModeCollection = binding.viewmodeCollection

        viewModeCollection.check(
            when (globalPrefs.getViewMode(tabKey)) {
                VIEW_LIST -> R.id.btn_viewmode_collection_list
                VIEW_COLLECTION_GRID -> R.id.btn_viewmode_collection_grid_2
                else -> {
                    globalPrefs.setViewMode(tabKey, VIEW_TAB_COLLECTION_FALLBACK)
                    BTN_TAB_COLLECTION_FALLBACK
                }
            }
        )

    }

}

class ViewModeItemViewHolder(private val binding: ButtonGroupViewmodeItemBinding) :
    ViewModeViewHolder(binding.root) {

    override fun initButtonState() {

        val tabKey = tab.toTabKey()

        val viewModeCollection = binding.viewmodeItem

        viewModeCollection.check(
            when (globalPrefs.getViewMode(tabKey)) {
                VIEW_LIST -> R.id.btn_viewmode_item_list
                VIEW_ITEM_GRID_L -> R.id.btn_viewmode_item_grid_3
                VIEW_ITEM_GRID_M -> R.id.btn_viewmode_item_grid_4
                VIEW_ITEM_GRID_S -> R.id.btn_viewmode_item_grid_5
                else -> {
                    globalPrefs.setViewMode(tabKey, VIEW_TAB_ITEM_FALLBACK)
                    BTN_TAB_ITEM_FALLBACK
                }
            }
        )
    }

}