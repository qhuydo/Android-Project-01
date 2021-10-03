package hcmus.android.gallery1.ui.base.collection

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainColllectionListBinding
import hcmus.android.gallery1.helpers.ScrollableToTop
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.getSpanCountOf
import hcmus.android.gallery1.helpers.extensions.isCompactLayout
import hcmus.android.gallery1.helpers.navigation.navigateToViewCollectionFragment
import hcmus.android.gallery1.helpers.widgets.PullToRefreshLayout
import hcmus.android.gallery1.ui.adapters.recyclerview.CollectionListAdapter
import hcmus.android.gallery1.ui.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class CollectionListFragment(private val tab: TAB = TAB.ALBUM) :
    BaseFragment<FragmentMainColllectionListBinding>(R.layout.fragment_main_colllection_list),
    ScrollableToTop {


    private val adapterCallback = CollectionListAdapter.Callback { collection ->
        collectionViewModel().navigateToCollectionDetails(collection)
    }

    protected val collectionListAdapter = CollectionListAdapter(callback = adapterCallback)

    override fun scrollToTop() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    override fun bindData() = with(binding) {
        albumPullToRefresh.listener = PullToRefreshLayout.Listener {
            lifecycleScope.launch {
                delay(PullToRefreshLayout.REFRESH_MIN_DELAY)
                collectionViewModel().loadData {
                    collectionListAdapter.notifyDataSetChanged()
                    binding.albumPullToRefresh.setRefreshing(false)
                }
            }
        }
    }

    override fun subscribeUi() = with(collectionViewModel()) {
        navigateToCollectionDetails.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            mainActivity?.navigateToViewCollectionFragment(it)
        }
        viewMode.observe(viewLifecycleOwner) {
            if (it != null) {
                initRecyclerView(it)
            }
        }
    }

    private fun initRecyclerView(viewMode: String) {
        binding.recyclerView.apply {
            adapter = collectionListAdapter
            collectionListAdapter.changeCompactLayout(viewMode.isCompactLayout())
            val spanCount = requireContext().getSpanCountOf(tab.key, viewMode)
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    protected fun startObserveContentChange() {

        sharedViewModel.contentChange.observe(viewLifecycleOwner) {
            Timber.d("Observed content change")
            collectionViewModel().loadData()
        }
    }

    abstract fun collectionViewModel(): CollectionListViewModel

}
