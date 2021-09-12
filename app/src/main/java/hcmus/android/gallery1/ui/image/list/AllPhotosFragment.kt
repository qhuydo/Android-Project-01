package hcmus.android.gallery1.ui.image.list

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import hcmus.android.gallery1.helpers.TAB_ALL
import hcmus.android.gallery1.helpers.observeOnce
import hcmus.android.gallery1.helpers.widgets.PullToRefreshLayout
import hcmus.android.gallery1.ui.base.image.ImageListFragment
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllPhotosFragment: ImageListFragment(tabName = TAB_ALL) {

    private val viewModel by activityViewModels<AllPhotosViewModel> {
        AllPhotosViewModel.Factory(
            mainActivity!!.photoRepository
        )
    }

    override fun imageListViewModel(): ImageListViewModel = viewModel

    override fun subscribeUi() {
        viewModel.photos.observeOnce(viewLifecycleOwner) {
            itemListAdapter.submitList(it)
        }
        //startObserveContentChange()
    }

    override fun bindData() {
        super.bindData()
        binding.allPhotoRefreshLayout.listener = PullToRefreshLayout.Listener {
            lifecycleScope.launch {
                delay(4000)
                binding.allPhotoRefreshLayout.setRefreshing(false)
            }
        }
    }
}
