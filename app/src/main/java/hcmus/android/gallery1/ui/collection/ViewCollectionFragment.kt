package hcmus.android.gallery1.ui.collection

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButtonToggleGroup
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.databinding.FragmentViewCollectionBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.ui.base.BottomDrawerFragment
import hcmus.android.gallery1.ui.main.globalPrefs

class ViewCollectionFragment :
    BottomDrawerFragment<FragmentViewCollectionBinding, LinearLayout>(R.layout.fragment_view_collection) {

    companion object {
        const val ARGS_COLLECTION = "collection"
    }

    private lateinit var viewModeSelector: MaterialButtonToggleGroup

    // Collection
    private val collection: Collection by lazy {
        requireArguments().getParcelable(ARGS_COLLECTION)!!
    }

    private val itemListAdapterCallback = ItemListAdapter.Callback { item ->
        mainActivity?.pushViewImageFragment(item)
    }

    private val viewModel by viewModels<CollectionDetailsViewModel> {
        CollectionDetailsViewModel.Factory(mainActivity!!.photoRepository)
    }

    private var itemListAdapter: ItemListAdapter = ItemListAdapter(
        isCompactLayout = globalPrefs.getViewMode(TAB_ALL) == VIEW_LIST,
        callback = itemListAdapterCallback
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity?.setViewPaddingWindowInset(binding.recyclerView)

        viewModel.setCollection(collection)

        viewModel.collection.observeOnce(viewLifecycleOwner) {
            viewModel.getPhotos()
            refreshCollection()
        }

        viewModel.photos.observeOnce(viewLifecycleOwner) { itemListAdapter.submitList(it) }
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
            when (globalPrefs.getViewMode(TAB_ALL)) {
                VIEW_LIST -> R.id.btn_viewmode_all_list
                VIEW_ITEM_GRID_L -> R.id.btn_viewmode_all_grid_3
                VIEW_ITEM_GRID_M -> R.id.btn_viewmode_all_grid_4
                VIEW_ITEM_GRID_S -> R.id.btn_viewmode_all_grid_5
                else -> R.id.btn_viewmode_all_grid_3
            }
        )

        viewModeSelector.addOnButtonCheckedListener { _, checkedId, _ ->
            // Write to settings
            when (checkedId) {
                R.id.btn_viewmode_all_list -> globalPrefs.setViewMode(TAB_ALL, VIEW_LIST)
                R.id.btn_viewmode_all_grid_3 -> globalPrefs.setViewMode(TAB_ALL, VIEW_ITEM_GRID_L)
                R.id.btn_viewmode_all_grid_4 -> globalPrefs.setViewMode(TAB_ALL, VIEW_ITEM_GRID_M)
                R.id.btn_viewmode_all_grid_5 -> globalPrefs.setViewMode(TAB_ALL, VIEW_ITEM_GRID_S)
            }

            // Dirty reload the current RecyclerView
            refreshCollection()
        }

        binding.bdrawerImageList.btnClose.setOnClickListener {
            closeCollection()
        }
    }

    override fun bindData() {
        binding.bdrawerImageList.collectionName.text = collection.name
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = itemListAdapter
            val spanCount = requireContext().getSpanCountOf(TAB_ALL, globalPrefs.getViewMode(TAB_ALL))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    private fun refreshCollection() {
        val items = itemListAdapter.currentList
        itemListAdapter = ItemListAdapter(
            isCompactLayout = globalPrefs.getViewMode(TAB_ALL) == VIEW_LIST,
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
