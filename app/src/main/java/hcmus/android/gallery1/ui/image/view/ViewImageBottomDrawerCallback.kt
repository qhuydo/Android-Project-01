package hcmus.android.gallery1.ui.image.view

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.databinding.BottomDrawerViewImageBinding
import hcmus.android.gallery1.ui.widgets.bottomsheet.DefaultBottomDrawerCallback

class ViewImageBottomDrawerCallback(
    bottomDrawerDim: View,
    binding: BottomDrawerViewImageBinding,
    behavior: BottomSheetBehavior<*>
) : DefaultBottomDrawerCallback(
    bottomDrawerDim = bottomDrawerDim,
    binding.btnBdrawerViewImageExpand
) {

    private val infoLayout = binding.bdrawerViewImageInfo.root
    private val bottomSheetHeight = binding.root.measuredHeight

    override fun onStateCollapsed(bottomSheet: View, newState: Int) {
        super.onStateCollapsed(bottomSheet, newState)
        infoLayout.translationY = infoLayout.measuredHeight.toFloat() +
                bottomSheetHeight - infoLayout.measuredHeight
    }

    override fun onStateExpanded(bottomSheet: View, newState: Int) {
        super.onStateExpanded(bottomSheet, newState)
        infoLayout.translationY = 0f
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        super.onSlide(bottomSheet, slideOffset)
        if (slideOffset in 0.0..1.0) {
            val y = infoLayout.measuredHeight * slideOffset +
                    bottomSheetHeight - infoLayout.measuredHeight

            infoLayout.translationY = y
        }

    }
}