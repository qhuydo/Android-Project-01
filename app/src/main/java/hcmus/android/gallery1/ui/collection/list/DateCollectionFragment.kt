package hcmus.android.gallery1.ui.collection.list

import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment

class DateCollectionFragment : CollectionListFragment(tab = TAB.DATE) {

    val viewModel by activityViewModels<DateCollectionViewModel> {
        DateCollectionViewModel.Factory(
            mainActivity!!.collectionRepository,
            mainActivity!!.preferenceRepository
        )
    }

    override fun collectionViewModel() = viewModel

    override fun subscribeUi() {
        super.subscribeUi()
        with(viewModel) {
            collections.observe(viewLifecycleOwner) {
                collectionListAdapter.submitList(it)
            }
            //startObserveContentChange()
        }
    }

}
