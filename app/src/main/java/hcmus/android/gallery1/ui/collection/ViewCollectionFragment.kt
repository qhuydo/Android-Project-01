package hcmus.android.gallery1.ui.collection

import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButtonToggleGroup
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.getItems
import hcmus.android.gallery1.data.getItemsByDate
import hcmus.android.gallery1.databinding.FragmentViewCollectionBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.ui.base.BottomDrawerFragment
import hcmus.android.gallery1.ui.main.globalPrefs

class ViewCollectionFragment : BottomDrawerFragment<FragmentViewCollectionBinding, LinearLayout>(R.layout.fragment_view_collection) {

    companion object {
        const val BUNDLE_COLLECTION = "collection"
    }

    private lateinit var viewModeSelector : MaterialButtonToggleGroup

    // Collection
    private val collection: Collection by lazy {
        requireArguments().getParcelable(BUNDLE_COLLECTION)!!
    }

    private val itemListAdapterCallback = ItemListAdapter.Callback { item ->
        mainActivity?.pushViewImageFragment(item)
    }


    override fun initBottomDrawerElements() {
        binding.bdrawerImageList.apply {
            bottomSheetBehavior = BottomSheetBehavior.from(bdrawerImageListStandalone)
            bottomSheetExpandButton = btnBottomSheetExpand
        }
        bottomDrawerDim = binding.bdrawerDim
        viewModeSelector = binding.bdrawerImageList.viewmodeAll
    }

    override fun initBottomDrawerElementsCallback() {
        super.initBottomDrawerElementsCallback()
        viewModeSelector.check(
            when(globalPrefs.getViewMode(TAB_ALL)) {
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
        refreshCollection()
    }

    private fun refreshCollection() {
        val contentResolver = requireContext().contentResolver

        binding.recyclerView.apply {
            layoutManager = when (globalPrefs.getViewMode(TAB_ALL)) {
                VIEW_LIST -> LinearLayoutManager(context)
                VIEW_ITEM_GRID_L -> GridLayoutManager(context, 3)
                VIEW_ITEM_GRID_M -> GridLayoutManager(context, 4)
                VIEW_ITEM_GRID_S -> GridLayoutManager(context, 5)
                else -> GridLayoutManager(context, 3)
            }
            val items = when (collection.type) {
                Collection.TYPE_ALBUM ->  contentResolver.getItems(collection.id)
                Collection.TYPE_DATE -> contentResolver.getItemsByDate(collection.id)
                else ->  contentResolver.getItems(collection.id)
            }

            adapter = ItemListAdapter(
                items = items,
                isCompactLayout = globalPrefs.getViewMode(TAB_ALL) == VIEW_LIST,
                callback = itemListAdapterCallback
            )

        }
    }

    private fun closeCollection() {
        activity?.onBackPressed()
    }
}
