package hcmus.android.gallery1.ui.image.list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import hcmus.android.gallery1.databinding.FragmentMainAllPhotosBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.helpers.widgets.PullToRefreshLayout
import hcmus.android.gallery1.helpers.widgets.PullToRefreshLayout.Companion.REFRESH_MIN_DELAY
import hcmus.android.gallery1.ui.adapters.binding.doOnApplyWindowInsets
import hcmus.android.gallery1.ui.base.image.ImageListFragment
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.main.ChildOfMainFragment
import hcmus.android.gallery1.ui.main.MainFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class AllPhotosFragment : ImageListFragment<FragmentMainAllPhotosBinding>(
    tab = TAB.ALL,
    screenConstant = ScreenConstant.IMAGE_LIST_ALL
), ChildOfMainFragment {

    private val mainFragment by lazy {
        activity?.supportFragmentManager?.findFragmentByTag(MainFragment::class.java.name)
                as? MainFragment
    }

    private val viewModel by activityViewModels<AllPhotosViewModel> {
        AllPhotosViewModel.Factory(
            mainActivity!!.photoRepository,
            mainActivity!!.preferenceRepository
        )
    }

    override fun imageListViewModel(): ImageListViewModel = viewModel

    override fun imageRecyclerView(): RecyclerView = binding.recyclerView

    override fun subscribeUi() {
        super.subscribeUi()
        viewModel.photos.observeOnce(viewLifecycleOwner) {
            itemListAdapter.submitList(it)
            binding.allPhotoRefreshLayout.setRefreshing(false)
        }
        // startObservingContentChange()
    }

    override fun bindData() = with(binding) {
        viewModel = this@AllPhotosFragment.viewModel

        allPhotoRefreshLayout.listener = PullToRefreshLayout.Listener {
            lifecycleScope.launch {
                delay(REFRESH_MIN_DELAY)
                this@AllPhotosFragment.viewModel.loadData {
                    Timber.d("loaded")
                    itemListAdapter.notifyDataSetChanged()
                    binding.allPhotoRefreshLayout.setRefreshing(false)
                }
            }
        }
    }

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
        (binding.recyclerView as? ViewGroup)?.clipToPadding = false
        mainFragment?.paddingChildPager(this)
    }

    override fun onPause() {
        TransitionManager.endTransitions(binding.recyclerView)
        super.onPause()
    }
}
