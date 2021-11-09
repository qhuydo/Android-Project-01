package hcmus.android.gallery1.ui.image.view

import android.view.View
import androidx.core.math.MathUtils.clamp
import hcmus.android.gallery1.databinding.BottomDrawerViewImageBinding
import hcmus.android.gallery1.helpers.ALPHA_INVISIBLE
import hcmus.android.gallery1.helpers.ALPHA_VISIBLE
import hcmus.android.gallery1.helpers.extensions.invisible
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.widgets.bottomsheet.DefaultBottomDrawerCallback

const val MIN_SLIDE_OFFSET = 0.85f

class ViewImageBottomDrawerCallback(
    bottomDrawerDim: View,
    binding: BottomDrawerViewImageBinding
) : DefaultBottomDrawerCallback(
    bottomDrawerDim = bottomDrawerDim,
    binding.btnBdrawerViewImageExpand
) {

    private val infoLayout = binding.bdrawerViewImageInfo.root
    private val secondRow = binding.secondRow
    private val bottomSheetHeight = binding.root.measuredHeight.toFloat()


    override fun onStateCollapsed(bottomSheet: View, newState: Int) {
        super.onStateCollapsed(bottomSheet, newState)
        infoLayout.translationY = infoLayout.measuredHeight.toFloat()

        secondRow.translationY = -(secondRow.measuredHeight * MIN_SLIDE_OFFSET
                + bottomSheetHeight - secondRow.measuredHeight)

        secondRow.alpha = ALPHA_INVISIBLE
    }

    override fun onStateExpanded(bottomSheet: View, newState: Int) {
        super.onStateExpanded(bottomSheet, newState)
        infoLayout.translationY = 0f

        secondRow.alpha = ALPHA_VISIBLE
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        super.onSlide(bottomSheet, slideOffset)
        if (slideOffset in 0.0..1.0) {
            animateInfoLayout(slideOffset)
            animateSecondRow(slideOffset)
        }

    }

    private fun animateSecondRow(slideOffset: Float) {

        if (slideOffset >= MIN_SLIDE_OFFSET) {
            val y = -(secondRow.measuredHeight * slideOffset
                    + bottomSheetHeight - secondRow.measuredHeight)
            secondRow.translationY = y
        }
        secondRow.alpha = clamp(
            (slideOffset - MIN_SLIDE_OFFSET) * 1 / (1 - MIN_SLIDE_OFFSET),
            0f, 1f
        )
    }

    private fun animateInfoLayout(slideOffset: Float) {
        val y = infoLayout.measuredHeight - infoLayout.measuredHeight * slideOffset
        infoLayout.translationY = y
    }
}