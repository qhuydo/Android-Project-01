package hcmus.android.gallery1.ui.adapters.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item

@BindingAdapter("loadGlideImage")
fun ImageView.loadGlideImage(item: Item?) {
    item?.let {
        Glide.with(context)
            .load(item.getUri())
            .error(R.drawable.placeholder_item)
            .into(this)
    }
}