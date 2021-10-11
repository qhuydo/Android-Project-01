package hcmus.android.gallery1.ui.collection.list

import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainColllectionListBinding
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment

class DateCollectionFragment : CollectionListFragment<FragmentMainColllectionListBinding>(
    R.layout.fragment_main_colllection_list,
    tab = TAB.DATE
) {

    val viewModel by activityViewModels<DateCollectionViewModel> {
        DateCollectionViewModel.Factory(
            mainActivity!!.collectionRepository,
            mainActivity!!.preferenceRepository
        )
    }

    override fun collectionViewModel() = viewModel

    override fun bindData() = with(binding) {
        super.bindData()
        viewModel = collectionViewModel()
    }

    override fun subscribeUi() {
        super.subscribeUi()
        with(viewModel) {
            collections.observe(viewLifecycleOwner) {
                collectionListAdapter.submitList(it)
            }
        }
    }

    override fun getAlbumRecyclerView() = binding.recyclerView

    override fun getPullToRefreshLayout() = binding.albumPullToRefresh

}
