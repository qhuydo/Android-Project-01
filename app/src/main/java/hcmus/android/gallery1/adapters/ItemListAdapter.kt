package hcmus.android.gallery1.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item

class ItemListAdapter(
    private val items: List<Item>,
    private val isCompactLayout: Boolean = false,
    private val callback: Callback? = null
) :
    RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val targetItemLayout = if (isCompactLayout) {
            R.layout.list_item_compact
        } else {
            R.layout.list_item
        }
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(targetItemLayout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val image = holder.itemView.findViewById<ImageView>(R.id.item_thumbnail)
        Log.e("", item.getUri())
        Glide.with(image.context)
            .load(item.getUri())
            .error(R.drawable.placeholder_item)
            .transition(DrawableTransitionOptions.withCrossFade(250)) // ms
            .into(image)

        if (isCompactLayout) {
            val name = holder.itemView.findViewById<TextView>(R.id.item_name)
            name.text = item.fileName
        }

        holder.itemView.setOnClickListener {
            callback?.onClick(item)
        }
    }

    class Callback(private val onClickFn: (Item) -> Unit) {
        fun onClick(item: Item) = onClickFn.invoke(item)
    }
}
