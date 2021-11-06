package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialSharedAxis
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeItemBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.helpers.navigation.navigateToViewImageFragment
import hcmus.android.gallery1.ui.adapters.recyclerview.ItemListAdapter
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.main.MainFragment
import timber.log.Timber

abstract class BaseViewCollectionFragment<B : ViewDataBinding>(
    @LayoutRes resId: Int,
    screenConstant: ScreenConstant
) : BottomDrawerFragment<B>(resId, screenConstant) {

    private val mainFragment by lazy {
        activity?.supportFragmentManager?.findFragmentByTag(MainFragment::class.java.name)
                as? MainFragment
    }

    protected val tab = TAB.ALL

    private val itemListAdapterCallback = ItemListAdapter.Callback { _, itemPosition ->
        viewModel().navigateToImageView(itemPosition)
    }

    protected val itemListAdapter: ItemListAdapter by lazy {
        ItemListAdapter(callback = itemListAdapterCallback)
    }

    abstract fun getPhotoRecyclerView(): RecyclerView
    abstract fun getButtonClose(): ImageButton
    abstract fun getViewModeView(): ButtonGroupViewmodeItemBinding
    abstract fun viewModel(): ImageListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity?.setViewPaddingWindowInset(getPhotoRecyclerView())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun subscribeUi() = with(viewModel()) {

        photos.observeOnce(viewLifecycleOwner) { itemListAdapter.submitList(it) }

        navigateToImageView.observe(viewLifecycleOwner) {
            if (it != null) {
                mainActivity?.navigateToViewImageFragment(tab, screenConstant, it, this)
            }
        }

        viewMode.observe(viewLifecycleOwner) {
            if (it != null) {
                mainFragment?.notifyViewModeChange(tab)
                initRecyclerView(it)
            }
        }

        startObservingItemRemoveLiveData()

    }

    override fun initBottomDrawerElementsCallback() {
        super.initBottomDrawerElementsCallback()

        getButtonClose().setOnClickListener { closeCollection() }

        getViewModeView().viewmodeItem.addOnButtonCheckedListener { _, checkedId, _ ->
            val viewMode = checkedId.viewIdToViewMode()
            preferenceRepository.setViewMode(tab.key, viewMode)
            animateViewModeChange()
        }
    }

    private fun animateViewModeChange() {
        val sharedAxis = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        TransitionManager.beginDelayedTransition(getPhotoRecyclerView(), sharedAxis)
    }

    override fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {
        getPhotoRecyclerView().setPadding(0, 0, 0, peekHeight)
        mainActivity?.setViewPaddingInNavigationBarSide(
            binding.root,
            usePaddingBottomNavigationBar = false
        )
    }

    private fun initRecyclerView(viewMode: String) {
        getPhotoRecyclerView().apply {
            adapter = itemListAdapter
            itemListAdapter.changeCompactLayout(viewMode.isCompactLayout())
            val spanCount = requireContext().getSpanCountOf(tab.key, viewMode)
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    private fun closeCollection() {
        forceBack = true
        activity?.onBackPressed()
    }

    private fun startObservingItemRemoveLiveData() = with(viewModel()) {
        sharedViewModel.removedItem.observe(viewLifecycleOwner) {
            if (it != null) {
                val (item, _, fragmentName) = it

                if (fragmentName != this::class.java.name) {
                    Timber.d(
                        "removedItem observe from ${
                            this@BaseViewCollectionFragment::class.java.name
                        }"
                    )
                    removeItemFromList(item) { itemPosition ->
                        itemListAdapter.notifyItemRemoved(itemPosition)
                    }
                }
            }

        }
    }
}
