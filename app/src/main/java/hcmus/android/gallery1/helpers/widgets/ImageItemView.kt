package hcmus.android.gallery1.helpers.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.BindingAdapter
import com.davemorrissey.labs.subscaleview.ImageSource
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.data.ItemType
import hcmus.android.gallery1.databinding.ViewImageItemBinding
import hcmus.android.gallery1.helpers.extensions.gone
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.adapters.binding.loadGlideImage

class ImageItemView : FrameLayout {

    init {
        initView()
    }

    lateinit var binding: ViewImageItemBinding

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private fun initView() {
        binding = ViewImageItemBinding.inflate(
            LayoutInflater.from(context), this, true
        )
        binding.subscaleView.setOnClickListener {
            callOnClick()
        }
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