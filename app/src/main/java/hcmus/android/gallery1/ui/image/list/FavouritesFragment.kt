package hcmus.android.gallery1.ui.image.list

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainFavouritesBinding
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.observeOnce
import hcmus.android.gallery1.ui.base.image.ImageListFragment
import hcmus.android.gallery1.ui.base.image.ImageListViewModel

class FavouritesFragment : ImageListFragment<FragmentMainFavouritesBinding>(
    layoutId = R.layout.fragment_main_favourites,
    tab = TAB.FAV
) {

    private val viewModelFactory by lazy {
        FavouritesViewModel.Factory(
            mainActivity!!.favouriteRepository,
            mainActivity!!.preferenceRepository
        )
    }
    private val viewModel by activityViewModels<FavouritesViewModel> { viewModelFactory }

    override fun imageListViewModel(): ImageListViewModel = viewModel

    override fun imageRecyclerView(): RecyclerView = binding.recyclerView

    override fun bindData() {

    }

    override fun subscribeUi() = with(viewModel) {
        super.subscribeUi()

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
                else -> {
                }
            }
        }
    }

}
