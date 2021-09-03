package hcmus.android.gallery1.ui.imagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.ui.base.ImageListFragment
import hcmus.android.gallery1.helpers.TAB_ALL
import hcmus.android.gallery1.helpers.observeOnce
import hcmus.android.gallery1.ui.base.ImageListViewModel

class TabAllFragment: ImageListFragment(tabName = TAB_ALL) {

    private val viewModel by activityViewModels<AllPhotosViewModel> { AllPhotosViewModel.Factory(
        mainActivity!!.photoRepository
    ) }

    override fun imageListViewModel(): ImageListViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.photos.observeOnce(viewLifecycleOwner) {
            itemListAdapter.submitList(it)
        }

        return view
    }
}
