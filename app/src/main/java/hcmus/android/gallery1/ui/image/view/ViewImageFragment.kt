package hcmus.android.gallery1.ui.image.view

import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentViewImageBinding
import hcmus.android.gallery1.ui.base.BaseViewImageFragment

class ViewImageFragment : BaseViewImageFragment<FragmentViewImageBinding>(
    R.layout.fragment_view_image
) {

    override fun getBottomDrawer() = binding.bdrawerViewImageLayout

    override fun getBottomDrawerDimView() = binding.bdrawerViewImageDim

    override fun bindData() {}

    override fun subscribeUi() {}

}