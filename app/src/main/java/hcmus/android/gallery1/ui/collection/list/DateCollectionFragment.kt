package hcmus.android.gallery1.ui.collection.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.helpers.TAB_DATE
import hcmus.android.gallery1.helpers.observeOnce
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment

class DateCollectionFragment : CollectionListFragment(tabName = TAB_DATE) {

    val viewModel by activityViewModels<DateCollectionViewModel> {
        DateCollectionViewModel.Factory(
            mainActivity!!.collectionRepository
        )
    }

    override fun collectionViewModel() = viewModel

    override fun subscribeUi() {
        with(viewModel) {
            collections.observeOnce(viewLifecycleOwner) {
                collectionListAdapter.submitList(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
