package hcmus.android.gallery1.ui.adapters.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.databinding.ListCollectionBinding
import hcmus.android.gallery1.databinding.ListCollectionCompactBinding

sealed class CollectionListViewHolder(view: View): RecyclerView.ViewHolder(view) {

    abstract fun bind(item: Collection)

    class ViewHolderCompact(private val binding: ListCollectionCompactBinding) :
        CollectionListViewHolder(binding.root) {

        override fun bind(item: Collection) {
            binding.item = item
            binding.executePendingBindings()
        }

    }

    class ViewHolderGrid(private val binding: ListCollectionBinding) :
        CollectionListViewHolder(binding.root) {

        override fun bind(item: Collection) {
            binding.item = item
            binding.executePendingBindings()
        }

    }

    companion object {
        fun from(parent: ViewGroup, type: Int): CollectionListViewHolder {

            val layoutInflater = LayoutInflater.from(parent.context)

            return when (type) {
                R.layout.list_collection_compact -> {
                    val binding =
                        ListCollectionCompactBinding.inflate(layoutInflater, parent, false)
                    ViewHolderCompact(binding)
                }
                else -> {
                    val binding = ListCollectionBinding.inflate(layoutInflater, parent, false)
                    ViewHolderGrid(binding)
                }
            }

        }
    }
}