package hcmus.android.gallery1.ui.adapters.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeCollectionBinding
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeItemBinding
import hcmus.android.gallery1.helpers.TAB

class ButtonGroupViewModeAdapter(private val viewModeSelectedCallback: OnViewModeSelectedCallback) :
    RecyclerView.Adapter<ViewModeViewHolder>() {

    private val tabs = TAB.validTabs()

    override fun getItemCount(): Int = tabs.size

    override fun getItemViewType(position: Int) = when (position) {
        TAB.ALL.ordinal -> R.layout.button_group_viewmode_item
        TAB.ALBUM.ordinal -> R.layout.button_group_viewmode_collection
        TAB.DATE.ordinal -> R.layout.button_group_viewmode_collection
        TAB.FAV.ordinal -> R.layout.button_group_viewmode_item
        else -> super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewModeViewHolder, position: Int) {
        val tab = TAB.fromPosition(position)
        holder.bind(tab, viewModeSelectedCallback)
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