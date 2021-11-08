package hcmus.android.gallery1.ui.widgets.bottomsheet

import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.extensions.gone
import hcmus.android.gallery1.helpers.extensions.setLowProfileUI
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.main.MainActivity

open class DefaultBottomDrawerCallback(
    private val bottomDrawerDim: View,
    private val bottomSheetExpandButton: ImageButton
) : BottomSheetBehavior.BottomSheetCallback() {

    open fun onStateCollapsed(bottomSheet: View, newState: Int) {
        bottomDrawerDim.gone()
        val drawable = ContextCompat.getDrawable(
            bottomSheet.context,
            R.drawable.ic_bdrawer_anim_down_to_up
        )
        bottomSheetExpandButton.setImageDrawable(drawable)
        (drawable as? AnimatedVectorDrawable)?.start()
        (bottomSheet.context as? MainActivity)?.setLowProfileUI(false)
    }

    open fun onStateExpanded(bottomSheet: View, newState: Int) {
        bottomDrawerDim.visible()
        val drawable = ContextCompat.getDrawable(
            bottomSheet.context,
            R.drawable.ic_bdrawer_anim_up_to_down
        )
        bottomSheetExpandButton.setImageDrawable(drawable)
        (drawable as? AnimatedVectorDrawable)?.start()
        (bottomSheet.context as? MainActivity)?.setLowProfileUI(true)
    }

    open fun onStateDragging(bottomSheet: View, newState: Int) {}
    open fun onStateHidden(bottomSheet: View, newState: Int) {}
    open fun onStateHalfExpanded(bottomSheet: View, newState: Int) {}
    open fun onStateSettling(bottomSheet: View, newState: Int) {}

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED -> onStateCollapsed(bottomSheet, newState)
            BottomSheetBehavior.STATE_EXPANDED -> onStateExpanded(bottomSheet, newState)
            BottomSheetBehavior.STATE_DRAGGING -> onStateDragging(bottomSheet, newState)
            BottomSheetBehavior.STATE_HALF_EXPANDED -> onStateHalfExpanded(bottomSheet, newState)
            BottomSheetBehavior.STATE_HIDDEN -> onStateHidden(bottomSheet, newState)
            BottomSheetBehavior.STATE_SETTLING -> onStateSettling(bottomSheet, newState)
            else -> {
            }
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        bottomDrawerDim.visible()
        bottomDrawerDim.alpha = 0.5f * slideOffset
    }
}