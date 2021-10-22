package hcmus.android.gallery1.ui.base.image

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.ScrollableToTop
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.getSpanCountOf
import hcmus.android.gallery1.helpers.extensions.isCompactLayout
import hcmus.android.gallery1.helpers.navigation.navigateToViewImageFragment
import hcmus.android.gallery1.ui.adapters.recyclerview.ItemListAdapter
import hcmus.android.gallery1.ui.base.BaseFragment
import timber.log.Timber

abstract class ImageListFragment<B : ViewDataBinding>(
    @LayoutRes layoutId: Int = R.layout.fragment_main_all_photos,
    private val tab: TAB = TAB.ALL,
    screenConstant: ScreenConstant = ScreenConstant.IMAGE_LIST_ALL
) : BaseFragment<B>(layoutId, screenConstant), ScrollableToTop {

    private val itemListAdapterCallback = ItemListAdapter.Callback { _, itemPos ->
        imageListViewModel().navigateToImageView(itemPos)
    }
    protected val itemListAdapter: ItemListAdapter by lazy {
        ItemListAdapter(callback = itemListAdapterCallback)
    }

    abstract fun imageListViewModel(): ImageListViewModel

    abstract fun imageRecyclerView(): RecyclerView

    override fun subscribeUi() {

        imageListViewModel().navigateToImageView.observe(viewLifecycleOwner) {
            if (it != null) {
                imageListViewModel().setCurrentDisplayingList(sharedViewModel)
                mainActivity?.navigateToViewImageFragment(
                    tab,
                    screenConstant,
                    it,
                    imageListViewModel()
                )
            }
        }

        imageListViewModel().viewMode.observe(viewLifecycleOwner) {
            if (it != null) {
                initRecyclerView(it)
            }
        }
        startObservingItemRemoveLiveData()
    }

    override fun scrollToTop() {
        imageRecyclerView().smoothScrollToPosition(0)
    }

    private fun initRecyclerView(viewMode: String) {
        imageRecyclerView().apply {
            adapter = itemListAdapter
            itemListAdapter.changeCompactLayout(viewMode.isCompactLayout())
            val spanCount = requireContext().getSpanCountOf(tab.key, viewMode)
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    protected fun startObservingContentChange() {

        sharedViewModel.contentChange.observe(viewLifecycleOwner) {
            Timber.d("Observed content change")
            imageListViewModel().loadData()
            itemListAdapter.notifyDataSetChanged()
        }
    }

    protected fun startObservingItemRemoveLiveData() = with(sharedViewModel) {
        removedItem.observe(viewLifecycleOwner) {
            if (it != null) {
                val (item, _, fragmentName) = it
                if (fragmentName != this::class.java.name) {
                    Timber.d("removedItem observe from ${this@ImageListFragment::class.java.name}")
                    imageListViewModel().removeItemFromList(item) { itemPosition ->
                        itemListAdapter.notifyItemRemoved(itemPosition)
                    }
                }
            }
        }
    }
}
