package hcmus.android.gallery1.ui.image.view

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ui.PlayerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.data.ItemType
import hcmus.android.gallery1.databinding.FragmentViewImageBinding
import hcmus.android.gallery1.helpers.ALPHA_VISIBLE
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.helpers.widgets.ImageItemView
import hcmus.android.gallery1.ui.adapters.viewpager2.ImagePageAdapter
import hcmus.android.gallery1.ui.base.BaseViewImageFragment

class ViewImageFragment : BaseViewImageFragment<FragmentViewImageBinding>(
    R.layout.fragment_view_image,
    screenConstant = ScreenConstant.IMAGE_VIEW
) {

//    private var currentItemView: ImageItemView? = null

    private val pagerAdapter by lazy {
        ImagePageAdapter(sharedViewModel.currentDisplayingList, object : ImagePageAdapter.Callback {
            override fun onClick(item: Item?) {
                toggleFullScreenMode()
            }

            override fun onVideoViewClicked(videoView: PlayerView, item: Item?) {
                if (videoView.isControllerVisible) {
                    showFullScreen()
                } else {
                    hideFullScreen()
                }
            }
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
                setUpVideoPlayer()
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
        val videoControllerHeight = if (item.isVideo()) {
            videoController.measuredHeight
        } else {
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

    override fun getCurrentImageItemView(): ImageItemView? {
        return binding.pagerImage.findViewWithTag(item.id)
    }
}