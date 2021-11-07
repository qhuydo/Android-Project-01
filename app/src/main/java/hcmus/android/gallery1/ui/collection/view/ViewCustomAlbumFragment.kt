package hcmus.android.gallery1.ui.collection.view

import android.animation.LayoutTransition
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentViewCustomAlbumBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.extensions.toast
import hcmus.android.gallery1.repository.RemoveAlbumResult
import hcmus.android.gallery1.ui.base.BaseViewCollectionFragment
import hcmus.android.gallery1.ui.dialog.AddPhotosIntoAlbumDialog.Companion.showAddPhotosIntoAlbumDialog
import hcmus.android.gallery1.ui.dialog.RenameAlbumDialog.Companion.showRenameAlbumDialog

class ViewCustomAlbumFragment : BaseViewCollectionFragment<FragmentViewCustomAlbumBinding>(
    R.layout.fragment_view_custom_album,
    ScreenConstant.COLLECTION_VIEW_CUSTOM_ALBUM
) {

    companion object {
        const val ARGS_COLLECTION_ID = "collection_id"
    }

    private val collectionId: Long by lazy {
        requireArguments().getLong(ARGS_COLLECTION_ID)
    }

    private val viewModel by viewModels<ViewCustomAlbumViewModel>(
        ownerProducer = { this }
    ) {
        ViewCustomAlbumViewModel.Factory(
            mainActivity!!.photoRepository,
            mainActivity!!.customAlbumRepository,
            preferenceRepository
        )
    }

//    private val photosViewModel by activityViewModels<AllPhotosViewModel>()
//
//    private val addPhotoAdapter by lazy {
//        ItemListAdapter(isCompactLayout = false)
//    }

    override fun subscribeUi() = with(viewModel) {
        setCollection(collectionId)
        super.subscribeUi()
        photos.observe(viewLifecycleOwner) {
            if (it != null) {
                enableButtonGroup()
            }
        }
        listStateChangeEvent.observe(viewLifecycleOwner) {
            itemListAdapter.notifyDataSetChanged()
        }
    }

    override fun bindData() = with(binding) {
        viewModel = this@ViewCustomAlbumFragment.viewModel
        fm = this@ViewCustomAlbumFragment
    }

    override fun calculatePeekHeight() = with(binding.bottomDrawer) {
        listDivider.measuredHeight + btnCloseCustomAlbum.measuredHeight
    }

    override fun initBottomDrawerElements() {
        binding.bottomDrawer.apply {
            bottomDrawerView = container
            bottomSheetBehavior = BottomSheetBehavior.from(container)
            bottomSheetExpandButton = btnBottomSheetExpand
        }
        bottomDrawerDim = binding.bdrawerDim
        binding.bottomDrawer.container.layoutTransition = LayoutTransition().apply {
            setAnimateParentHierarchy(false)
        }
    }

    override fun getHiddenRows() = binding.bottomDrawer.customAlbumHiddenElements
    override fun getPhotoRecyclerView() = binding.albumList
    override fun getButtonClose() = binding.bottomDrawer.btnCloseCustomAlbum
    override fun getViewModeView() = binding.bottomDrawer.viewmodeAll

    override fun viewModel() = viewModel

    private fun enableButtonGroup() = with(binding.bottomDrawer) {
        listOf(
            btnAddPhoto,
            btnRemoveAlbum,
            btnRenameAlbum,
        ).forEach { it.isEnabled = true }
    }

    fun removeAlbum() {
        viewModel.removeCollection().observe(viewLifecycleOwner) {
            if (it == null) return@observe

            when (it) {
                RemoveAlbumResult.SUCCEED -> forceBackPress()
                RemoveAlbumResult.FAILED -> toast("Failed")
            }
        }
    }

    fun renameAlbum() {
        showRenameAlbumDialog()
    }

    fun addPhotosIntoAlbum() {
        mainActivity?.showAddPhotosIntoAlbumDialog()
    }
}