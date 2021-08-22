package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R

abstract class BottomDrawerFragment<B : ViewDataBinding, V : View>(layoutId: Int) :
    BaseFragment<B>(layoutId) {

    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<V>
    protected lateinit var bottomSheetExpandButton: ImageButton
    protected lateinit var bottomDrawerDim: View

    protected var forceBack = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomDrawer()
    }

    /**
     * set values for these attributes:
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
                        bottomDrawerDim.visibility = View.GONE
                        val drawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_bdrawer_up
                        )
                        bottomSheetExpandButton.setImageDrawable(drawable)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomDrawerDim.visibility = View.VISIBLE
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
                bottomDrawerDim.visibility = View.VISIBLE
                bottomDrawerDim.alpha = 0.5f * slideOffset
            }
        })
    }

    open fun initBottomDrawer() {
        initBottomDrawerElements()

        // Bottom sheet behavior
        bottomSheetBehavior.apply {
            isFitToContents = true
            // halfExpandedRatio = (490/1000f) // magic
        }

        initBottomDrawerElementsCallback()
    }

    override fun onBackPressed(): Boolean {
        if (!forceBack && bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return super.onBackPressed()
    }

}
