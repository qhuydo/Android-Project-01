package hcmus.android.gallery1.ui.image.view

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentViewImageBinding
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
                this@ViewImageFragment.item = it
                binding.photoViewModel = this
                binding.executePendingBindings()
            }
        }
    }

    override fun bindData() = binding.run {
        fragment = this@ViewImageFragment
        pagerImage.run {
            adapter = pagerAdapter
            registerOnPageChangeCallback(onPageChangeCallback)
            setCurrentItem(sharedViewModel.currentDisplayingItemPos, false)
        }
    }

    override fun onBackPressed() = super.onBackPressed().also {
        if (it) {
            sharedViewModel.currentDisplayingList = null
        }
    }

    override fun notifyItemRemoved() {
        pagerAdapter.notifyItemRemoved(sharedViewModel.currentDisplayingItemPos)

        val currentPos = binding.pagerImage.currentItem
        sharedViewModel.currentDisplayingList?.getOrNull(currentPos)?.let {
            viewModel.setItem(it)
        }

        if (sharedViewModel.currentDisplayingList?.isEmpty() == true) closeViewer()
    }
}