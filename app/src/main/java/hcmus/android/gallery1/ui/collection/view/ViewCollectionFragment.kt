package hcmus.android.gallery1.ui.collection.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButtonToggleGroup
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.databinding.FragmentViewCollectionBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.helpers.extensions.getSpanCountOf
import hcmus.android.gallery1.helpers.extensions.observeOnce
import hcmus.android.gallery1.helpers.extensions.padding
import hcmus.android.gallery1.helpers.navigation.navigateToViewImageFragment
import hcmus.android.gallery1.ui.adapters.recyclerview.ItemListAdapter
import hcmus.android.gallery1.ui.base.BottomDrawerFragment

class ViewCollectionFragment :
    BottomDrawerFragment<FragmentViewCollectionBinding>(R.layout.fragment_view_collection) {

    companion object {
        const val ARGS_COLLECTION = "collection"
    }

    private lateinit var viewModeSelector: MaterialButtonToggleGroup

    // Collection
    private val collection: Collection by lazy {
        requireArguments().getParcelable(ARGS_COLLECTION)!!
    }

    private val itemListAdapterCallback = ItemListAdapter.Callback { _, itemPosition ->
        viewModel.navigateToImageView(itemPosition)
    }

    private val viewModel by viewModels<ViewCollectionViewModel> {
        ViewCollectionViewModel.Factory(mainActivity!!.photoRepository)
    }

    private lateinit var itemListAdapter: ItemListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity?.setViewPaddingWindowInset(binding.recyclerView)

        itemListAdapter = ItemListAdapter(
            requireContext(),
            isCompactLayout = preferenceRepository.isCompactLayout(TAB_ALL),
            callback = itemListAdapterCallback
        )
    }

    override fun subscribeUi() {
        with(viewModel) {

            setCollection(this@ViewCollectionFragment.collection)

            collection.observeOnce(viewLifecycleOwner) {
                viewModel.getPhotos()
                refreshCollection()
            }

            photos.observeOnce(viewLifecycleOwner) { itemListAdapter.submitList(it) }

            navigateToImageView.observe(viewLifecycleOwner) {
                if (it != null) {
                    mainActivity?.navigateToViewImageFragment(it, viewModel)
                }
            }
        }
    }

    override fun bindData() {
        binding.collection = collection
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
        viewModeSelector = binding.bdrawerImageList.viewmodeAll
    }

    override fun initBottomDrawerElementsCallback() {
        super.initBottomDrawerElementsCallback()
        viewModeSelector.check(
            when (preferenceRepository.tabAllViewMode) {
                VIEW_LIST -> R.id.btn_viewmode_all_list
                VIEW_ITEM_GRID_L -> R.id.btn_viewmode_all_grid_3
                VIEW_ITEM_GRID_M -> R.id.btn_viewmode_all_grid_4
                VIEW_ITEM_GRID_S -> R.id.btn_viewmode_all_grid_5
                else -> R.id.btn_viewmode_all_grid_3
            }
        )

        viewModeSelector.addOnButtonCheckedListener { _, checkedId, _ ->

            preferenceRepository.tabAllViewMode = when (checkedId) {
                R.id.btn_viewmode_all_list -> VIEW_LIST
                R.id.btn_viewmode_all_grid_3 -> VIEW_ITEM_GRID_L
                R.id.btn_viewmode_all_grid_4 -> VIEW_ITEM_GRID_M
                R.id.btn_viewmode_all_grid_5 -> VIEW_ITEM_GRID_S
                else -> preferenceRepository.tabAllViewMode
            }

            // Dirty reload the current RecyclerView
            refreshCollection()
        }

        binding.bdrawerImageList.btnClose.setOnClickListener {
            closeCollection()
        }
    }

    override fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {
        binding.recyclerView.padding(bottom = peekHeight)
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = itemListAdapter
            val spanCount =
                requireContext().getSpanCountOf(TAB_ALL, preferenceRepository.tabAllViewMode)
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    private fun refreshCollection() {
        val items = itemListAdapter.currentList
        itemListAdapter = ItemListAdapter(
            requireContext(),
            isCompactLayout = preferenceRepository.isCompactLayout(TAB_ALL),
            callback = itemListAdapterCallback
        ).apply {
            submitList(items)
        }
        initRecyclerView()
    }

    private fun closeCollection() {
        forceBack = true
        activity?.onBackPressed()
    }
}
