package hcmus.android.gallery1.ui.adapters.recyclerview

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ListAdapter
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item

class ItemListAdapter(
    context: Context,
    private val isCompactLayout: Boolean = false,
    private val callback: Callback? = null
) : ListAdapter<Item, ItemListViewHolder>(Item.diffCallback) {

    private val checkIcon = AppCompatResources.getDrawable(context, R.drawable.ic_check)!!
    private val selectedTintColour = context.getColor(R.color.item_selected_colour)
    private val selectedCornerRadius = if (!isCompactLayout) {
        context.resources.getDimensionPixelSize(R.dimen.selected_corner_radius)
    } else {
        0
    }

    override fun getItemViewType(position: Int): Int {
        return if (isCompactLayout) {
            R.layout.list_item_compact
        } else {
            R.layout.list_item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        return ItemListViewHolder.from(parent, viewType)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, selectedTintColour, selectedCornerRadius, checkIcon)
        holder.itemView.setOnClickListener {
            callback?.onClick(item)
            // holder.itemView.isSelected = !holder.itemView.isSelected
        }
    }

    class Callback(private val onClickFn: (Item) -> Unit) {
        fun onClick(item: Item) = onClickFn.invoke(item)
    }
}
