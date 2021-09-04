package hcmus.android.gallery1.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import hcmus.android.gallery1.R
import hcmus.android.gallery1.ui.adapters.viewholders.ViewModeCollectionViewHolder
import hcmus.android.gallery1.ui.adapters.viewholders.ViewModeItemViewHolder
import hcmus.android.gallery1.ui.adapters.viewholders.ViewModeViewHolder
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeCollectionBinding
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeItemBinding
import hcmus.android.gallery1.helpers.*

class ButtonGroupViewModeAdapter(private val viewModeSelectedCallback: OnViewModeSelectedCallback) :
    RecyclerView.Adapter<ViewModeViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewModeViewHolder, position: Int) {
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
    ): ViewModeViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.button_group_viewmode_collection -> {
                val binding = ButtonGroupViewmodeCollectionBinding.inflate(
                    inflater, parent, false
                )
                ViewModeCollectionViewHolder(binding)
            }
            else -> {
                val binding = ButtonGroupViewmodeItemBinding.inflate(
                    inflater, parent, false
                )
                ViewModeItemViewHolder(binding)
            }
        }
    }

}

interface OnViewModeSelectedCallback {
    fun onViewModeSelected(tab: TAB, viewMode: String)
}