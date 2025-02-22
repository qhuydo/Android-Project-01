package hcmus.android.gallery1.ui.image.view

import android.widget.ImageButton
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentViewImageNopagerBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.base.BaseViewImageFragment
import hcmus.android.gallery1.ui.widgets.ImageItemView

class ViewImageFragmentNoPager : BaseViewImageFragment<FragmentViewImageNopagerBinding>(
    R.layout.fragment_view_image_nopager,
    ScreenConstant.IMAGE_VIEW_NO_PAGER
) {
    // private val CREATE_FILE: Int = 1

    override fun getBottomDrawer() = binding.bdrawerViewImageLayout

    override fun getBottomDrawerDimView() = binding.bdrawerViewImageDim

    override fun subscribeUi() {
        super.subscribeUi()

        with(viewModel) {
            item.observe(viewLifecycleOwner) {
                if (it != null) {
                    this@ViewImageFragmentNoPager.item = it
                    getBottomDrawer().bdrawerViewImage.visible()
                    setUpVideoPlayer()
                }
            }
        }
    }

    override fun bindData() {
        binding.fragment = this
        binding.photoViewModel = viewModel
    }

    // A dirty workaround to disable (nearly) all buttons when an external URI is detected
    // (i.e. an image was opened but NOT from the gallery)
    private fun workaroundDisableButtons() = binding.bdrawerViewImageLayout.let {
        val toDisableBtns: List<ImageButton> = listOf(
            it.btnDelete,
            it.btnCopy,
            it.btnAddItemIntoAlbums,
            it.btnSlideshow,
            it.btnFavorite
        )
        for (each in toDisableBtns) {
            each.isEnabled = false
            each.alpha = 0.25f
        }
    }

    override fun notifyItemRemoved() = closeViewer()

    override fun getCurrentImageItemView(): ImageItemView = binding.image

}