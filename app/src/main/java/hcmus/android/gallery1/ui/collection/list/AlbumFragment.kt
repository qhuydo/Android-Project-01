package hcmus.android.gallery1.ui.collection.list

import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainAlbumBinding
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.ui.adapters.recyclerview.CollectionListAdapter
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment

class AlbumFragment : CollectionListFragment<FragmentMainAlbumBinding>(
    R.layout.fragment_main_album,
    tab = TAB.ALBUM
) {
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

    override fun getAlbumRecyclerView() = binding.albumList

    override fun getPullToRefreshLayout() = binding.albumPullToRefresh

    override fun scrollToTop() {
        binding.scrollView.smoothScrollTo(0, 0)
    }

}

