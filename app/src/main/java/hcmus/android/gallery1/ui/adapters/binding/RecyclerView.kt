package hcmus.android.gallery1.ui.adapters.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.ui.adapters.recyclerview.CollectionListAdapter
import hcmus.android.gallery1.ui.adapters.recyclerview.ItemListAdapter

@BindingAdapter("loadAddToAlbumRecyclerView")
fun RecyclerView.loadAddToAlbumRecyclerView(list: List<Collection>?) {
    list?.let {
        (adapter as? CollectionListAdapter)?.submitList(list)
    }
}

@BindingAdapter("loadItemsIntoRecyclerView")
fun RecyclerView.loadItemsIntoRecyclerView(list: List<Item>?) {
    list?.let {
        (adapter as? ItemListAdapter)?.submitList(list)
    }
}