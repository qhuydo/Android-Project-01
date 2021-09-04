package hcmus.android.gallery1.ui.collection.list

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment
import hcmus.android.gallery1.helpers.TAB_ALBUM
import hcmus.android.gallery1.helpers.observeOnce

class AlbumFragment : CollectionListFragment(tabName = TAB_ALBUM) {

    val viewModel by activityViewModels<AlbumViewModel> {
        AlbumViewModel.Factory(
            mainActivity!!.collectionRepository
        )
    }

    override fun collectionViewModel() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.collections.observeOnce(this) {
            collectionListAdapter.submitList(it)
        }
    }

}
