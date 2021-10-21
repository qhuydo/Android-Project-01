package hcmus.android.gallery1.ui.adapters.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item

class ItemListAdapter(
    private var isCompactLayout: Boolean = false,
    private val callback: Callback? = null
) : ListAdapter<Item, ItemListViewHolder>(Item.diffCallback) {

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
        holder.bind(item)
        holder.itemView.setOnClickListener {
            callback?.onClick(item, holder.bindingAdapterPosition)
//            holder.itemView.isSelected = !holder.itemView.isSelected
        }
    }

    fun changeCompactLayout(isCompactLayout: Boolean) {
        this.isCompactLayout = isCompactLayout
        notifyDataSetChanged()
    }

    class Callback(private val onClickFn: (Item, Int) -> Unit) {
        fun onClick(item: Item, position: Int) = onClickFn.invoke(item, position)
    }
}
