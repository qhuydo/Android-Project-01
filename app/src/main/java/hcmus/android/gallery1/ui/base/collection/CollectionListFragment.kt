package hcmus.android.gallery1.ui.base.collection

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainColllectionListBinding
import hcmus.android.gallery1.helpers.ScrollableToTop
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.getSpanCountOf
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

    protected lateinit var collectionListAdapter: CollectionListAdapter

    private val adapterCallback = CollectionListAdapter.Callback { collection ->
        collectionViewModel().navigateToCollectionDetails(collection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionListAdapter = CollectionListAdapter(
            isCompactLayout = preferenceRepository.isCompactLayout(tab.key),
            callback = adapterCallback
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectionViewModel().navigateToCollectionDetails.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }

            mainActivity?.navigateToViewCollectionFragment(it)
        }

        binding.albumPullToRefresh.listener = PullToRefreshLayout.Listener {
            lifecycleScope.launch {
                delay(PullToRefreshLayout.REFRESH_MIN_DELAY)
                collectionViewModel().loadData {
                    collectionListAdapter.notifyDataSetChanged()
                    binding.albumPullToRefresh.setRefreshing(false)
                }
            }
        }
    }

    override fun scrollToTop() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    override fun bindData() {
        initRecyclerView()
    }

    fun notifyViewTypeChanged() {

        val collections = collectionListAdapter.currentList
        collectionListAdapter = CollectionListAdapter(
            isCompactLayout = preferenceRepository.isCompactLayout(tab.key),
            callback = adapterCallback
        ).also {
            it.submitList(collections)
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = collectionListAdapter
            val spanCount = requireContext().getSpanCountOf(
                tab.key,
                preferenceRepository.getViewMode(tab.key)
            )
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
