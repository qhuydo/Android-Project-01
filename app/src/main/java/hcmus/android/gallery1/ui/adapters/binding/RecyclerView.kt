package hcmus.android.gallery1.ui.adapters.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.ui.adapters.recyclerview.CollectionListAdapter

@BindingAdapter("loadAddToAlbumRecyclerView")
fun RecyclerView.loadAddToAlbumRecyclerView(list: List<Collection>?) {
    list?.let {
        (adapter as? CollectionListAdapter)?.submitList(list)
    }
}