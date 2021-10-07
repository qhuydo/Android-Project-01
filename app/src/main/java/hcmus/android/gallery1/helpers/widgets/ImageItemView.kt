package hcmus.android.gallery1.helpers.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.databinding.BindingAdapter
import com.davemorrissey.labs.subscaleview.ImageSource
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.data.ItemType
import hcmus.android.gallery1.databinding.ViewImageItemBinding
import hcmus.android.gallery1.helpers.extensions.gone
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.adapters.binding.loadGlideImage

class ImageItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        initView()
    }

    internal lateinit var binding: ViewImageItemBinding

    private fun initView() {
        binding = ViewImageItemBinding.inflate(
            LayoutInflater.from(context), this, true
        )
        binding.subscaleView.setOnClickListener {
            callOnClick()
        }

        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)

    }

    fun goneAllExcept(view: View) {
        listOf(
            binding.glideView,
            binding.subscaleView
        ).forEach { it.gone() }
        view.visible()
    }
}

@BindingAdapter("item")
fun ImageItemView.bindItem(item: Item?) = item?.let {
    // binding.item = item
    when (item.getType()) {
        ItemType.STATIC_IMAGE -> {
            binding.subscaleView.setImage(ImageSource.uri(item.getUri()))
            goneAllExcept(binding.subscaleView)
        }
        else -> {
            binding.glideView.loadGlideImage(item)
            goneAllExcept(binding.glideView)
        }
    }
}