package hcmus.android.gallery1.ui.main

import android.view.View
import androidx.core.math.MathUtils.clamp
import hcmus.android.gallery1.databinding.BottomDrawerMainBinding
import hcmus.android.gallery1.helpers.ALPHA_INVISIBLE
import hcmus.android.gallery1.helpers.ALPHA_VISIBLE
import hcmus.android.gallery1.ui.widgets.bottomsheet.DefaultBottomDrawerCallback

const val MIN_OFFSET_TO_ANIMATE_ALPHA = 0.75f

class MainBottomDrawerCallback(
    bottomDrawerDim: View,
    binding: BottomDrawerMainBinding
) : DefaultBottomDrawerCallback(
    bottomDrawerDim = bottomDrawerDim,
    binding.btnBottomSheetExpand
) {
    private val hiddenRows = binding.hiddenRows
    private val bottomSheetHeight = binding.root.measuredHeight

    override fun onStateCollapsed(bottomSheet: View, newState: Int) {
        super.onStateCollapsed(bottomSheet, newState)
        hiddenRows.alpha = ALPHA_INVISIBLE
        hiddenRows.translationY = -(bottomSheetHeight).toFloat()
    }

    override fun onStateExpanded(bottomSheet: View, newState: Int) {
        super.onStateExpanded(bottomSheet, newState)
        hiddenRows.alpha = ALPHA_VISIBLE
        hiddenRows.translationY = 0f
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        super.onSlide(bottomSheet, slideOffset)

        if (slideOffset in 0.0..1.0) {

            hiddenRows.alpha = clamp(
                (slideOffset - MIN_OFFSET_TO_ANIMATE_ALPHA)
                        * 1 / (1 - MIN_OFFSET_TO_ANIMATE_ALPHA),
                0f,
                1f
            )

            val y = -(hiddenRows.measuredHeight * slideOffset +
                    bottomSheetHeight - hiddenRows.measuredHeight)
            hiddenRows.translationY = y
        }
    }
}