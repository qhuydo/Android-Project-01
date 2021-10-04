package hcmus.android.gallery1.ui.image.list

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.databinding.FragmentMainAllPhotosBinding
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.observeOnce
import hcmus.android.gallery1.helpers.widgets.PullToRefreshLayout
import hcmus.android.gallery1.helpers.widgets.PullToRefreshLayout.Companion.REFRESH_MIN_DELAY
import hcmus.android.gallery1.ui.base.image.ImageListFragment
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class AllPhotosFragment: ImageListFragment<FragmentMainAllPhotosBinding>(tab = TAB.ALL) {

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
}
