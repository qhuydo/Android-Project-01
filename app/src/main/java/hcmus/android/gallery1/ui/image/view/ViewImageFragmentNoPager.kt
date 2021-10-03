package hcmus.android.gallery1.ui.image.view

import android.widget.ImageButton
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentViewImageNopagerBinding
import hcmus.android.gallery1.ui.base.BaseViewImageFragment

class ViewImageFragmentNoPager : BaseViewImageFragment<FragmentViewImageNopagerBinding>(
    R.layout.fragment_view_image_nopager
) {
    // private val CREATE_FILE: Int = 1

    override fun getBottomDrawer() = binding.bdrawerViewImageLayout

    override fun getBottomDrawerDimView() = binding.bdrawerViewImageDim

    override fun subscribeUi() {
        with(viewModel) {
            item.observe(viewLifecycleOwner) {
                if (it != null) {
                    this@ViewImageFragmentNoPager.item = it
                    binding.photoViewModel = this
                    binding.executePendingBindings()
                }
            }
        }
    }

    override fun bindData() {
        binding.fragment = this
    }

    // A dirty workaround to disable (nearly) all buttons when an external URI is detected
    // (i.e. an image was opened but NOT from the gallery)
    private fun workaroundDisableButtons() = binding.bdrawerViewImageLayout.let {
        val toDisableBtns: List<ImageButton> = listOf(
            it.btnDelete,
            it.btnCopy,
            it.btnMove,
            it.btnSlideshow,
            it.btnFavorite
        )
        for (each in toDisableBtns) {
            each.isEnabled = false
            each.alpha = 0.25f
        }
    }

    override fun notifyItemRemoved() = closeViewer()
}