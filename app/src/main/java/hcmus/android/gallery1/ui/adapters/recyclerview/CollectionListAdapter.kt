package hcmus.android.gallery1.ui.adapters.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Collection

open class CollectionListAdapter(
    private var isCompactLayout: Boolean = false,
    protected val callback: Callback? = null
) : ListAdapter<Collection, CollectionListViewHolder>(Collection.diffCallback) {

    fun changeCompactLayout(isCompactLayout: Boolean) {
        this.isCompactLayout = isCompactLayout
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isCompactLayout) {
            R.layout.list_collection_compact
        } else {
            R.layout.list_collection
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionListViewHolder {
        return CollectionListViewHolder.from(parent, viewType)
    }

    override fun onBindViewHolder(holder: CollectionListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            callback?.onClick(item, holder)
        }
    }

    open class Callback(private val onClickFn: (Collection, CollectionListViewHolder) -> Unit) {
        fun onClick(item: Collection, viewHolder: CollectionListViewHolder) =
            onClickFn.invoke(item, viewHolder)
    }
}