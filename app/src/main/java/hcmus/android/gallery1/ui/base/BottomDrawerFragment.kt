package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import androidx.core.view.doOnLayout
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.helpers.extensions.gone
import hcmus.android.gallery1.helpers.extensions.visible

abstract class BottomDrawerFragment<B : ViewDataBinding>(layoutId: Int) :
    BaseFragment<B>(layoutId) {

    protected var fullScreenMode: Boolean = false

    // root view of the bottom drawer
    protected lateinit var bottomDrawerView: View
    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    // button to expand, collapse the bottom drawer
    protected lateinit var bottomSheetExpandButton: ImageButton
    protected lateinit var bottomDrawerDim: View

    protected var forceBack = false

    private val defaultPeekHeightInPixels by lazy {
        requireContext().resources.getDimension(R.dimen.bdrawer_peek_height)
    }

    private val navigationBarHeight by lazy {
        requireContext().navigationBarHeight()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBottomDrawer()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * set values for these attributes:
     * - [bottomDrawerView]
     * - [bottomSheetBehavior]
     * - [bottomSheetExpandButton]
     * - [bottomDrawerDim]
     */
    abstract fun initBottomDrawerElements()

    /**
     * Calculate the peek height of the bottom sheet view *(excluding the height of navigation bar)*
     */
    open fun calculatePeekHeight(): Int = defaultPeekHeightInPixels.toInt()

    open fun initBottomDrawerElementsCallback() {
        bottomDrawerDim.setOnClickListener {
            bottomSheetBehavior.collapse()
        }

        // Button expansion behavior
        bottomSheetExpandButton.setOnClickListener {
            bottomSheetBehavior.toggleCollapseState()
        }

        bottomSheetBehavior.addBottomSheetCallback(
            requireContext().defaultBottomSheetCallback(bottomDrawerDim, bottomSheetExpandButton)
        )
    }

    open fun initBottomDrawer() {
        initBottomDrawerElements()
        initBottomSheetBehaviour()
        initBottomDrawerElementsCallback()
    }

    open fun initBottomSheetBehaviour() {
        // Bottom sheet behavior
        bottomSheetBehavior.apply {

            // measure the peek height
            changePeekHeight()

            isFitToContents = true
            // halfExpandedRatio = (490/1000f) // magic
        }
        mainActivity?.setViewPaddingInNavigationBarSide(bottomDrawerView)
    }

    protected fun changePeekHeight() = with(bottomSheetBehavior) {
        bottomDrawerView.doOnLayout {
            peekHeight = calculatePeekHeight()
            if (mainActivity?.isBottomNavigationBar == true) {
                peekHeight += navigationBarHeight
            }
            paddingContainerToFitWithPeekHeight(peekHeight)
        }
    }

    // TODO: give me a better name or clearer explanation
    open fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {}

    override fun onBackPressed(): Boolean {
        if (!forceBack && bottomSheetBehavior.isNotCollapsed()) {
            bottomSheetBehavior.collapse()
            return true
        }
        return super.onBackPressed()
    }

    protected open fun showFullScreen() {
        animateHideBottomDrawer()
        mainActivity?.showFullScreen()
        fullScreenMode = true
    }

    private fun animateHideBottomDrawer() {
        bottomDrawerView.animate()
            .alpha(ALPHA_INVISIBLE)
            .translationY(bottomSheetBehavior.peekHeight.toFloat())
            .setInterpolator(AccelerateInterpolator())
            .setDuration(DURATION_BOTTOM_SHEET_ANIMATION) //ms
            .withEndAction { bottomDrawerView.gone() }
            .start()
    }

    protected open fun hideFullScreen() {
        animateShowBottomDrawer()
        mainActivity?.hideFullScreen()
        fullScreenMode = false
    }

    private fun animateShowBottomDrawer() {
        bottomDrawerView.animate()
            .alpha(ALPHA_VISIBLE)
            .translationY(0f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(DURATION_BOTTOM_SHEET_ANIMATION) //ms
            .withStartAction { bottomDrawerView.visible() }
            .start()
    }

    open fun getHiddenRows(): View? = null

    open fun animateHideHiddenRows() {
        getHiddenRows()
            ?.animate()
            ?.alpha(ALPHA_INVISIBLE)
    }

    open fun animateShowHiddenRows() {
        getHiddenRows()
            ?.animate()
            ?.alpha(ALPHA_VISIBLE)
            ?.apply {
                duration = context?.resources?.getInteger(
                    android.R.integer.config_longAnimTime
                )?.toLong() ?: return@apply
            }
    }
}
