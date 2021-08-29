package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.navigationBarHeight
import hcmus.android.gallery1.helpers.widgets.gone
import hcmus.android.gallery1.helpers.widgets.visible

abstract class BottomDrawerFragment<B : ViewDataBinding, V : View>(layoutId: Int) :
    BaseFragment<B>(layoutId) {

    protected var fullScreenMode: Boolean = false

    // root view of the bottom drawer
    protected lateinit var bottomDrawerView: View
    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<V>
    // button to expand, collapse the bottom drawer
    protected lateinit var bottomSheetExpandButton: ImageButton
    protected lateinit var bottomDrawerDim: View

    protected var forceBack = false

    protected val peekHeightInPixels by lazy {
        requireContext().resources.getDimension(R.dimen.bdrawer_peek_height)
    }

    protected val navigationBarHeight by lazy {
        requireContext().navigationBarHeight()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomDrawer()
    }

    /**
     * set values for these attributes:
     * - [bottomDrawerView]
     * - [bottomSheetBehavior]
     * - [bottomSheetExpandButton]
     * - [bottomDrawerDim]
     */
    abstract fun initBottomDrawerElements()

    open fun initBottomDrawerElementsCallback() {
        bottomDrawerDim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Button expansion behavior
        bottomSheetExpandButton.setOnClickListener {
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED
                else -> { }
            }
        }

        // https://blog.mindorks.com/android-bottomsheet-in-kotlin
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomDrawerDim.gone()
                        val drawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_bdrawer_up
                        )
                        bottomSheetExpandButton.setImageDrawable(drawable)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomDrawerDim.visible()
                        val drawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_bdrawer_down
                        )
                        bottomSheetExpandButton.setImageDrawable(drawable)
                    }
                    else -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomDrawerDim.visible()
                bottomDrawerDim.alpha = 0.5f * slideOffset
            }
        })
    }

    open fun initBottomDrawer() {
        initBottomDrawerElements()
        initBottomSheetBehaviour()
        initBottomDrawerElementsCallback()
    }

    open fun initBottomSheetBehaviour() {
        // Bottom sheet behavior
        bottomSheetBehavior.apply {
            if (mainActivity?.isBottomNavigationBar == true) {
                peekHeight += navigationBarHeight
            }
            isFitToContents = true
            // halfExpandedRatio = (490/1000f) // magic
        }
        mainActivity?.setViewPaddingInNavigationBarSide(bottomDrawerView)
    }

    override fun onBackPressed(): Boolean {
        if (!forceBack && bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return super.onBackPressed()
    }

    protected fun showFullScreen() {
        bottomDrawerView.animate()
            .alpha(0f)
            .translationY(bottomSheetBehavior.peekHeight.toFloat())
            .setInterpolator(DecelerateInterpolator())
            .setDuration(240) //ms
            .withEndAction { bottomDrawerView.gone() }
            .start()

        mainActivity?.showFullScreen()
        fullScreenMode = true
    }

    protected fun hideFullScreen() {
        bottomDrawerView.animate()
            .alpha(1f)
            .translationY(0f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(240) //ms
            .withStartAction { bottomDrawerView.visible() }
            .start()

        mainActivity?.hideFullScreen()
        fullScreenMode = false
    }

    fun collapseDrawer() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}
