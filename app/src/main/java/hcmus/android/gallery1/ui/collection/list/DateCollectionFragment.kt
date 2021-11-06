package hcmus.android.gallery1.ui.collection.list

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainColllectionListBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.addPadding
import hcmus.android.gallery1.helpers.extensions.animateFadeUp
import hcmus.android.gallery1.helpers.extensions.invisible
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.adapters.binding.doOnApplyWindowInsets
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment
import hcmus.android.gallery1.ui.main.ChildOfMainFragment
import hcmus.android.gallery1.ui.main.MainFragment

class DateCollectionFragment : CollectionListFragment<FragmentMainColllectionListBinding>(
    R.layout.fragment_main_colllection_list,
    tab = TAB.DATE,
    screenConstant = ScreenConstant.COLLECTION_LIST_DATE
), ChildOfMainFragment {

    private val mainFragment by lazy {
        activity?.supportFragmentManager?.findFragmentByTag(MainFragment::class.java.name)
                as? MainFragment
    }

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

    override fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {
        binding.recyclerView.doOnApplyWindowInsets { view, windowInsets, padding, _, _ ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = padding.top + insets.top,
                bottom = padding.bottom + insets.bottom + peekHeight
            )
        }
    }

    override fun paddingContainerInStatusBarSide() {}

    override fun animateFadeUp() {
        binding.recyclerView.apply {
            invisible()
            animateFadeUp()
            visible()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainFragment?.paddingChildPager(this)
    }

    override fun onStop() {
        super.onStop()
        binding.recyclerView.clearAnimation()
    }

    override fun onPause() {
        TransitionManager.endTransitions(binding.recyclerView)
        super.onPause()
    }
}
