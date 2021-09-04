package hcmus.android.gallery1.ui.image.list

import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.helpers.TAB_FAV
import hcmus.android.gallery1.helpers.observeOnce
import hcmus.android.gallery1.ui.base.image.ImageListFragment
import hcmus.android.gallery1.ui.base.image.ImageListViewModel

class FavouritesFragment : ImageListFragment(tabName = TAB_FAV) {

    private val viewModelFactory by lazy {
        FavouritesViewModel.Factory(mainActivity!!.favouriteRepository)
    }
    private val viewModel by activityViewModels<FavouritesViewModel> { viewModelFactory }

    override fun imageListViewModel(): ImageListViewModel = viewModel

    override fun subscribeUi() {
        with(viewModel) {

            favourites.observeOnce(viewLifecycleOwner) {
                itemListAdapter.submitList(it)
            }
            listStateChangeEvent.observe(viewLifecycleOwner) {
                when (it) {
                    is RecyclerViewListState.ItemInserted -> {
                        itemListAdapter.notifyItemInserted(it.position)
                    }

                    is RecyclerViewListState.ItemRemoved -> {
                        itemListAdapter.notifyItemRemoved(it.position)
                    }
                }
            }

        }
    }

}
