package hcmus.android.gallery1.ui.base.collection

import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.ScrollableToTop
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.getSpanCountOf
import hcmus.android.gallery1.helpers.extensions.isCompactLayout
import hcmus.android.gallery1.helpers.navigation.navigateToViewCollectionFragment
import hcmus.android.gallery1.helpers.navigation.navigateToViewCustomAlbumFragment
import hcmus.android.gallery1.ui.adapters.recyclerview.CollectionListAdapter
import hcmus.android.gallery1.ui.adapters.recyclerview.CollectionListViewHolder
import hcmus.android.gallery1.ui.base.BaseFragment
import hcmus.android.gallery1.ui.widgets.PullToRefreshLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference

abstract class CollectionListFragment<B : ViewDataBinding>(
    @LayoutRes layoutId: Int,
    private val tab: TAB = TAB.ALBUM,
    screenConstant: ScreenConstant = ScreenConstant.COLLECTION_LIST_ALBUM,
) : BaseFragment<B>(layoutId, screenConstant), ScrollableToTop {

    private val adapterCallback = CollectionListAdapter.Callback { collection, holder ->
        collectionViewModel().navigateToCollectionDetails(collection)
        takeSharedElement(holder, collection)
    }

    protected val collectionListAdapter = CollectionListAdapter(callback = adapterCallback)
    private var sharedElementView = WeakReference<CollectionListViewHolder>(null)
    private var sharedElementName: String? = null

    override fun scrollToTop() {
        getAlbumRecyclerView().smoothScrollToPosition(0)
    }

    override fun bindData() {

        getPullToRefreshLayout()?.listener = PullToRefreshLayout.Listener {
            lifecycleScope.launch {
                delay(PullToRefreshLayout.REFRESH_MIN_DELAY)
                collectionViewModel().loadData {
                    collectionListAdapter.notifyDataSetChanged()
                    getPullToRefreshLayout()?.setRefreshing(false)
                }
            }
        }
    }

    override fun subscribeUi() = with(collectionViewModel()) {
        navigateToCollectionDetails.observe(viewLifecycleOwner) { collection ->
            if (collection == null) {
                return@observe
            }

            when (collection.type) {
                Collection.TYPE_CUSTOM -> mainActivity?.navigateToViewCustomAlbumFragment(
                    collection.id,
                    sharedElementView.get()?.itemView,
                    sharedElementName
                )
                else -> {
                    mainActivity?.navigateToViewCollectionFragment(
                        collection,
                        sharedElementView.get()?.itemView,
                        sharedElementName
                    )
                }
            }
        }
        viewMode.observe(viewLifecycleOwner) {
            if (it != null) {
                getAlbumRecyclerView().initRecyclerView(it, collectionListAdapter)
            }
        }
        startObservingContentChange()
    }

    protected fun RecyclerView.initRecyclerView(viewMode: String, adapter: CollectionListAdapter) {
        this.adapter = adapter
        adapter.changeCompactLayout(viewMode.isCompactLayout())
        val spanCount = requireContext().getSpanCountOf(tab.key, viewMode)
        layoutManager = GridLayoutManager(requireContext(), spanCount)
    }

    private fun startObservingContentChange() {

        sharedViewModel.contentChange.observe(viewLifecycleOwner) {
            Timber.d("Observed content change")
            collectionViewModel().loadData()
        }
    }

    abstract fun collectionViewModel(): CollectionListViewModel

    abstract fun getAlbumRecyclerView(): RecyclerView

    abstract fun getPullToRefreshLayout(): PullToRefreshLayout?

    protected fun takeSharedElement(
        holder: CollectionListViewHolder,
        collection: Collection
    ) {
        sharedElementView = WeakReference(holder)
        sharedElementName = "${collection.id}"
        ViewCompat.setTransitionName(holder.itemView, sharedElementName)
    }
}
