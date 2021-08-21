package hcmus.android.gallery1.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import hcmus.android.gallery1.ui.main.globalPrefs

class ViewCollectionFragment : Fragment() {

    companion object {
        const val BUNDLE_COLLECTION = "collection"
    }

    private lateinit var binding: FragmentViewCollectionBinding
    // UI elements
    private lateinit var bDrawerBehavior : BottomSheetBehavior<LinearLayout>
    private lateinit var bDrawerBtnExpand : ImageButton
    private lateinit var bDrawerDim : View
    private lateinit var viewModeSelector : MaterialButtonToggleGroup

    // Collection
    private val collection: Collection by lazy {
        requireArguments().getParcelable(BUNDLE_COLLECTION)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewCollectionBinding.inflate(layoutInflater)
        binding.bdrawerImageList.collectionName.text = collection.name

        initBottomDrawer()
        refreshCollection()

        return binding.root
    }

    private fun initBottomDrawer() {
        binding.bdrawerImageList.apply {

            bDrawerBehavior  = BottomSheetBehavior.from(bdrawerImageListStandalone)
            bDrawerBtnExpand = btnBottomSheetExpand
        }
        bDrawerDim       = binding.bdrawerDim
        viewModeSelector = binding.bdrawerImageList.viewmodeAll

        // Bottom sheet behavior
        bDrawerBehavior.apply {
            isFitToContents = true
            // halfExpandedRatio = (490/1000f) // magic
        }

        // https://blog.mindorks.com/android-bottomsheet-in-kotlin
        bDrawerBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bDrawerDim.visibility = View.GONE
                        val drawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_bdrawer_up
                        )
                        bDrawerBtnExpand.setImageDrawable(drawable)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bDrawerDim.visibility = View.VISIBLE
                        val drawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_bdrawer_down
                        )
                        bDrawerBtnExpand.setImageDrawable(drawable)
                    }
                    else -> { }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bDrawerDim.visibility = View.VISIBLE
                bDrawerDim.alpha = 0.5f * slideOffset
            }
        })

        bDrawerDim.setOnClickListener {
            bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Button expansion behavior
        bDrawerBtnExpand.apply {
            setOnClickListener {
                when (bDrawerBehavior.state) {
                    BottomSheetBehavior.STATE_COLLAPSED     -> bDrawerBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_EXPANDED      -> bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    else -> { }
                }
            }
        }

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
                isCompactLayout = globalPrefs.getViewMode(TAB_ALL) == VIEW_LIST
            )

        }
    }


    private fun closeCollection() {
        activity?.onBackPressed()
    }
}
