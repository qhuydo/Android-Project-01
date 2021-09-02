package hcmus.android.gallery1.ui.imagelist

import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.data.getItems
import hcmus.android.gallery1.ui.base.ImageListFragment
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.TAB_ALL
import hcmus.android.gallery1.ui.base.ImageListViewModel

class TabAllFragment: ImageListFragment(tabName = TAB_ALL) {

    private val viewModel by activityViewModels<ImageListViewModel> { ImageListViewModel.Factory() }

    override fun getItemList(): List<Item> {
        return requireContext().contentResolver.getItems(null)
    }

    override fun imageListViewModel(): ImageListViewModel = viewModel

}
