package hcmus.android.gallery1.ui.adapters.viewpager2

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.widgets.ImageItemView
import hcmus.android.gallery1.helpers.widgets.bindItem

class ImagePageAdapter(
    private val itemList: List<Item>?,
    private val callback: Callback? = null
) :
    RecyclerView.Adapter<ImagePageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePageViewHolder {
        return ImagePageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImagePageViewHolder, position: Int) {
        val item = itemList?.getOrNull(position)
        holder.bind(item)
        callback?.let { holder.itemView.setOnClickListener { callback.onClick(item) } }
    }

    override fun getItemCount(): Int = itemList?.size ?: 0


    class Callback(private val onClickFn: (item: Item?) -> Unit) {
        fun onClick(item: Item?) = onClickFn.invoke(item)
    }
}

class ImagePageViewHolder internal constructor(private val imageItemView: ImageItemView) :
    RecyclerView.ViewHolder(imageItemView) {
    internal fun bind(item: Item?) {
        imageItemView.bindItem(item)
    }

    companion object {
        fun from(parent: ViewGroup): ImagePageViewHolder {
            return ImagePageViewHolder(
                ImageItemView(parent.context)
            )
        }
    }
}
