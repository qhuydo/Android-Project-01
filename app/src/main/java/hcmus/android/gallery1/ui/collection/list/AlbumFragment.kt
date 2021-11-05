package hcmus.android.gallery1.ui.collection.list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainAlbumBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.animateFadeUp
import hcmus.android.gallery1.helpers.extensions.invisible
import hcmus.android.gallery1.helpers.extensions.padding
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.adapters.recyclerview.CollectionListAdapter
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment
import hcmus.android.gallery1.ui.main.ChildOfMainFragment
import hcmus.android.gallery1.ui.main.MainFragment

class AlbumFragment : CollectionListFragment<FragmentMainAlbumBinding>(
    R.layout.fragment_main_album,
    tab = TAB.ALBUM,
    screenConstant = ScreenConstant.COLLECTION_LIST_ALBUM
), ChildOfMainFragment {

    private val mainFragment by lazy {
        activity?.supportFragmentManager?.findFragmentByTag(MainFragment::class.java.name)
                as? MainFragment
    }

    private val customAlbumCallback = CollectionListAdapter.Callback { collection ->
        collectionViewModel().navigateToCollectionDetails(collection)
    }

    private val customAlbumAdapter = CollectionListAdapter(callback = customAlbumCallback)

    val viewModel by activityViewModels<AlbumViewModel> {
        AlbumViewModel.Factory(
            mainActivity!!.collectionRepository,
            mainActivity!!.customAlbumRepository,
            mainActivity!!.preferenceRepository
        )
    }

    override fun collectionViewModel() = viewModel

    override fun bindData() = with(binding) {
        super.bindData()
        viewModel = this@AlbumFragment.viewModel
    }

    override fun subscribeUi() {
        super.subscribeUi()
        with(viewModel) {
            collections.observe(viewLifecycleOwner) {
                collectionListAdapter.submitList(it)
            }

            customAlbums.observe(viewLifecycleOwner) { list ->
                if (list != null) {
                    customAlbumAdapter.submitList(list)
                }
            }

            viewMode.observe(viewLifecycleOwner) { viewMode ->
                if (viewMode != null) {
                    binding.customAlbumList.initRecyclerView(viewMode, customAlbumAdapter)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainFragment?.paddingChildPager(this)
    }

    override fun getAlbumRecyclerView() = binding.albumList

    override fun getPullToRefreshLayout() = binding.albumPullToRefresh

    override fun scrollToTop() {
        binding.scrollView.smoothScrollTo(0, 0)
    }

    override fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {
        binding.scrollView.padding(bottom = peekHeight)
    }

    override fun paddingContainerInStatusBarSide() {
        mainActivity?.setViewPaddingInStatusBarSide(binding.scrollView)
        binding.albumPullToRefresh.apply {
            shouldUpdateTargetView = true
            mainActivity?.setViewPaddingInStatusBarSide(refreshView)
        }
    }

    override fun animateFadeUp() {
        (binding.root as? ViewGroup)?.apply {
            invisible()
            animateFadeUp()
            visible()
        }
    }

    override fun onPause() {
        TransitionManager.endTransitions(binding.root as? ViewGroup)
        super.onPause()
    }
}

