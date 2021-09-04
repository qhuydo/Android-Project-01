package hcmus.android.gallery1.ui.imagelist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.ui.base.imagelist.ImageListFragment
import hcmus.android.gallery1.helpers.TAB_FAV
import hcmus.android.gallery1.helpers.observeOnce
import hcmus.android.gallery1.ui.base.imagelist.ImageListViewModel

class TabFavoritesFragment : ImageListFragment(tabName = TAB_FAV) {

    private val viewModelFactory by lazy {
        FavouriteViewModel.Factory(mainActivity!!.favouriteRepository)
    }
    private val viewModel by activityViewModels<FavouriteViewModel> { viewModelFactory }

    override fun imageListViewModel(): ImageListViewModel = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.favourites.observeOnce(viewLifecycleOwner) {
            itemListAdapter.submitList(it)
        }
        viewModel.listStateChangeEvent.observe(viewLifecycleOwner) {
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
