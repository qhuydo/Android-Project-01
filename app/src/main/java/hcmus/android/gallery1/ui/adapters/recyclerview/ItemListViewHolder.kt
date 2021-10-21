package hcmus.android.gallery1.ui.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.ListItemBinding
import hcmus.android.gallery1.databinding.ListItemCompactBinding

sealed class ItemListViewHolder(view: View): RecyclerView.ViewHolder(view) {

    abstract fun bind(item: Item)

    class ViewHolderCompact(private val binding: ListItemCompactBinding) :
        ItemListViewHolder(binding.root) {

        override fun bind(item: Item) {
            binding.item = item
            binding.executePendingBindings()
        }

    }

    class ViewHolderGrid(private val binding: ListItemBinding) :
        ItemListViewHolder(binding.root) {

        override fun bind(item: Item) {
            binding.item = item
            binding.executePendingBindings()
        }

    }

    companion object {
        fun from(parent: ViewGroup, type: Int): ItemListViewHolder {

            val layoutInflater = LayoutInflater.from(parent.context)

            return when (type) {
                R.layout.list_item_compact -> {
                    val binding =
                        ListItemCompactBinding.inflate(layoutInflater, parent, false)
                    ViewHolderCompact(binding)
                }
                else -> {
                    val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                    ViewHolderGrid(binding)
                }
            }

        }
    }
}