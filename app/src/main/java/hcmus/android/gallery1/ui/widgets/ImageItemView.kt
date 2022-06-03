package hcmus.android.gallery1.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.databinding.BindingAdapter
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ORIENTATION_USE_EXIF
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

    private fun goneAllExcept(view: View) {
        listOf(
            binding.glideView,
            binding.subscaleView,
            binding.playerView
        ).forEach { it.gone() }
        view.visible()
    }

    private fun bindItemInternal(item: Item) {
        when (item.getType()) {
            ItemType.STATIC_IMAGE -> {
                binding.subscaleView.setImage(ImageSource.uri(item.getUri()))
                binding.subscaleView.orientation = ORIENTATION_USE_EXIF
                goneAllExcept(binding.subscaleView)
            }
            ItemType.VIDEO -> {
                goneAllExcept(binding.playerView)
            }
            else -> {
                binding.glideView.loadGlideImage(item)
                goneAllExcept(binding.glideView)
            }
        }
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
    }

    companion object {
        @BindingAdapter("item")
        fun ImageItemView.bindItem(item: Item?) = item?.let {
            // binding.item = item
            bindItemInternal(item)
        }
    }
}
