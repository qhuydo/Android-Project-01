package hcmus.android.gallery1.ui.collectionlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.getCollectionsByDate
import hcmus.android.gallery1.ui.base.CollectionListFragment
import hcmus.android.gallery1.helpers.TAB_DATE
import hcmus.android.gallery1.helpers.observeOnce

class TabDateFragment : CollectionListFragment(tabName = TAB_DATE) {

    val viewModel by activityViewModels<DateCollectionViewModel> { DateCollectionViewModel.Factory(
        mainActivity!!.collectionRepository
    ) }

    override fun collectionViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.collections.observeOnce(viewLifecycleOwner) {
            collectionListAdapter.submitList(it)
        }
    }
}
