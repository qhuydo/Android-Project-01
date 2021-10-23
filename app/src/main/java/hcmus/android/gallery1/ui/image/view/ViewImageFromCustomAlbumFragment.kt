package hcmus.android.gallery1.ui.image.view

import androidx.fragment.app.viewModels
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.extensions.gone
import hcmus.android.gallery1.helpers.extensions.observeOnce
import hcmus.android.gallery1.helpers.extensions.visible
import hcmus.android.gallery1.ui.collection.view.ViewCustomAlbumFragment
import hcmus.android.gallery1.ui.collection.view.ViewCustomAlbumViewModel

class ViewImageFromCustomAlbumFragment :
    ViewImageFragment(screenConstant = ScreenConstant.IMAGE_VIEW_FROM_CUSTOM_ALBUM) {

    private val viewCustomAlbumViewModel by viewModels<ViewCustomAlbumViewModel>(
        ownerProducer = {
            parentFragmentManager.findFragmentByTag(ViewCustomAlbumFragment::class.java.name)
                ?: this
        }
    ) {
        ViewCustomAlbumViewModel.Factory(
            mainActivity!!.photoRepository,
            mainActivity!!.customAlbumRepository,
            preferenceRepository
        )
    }

    override fun bindData() = with(binding) {
        super.bindData()

        bdrawerViewImageLayout.run {
            btnRemoveItemFromAlbum.visible()
            btnRemoveItemFromAlbum.setOnClickListener { removeAlbum() }
            btnAddItemIntoAlbums.gone()
        }
    }

    private fun removeAlbum() {
        viewCustomAlbumViewModel.removeItemFromCustomAlbum(item).observeOnce(viewLifecycleOwner) {
            notifyItemRemoved()
        }
    }

    companion object {
        const val ARGS_CUSTOM_ALBUM_ID = "custom_album_id"
    }
}