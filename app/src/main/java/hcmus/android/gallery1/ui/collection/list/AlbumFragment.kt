package hcmus.android.gallery1.ui.collection.list

import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.helpers.TAB_ALBUM
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment

class AlbumFragment : CollectionListFragment(tabName = TAB_ALBUM) {

    val viewModel by activityViewModels<AlbumViewModel> {
        AlbumViewModel.Factory(
            mainActivity!!.collectionRepository
        )
    }

    override fun collectionViewModel() = viewModel

    override fun subscribeUi() {
        with(viewModel) {
            collections.observe(viewLifecycleOwner) {
                collectionListAdapter.submitList(it)
            }
            // startObserveContentChange()
        }
    }

}
