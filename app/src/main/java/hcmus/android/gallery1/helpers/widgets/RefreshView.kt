package hcmus.android.gallery1.helpers.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.RefreshViewBinding
import hcmus.android.gallery1.helpers.extensions.gone
import hcmus.android.gallery1.helpers.extensions.invisible
import hcmus.android.gallery1.helpers.extensions.visible

class RefreshView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        initView()
    }
    private lateinit var binding: RefreshViewBinding

    private fun initView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = RefreshViewBinding.inflate(inflater, this, true)
        binding.root.invisible()
    }

    fun startRefreshing() = with(binding) {
        refreshTextView.text = context.resources.getText(R.string.refreshing)
        root.visible()
        alpha = 1.0f

        indicator.apply {
            invisible()
            isIndeterminate = true
            visible()
            show()
        }
    }

    fun stopRefreshing() = with(binding) {
        refreshTextView.text = context.resources.getText(R.string.refresh)
        indicator.apply {
            invisible()
            isIndeterminate = false
            visible()
        }
        root.gone()
    }

    fun setDraggingPercent(percent: Float) = with(binding) {
        if (percent > 0f) root.visible()
        indicator.progress = (percent * 100).toInt()
        alpha = percent
    }


}