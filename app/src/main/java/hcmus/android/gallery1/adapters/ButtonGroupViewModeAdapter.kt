package hcmus.android.gallery1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import hcmus.android.gallery1.R
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.*

class ButtonGroupViewModeAdapter(val viewModeSelectedCallback: OnViewModeSelectedCallback) :
    RecyclerView.Adapter<ButtonGroupViewModeViewHolder>() {

    private val tabs = listOf(
        TAB.ALL,
        TAB.ALBUM,
        TAB.DATE,
        TAB.FAV
    )

    override fun getItemCount(): Int = tabs.size

    override fun getItemViewType(position: Int) = when (position) {
        TAB.ALL.ordinal -> R.layout.button_group_viewmode_item
        TAB.ALBUM.ordinal -> R.layout.button_group_viewmode_collection
        TAB.DATE.ordinal -> R.layout.button_group_viewmode_collection
        TAB.FAV.ordinal -> R.layout.button_group_viewmode_item
        else -> super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ButtonGroupViewModeViewHolder, position: Int) {
        val tab = when (position) {
            TAB.ALL.ordinal -> TAB.ALL
            TAB.ALBUM.ordinal -> TAB.ALBUM
            TAB.DATE.ordinal -> TAB.DATE
            TAB.FAV.ordinal -> TAB.FAV
            else -> TAB.ALL
        }

        holder.tab = tab
        holder.apply {
            initButtonState()
            (itemView as? MaterialButtonToggleGroup)?.addOnButtonCheckedListener { _, checkedId, _ ->
                val viewMode = viewIdToViewMode(checkedId)
                viewModeSelectedCallback.onViewModeSelected(this.tab, viewMode)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ButtonGroupViewModeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)

        return ButtonGroupViewModeViewHolder(view)
    }

}

class ButtonGroupViewModeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    lateinit var tab: TAB

    fun initButtonState() {
        val tabKey = when (tab) {
            TAB.ALBUM -> TAB_ALBUM
            TAB.DATE -> TAB_DATE
            TAB.FAV -> TAB_FAV
            else -> TAB_ALL
        }

        when (tab) {
            TAB.ALL, TAB.FAV -> initButtonGroupItem(tabKey)
            else -> initButtonGroupCollection(tabKey)
        }
    }

    private fun initButtonGroupCollection(tabKey: String) {
        val viewModeCollection = itemView.findViewById<MaterialButtonToggleGroup>(
            R.id.viewmode_collection
        )

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

    private fun initButtonGroupItem(tabKey: String) {
        val viewModeCollection = itemView.findViewById<MaterialButtonToggleGroup>(
            R.id.viewmode_item
        )

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

    internal fun viewIdToViewMode(id: Int) = when (id) {
        R.id.btn_viewmode_item_grid_3 -> VIEW_ITEM_GRID_L
        R.id.btn_viewmode_item_grid_4 -> VIEW_ITEM_GRID_M
        R.id.btn_viewmode_item_grid_5 -> VIEW_ITEM_GRID_S
        R.id.btn_viewmode_collection_grid_2 -> VIEW_COLLECTION_GRID
        else -> VIEW_LIST
    }

}

interface OnViewModeSelectedCallback {
    fun onViewModeSelected(tab: TAB, viewMode: String)
}