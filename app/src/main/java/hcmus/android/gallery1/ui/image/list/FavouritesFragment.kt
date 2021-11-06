package hcmus.android.gallery1.ui.image.list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainFavouritesBinding
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.ui.adapters.binding.doOnApplyWindowInsets
import hcmus.android.gallery1.ui.base.image.ImageListFragment
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.main.ChildOfMainFragment
import hcmus.android.gallery1.ui.main.MainFragment

class FavouritesFragment : ImageListFragment<FragmentMainFavouritesBinding>(
    layoutId = R.layout.fragment_main_favourites,
    tab = TAB.FAV,
    screenConstant = ScreenConstant.IMAGE_LIST_FAVOURITE
), ChildOfMainFragment {

    private val mainFragment by lazy {
        activity?.supportFragmentManager?.findFragmentByTag(MainFragment::class.java.name)
                as? MainFragment
    }

    private val viewModelFactory by lazy {
        FavouritesViewModel.Factory(
            mainActivity!!.favouriteRepository,
            mainActivity!!.preferenceRepository
        )
    }
    private val viewModel by activityViewModels<FavouritesViewModel> { viewModelFactory }

    override fun imageListViewModel(): ImageListViewModel = viewModel

    override fun imageRecyclerView(): RecyclerView = binding.recyclerView

    override fun bindData() = with(binding) {
        viewModel = this@FavouritesFragment.viewModel
    }

    override fun subscribeUi() = with(viewModel) {
        super.subscribeUi()

        photos.observeOnce(viewLifecycleOwner) {
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

    override fun paddingContainerInStatusBarSide() {}

    override fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {
        binding.recyclerView.doOnApplyWindowInsets { view, windowInsets, padding, _, _ ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = padding.top + insets.top,
                bottom = padding.bottom + insets.bottom + peekHeight
            )
        }
    }

    override fun animateFadeUp() {
        binding.recyclerView.apply {
            invisible()
            animateFadeUp()
            visible()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (binding.recyclerView as? ViewGroup)?.clipToPadding = false
        mainFragment?.paddingChildPager(this)
    }

    override fun onPause() {
        TransitionManager.endTransitions(binding.recyclerView)
        super.onPause()
    }
}
