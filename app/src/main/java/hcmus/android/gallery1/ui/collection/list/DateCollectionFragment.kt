package hcmus.android.gallery1.ui.collection.list

import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.helpers.TAB_DATE
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
            collections.observe(viewLifecycleOwner) {
                collectionListAdapter.submitList(it)
            }
            //startObserveContentChange()
        }
    }

}
