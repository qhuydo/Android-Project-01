package hcmus.android.gallery1.ui.collection.view

import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentViewCustomAlbumBinding
import hcmus.android.gallery1.ui.base.BaseViewCollectionFragment

class ViewCustomAlbumFragment :
    BaseViewCollectionFragment<FragmentViewCustomAlbumBinding>(R.layout.fragment_view_custom_album) {

    companion object {
        const val ARGS_COLLECTION_ID = "collection_id"
    }

    private val collectionId: Long by lazy {
        requireArguments().getLong(ARGS_COLLECTION_ID)
    }

    private val viewModel by viewModels<ViewCustomAlbumViewModel> {
        ViewCustomAlbumViewModel.Factory(
            mainActivity!!.photoRepository,
            mainActivity!!.customAlbumRepository,
            preferenceRepository
        )
    }

    override fun subscribeUi() = with(viewModel) {
        setCollection(collectionId)
        super.subscribeUi()
    }

    override fun bindData() = with(binding) {
        viewModel = this@ViewCustomAlbumFragment.viewModel
    }

    override fun calculatePeekHeight() = with(binding.bottomDrawer) {
        listDivider.measuredHeight + btnCloseCustomAlbum.measuredHeight
    }

    override fun initBottomDrawerElements() {
        binding.bottomDrawer.apply {
            bottomDrawerView = bdrawerViewCustomCollection
            bottomSheetBehavior = BottomSheetBehavior.from(bdrawerViewCustomCollection)
            bottomSheetExpandButton = btnBottomSheetExpand
        }
        bottomDrawerDim = binding.bdrawerDim
    }

    override fun getHiddenRows() = binding.bottomDrawer.customAlbumHiddenElements
    override fun getPhotoRecyclerView() = binding.albumList
    override fun getButtonClose() = binding.bottomDrawer.btnCloseCustomAlbum
    override fun getViewModeView() = binding.bottomDrawer.viewmodeAll

    override fun viewModel() = viewModel
}