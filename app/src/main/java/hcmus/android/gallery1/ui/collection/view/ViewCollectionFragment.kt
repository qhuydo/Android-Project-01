package hcmus.android.gallery1.ui.collection.view

import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.databinding.FragmentViewCollectionBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.ui.base.BaseViewCollectionFragment

class ViewCollectionFragment : BaseViewCollectionFragment<FragmentViewCollectionBinding>(
    R.layout.fragment_view_collection,
    ScreenConstant.COLLECTION_VIEW
) {

    companion object {
        const val ARGS_COLLECTION = "collection"
    }

    // Collection
    private val collection: Collection by lazy {
        requireArguments().getParcelable(ARGS_COLLECTION)!!
    }

    private val viewModel by viewModels<ViewCollectionViewModel> {
        ViewCollectionViewModel.Factory(
            mainActivity!!.photoRepository,
            preferenceRepository
        )
    }

    override fun subscribeUi() = with(viewModel) {
        setCollection(this@ViewCollectionFragment.collection)
        collection.observeOnce(viewLifecycleOwner) { viewModel.getPhotos() }
        super.subscribeUi()
    }

    override fun bindData() = with(binding) {
        collection = this@ViewCollectionFragment.collection
        viewModel = this@ViewCollectionFragment.viewModel
    }

    override fun calculatePeekHeight() = with(binding.bdrawerImageList) {
        listDivider.measuredHeight + topRow.measuredHeight
    }

    override fun initBottomDrawerElements() {
        binding.bdrawerImageList.apply {
            bottomDrawerView = bdrawerImageListStandalone
            bottomSheetBehavior = BottomSheetBehavior.from(bdrawerImageListStandalone)
            bottomSheetExpandButton = btnBottomSheetExpand
        }
        bottomDrawerDim = binding.bdrawerDim
    }

    override fun getHiddenRows() = binding.bdrawerImageList.viewmodeAll.root
    override fun getPhotoRecyclerView() = binding.recyclerView
    override fun getButtonClose() = binding.bdrawerImageList.btnClose
    override fun getViewModeView() = binding.bdrawerImageList.viewmodeAll

    override fun viewModel() = viewModel
}
