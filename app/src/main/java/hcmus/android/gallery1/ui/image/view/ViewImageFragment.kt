package hcmus.android.gallery1.ui.image.view

import android.os.Bundle
import android.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.ItemType
import hcmus.android.gallery1.databinding.FragmentViewImageBinding
import hcmus.android.gallery1.helpers.ALPHA_VISIBLE
import hcmus.android.gallery1.helpers.extensions.gone
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.adapters.viewpager2.ImagePageAdapter
import hcmus.android.gallery1.ui.base.BaseViewImageFragment

class ViewImageFragment : BaseViewImageFragment<FragmentViewImageBinding>(
    R.layout.fragment_view_image
) {

    private val pagerAdapter by lazy {
        ImagePageAdapter(sharedViewModel.currentDisplayingList, ImagePageAdapter.Callback {
            toggleFullScreenMode()
        })
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            sharedViewModel.apply {
                currentDisplayingItemPos = position
                currentDisplayingList?.getOrNull(position)?.let {
                    viewModel.setItem(it)
                    arguments?.putParcelable(ARGS_ITEM, it)
                }
            }
        }
    }

    override fun getBottomDrawer() = binding.bdrawerViewImageLayout

    override fun getBottomDrawerDimView() = binding.bdrawerViewImageDim

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedViewModel.currentDisplayingList?.let { items ->
            val pos = sharedViewModel.currentDisplayingItemPos
            arguments = Bundle().apply { putParcelable(ARGS_ITEM, items.getOrNull(pos)) }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.pagerImage.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    override fun subscribeUi() = with(viewModel) {
        super.subscribeUi()
        item.observe(viewLifecycleOwner) {
            if (it != null) {
                getBottomDrawer().bdrawerViewImage.visible()
                this@ViewImageFragment.item = it

                if (it.getType() == ItemType.VIDEO) {
                    getBottomDrawer().videoController.visible()
                } else {
                    getBottomDrawer().videoController.gone()
                }
                TransitionManager.beginDelayedTransition(getBottomDrawer().bdrawerViewImage)
                changePeekHeight()

            }
        }
    }

    override fun bindData() = binding.run {
        fragment = this@ViewImageFragment
        photoViewModel = viewModel

        pagerImage.run {
            adapter = pagerAdapter
            registerOnPageChangeCallback(onPageChangeCallback)
            setCurrentItem(sharedViewModel.currentDisplayingItemPos, false)
        }
        flingLayout.onPositionChanged = { percent ->
            previousFragment?.view?.alpha = percent
        }
        flingLayout.onViewDismissed = {
            onViewDismissed()
        }
    }

    private fun onViewDismissed() {
        forceBack = true
        exitTransition = null
        returnTransition = null
        previousFragment?.view?.alpha = ALPHA_VISIBLE
        activity?.onBackPressed()
    }

    override fun notifyItemRemoved() {
        pagerAdapter.notifyItemRemoved(sharedViewModel.currentDisplayingItemPos)

        val currentPos = binding.pagerImage.currentItem
        sharedViewModel.currentDisplayingList?.getOrNull(currentPos)?.let {
            viewModel.setItem(it)
        }

        if (sharedViewModel.currentDisplayingList?.isEmpty() == true) closeViewer()
    }

    override fun calculatePeekHeight(): Int = with(binding.bdrawerViewImageLayout) {
        val videoControllerHeight = if (item.getType() == ItemType.VIDEO) {
            videoController.measuredHeight
        }
        else {
            0
        }
        return listDivider.measuredHeight + topRow.measuredHeight + videoControllerHeight
    }

    // override the default to remove the peek height change
    // peek height of bottom drawer will be set when the video model has loaded image/video.
    override fun initBottomSheetBehaviour() {
        // Bottom sheet behavior
        bottomSheetBehavior.apply {
            isFitToContents = true
        }
        mainActivity?.setViewPaddingInNavigationBarSide(bottomDrawerView)
    }
}