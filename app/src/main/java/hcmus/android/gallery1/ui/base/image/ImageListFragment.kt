package hcmus.android.gallery1.ui.base.image

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.ScrollableToTop
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.getSpanCountOf
import hcmus.android.gallery1.helpers.extensions.isCompactLayout
import hcmus.android.gallery1.helpers.navigation.navigateToViewImageFragment
import hcmus.android.gallery1.ui.adapters.recyclerview.ItemListAdapter
import hcmus.android.gallery1.ui.base.BaseFragment
import hcmus.android.gallery1.ui.main.MainFragment
import timber.log.Timber

abstract class ImageListFragment<B : ViewDataBinding>(
    @LayoutRes layoutId: Int = R.layout.fragment_main_all_photos,
    private val tab: TAB = TAB.ALL
) : BaseFragment<B>(layoutId), ScrollableToTop {

    private val itemListAdapterCallback = ItemListAdapter.Callback { _, itemPos ->
        imageListViewModel().navigateToImageView(itemPos)
    }
    protected val itemListAdapter: ItemListAdapter by lazy {
        ItemListAdapter(requireContext(), callback = itemListAdapterCallback)
    }

    abstract fun imageListViewModel(): ImageListViewModel

    abstract fun imageRecyclerView(): RecyclerView

    override fun subscribeUi() = with(imageListViewModel()) {

        navigateToImageView.observe(viewLifecycleOwner) {
            if (it != null) {
                imageListViewModel().setCurrentDisplayingList(sharedViewModel)
                mainActivity?.navigateToViewImageFragment(tab, it, imageListViewModel())
            }
        }

        viewMode.observe(viewLifecycleOwner) {
            if (it != null) {
                initRecyclerView(it)
            }
        }
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

    protected fun startObserveContentChange() {

        sharedViewModel.contentChange.observe(viewLifecycleOwner) {
            Timber.d("Observed content change")
            imageListViewModel().loadData()
            itemListAdapter.notifyDataSetChanged()
        }
    }
}
