package hcmus.android.gallery1.ui.adapters.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item

class ItemListAdapter(
    private val isCompactLayout: Boolean = false,
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
            callback?.onClick(item)
        }
    }

    class Callback(private val onClickFn: (Item) -> Unit) {
        fun onClick(item: Item) = onClickFn.invoke(item)
    }
}
