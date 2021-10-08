package hcmus.android.gallery1.ui.adapters.viewpager2

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.PlayerView
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.widgets.ImageItemView
import hcmus.android.gallery1.helpers.widgets.ImageItemView.Companion.bindItem

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
        holder.bind(item, callback)
    }

    override fun getItemCount(): Int = itemList?.size ?: 0

    interface Callback {
        fun onClick(item: Item?)
        fun onVideoViewClicked(videoView: PlayerView, item: Item?)
    }
}

class ImagePageViewHolder internal constructor(private val imageItemView: ImageItemView) :
    RecyclerView.ViewHolder(imageItemView) {
    internal fun bind(item: Item?, callback: ImagePageAdapter.Callback?) {
        item?.let {
            imageItemView.apply {
                tag = item.id
                bindItem(item)
            }
            callback?.let {
                itemView.setOnClickListener { callback.onClick(item) }
                imageItemView.binding.playerView.setOnClickListener {
                    callback.onVideoViewClicked(imageItemView.binding.playerView, item)
                }
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): ImagePageViewHolder {
            return ImagePageViewHolder(
                ImageItemView(parent.context)
            )
        }
    }
}
