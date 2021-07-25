package hcmus.android.gallery1

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButtonToggleGroup
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.data.getItems
import hcmus.android.gallery1.data.getItemsByDate
import hcmus.android.gallery1.databinding.ImageListStandaloneBinding
import hcmus.android.gallery1.helpers.*
import kotlin.properties.Delegates

class ViewCollectionActivity : AppCompatActivity() {

    private lateinit var binding: ImageListStandaloneBinding
    // UI elements
    private lateinit var bDrawerBehavior : BottomSheetBehavior<LinearLayout>
    private lateinit var bDrawerBtnExpand : ImageButton
    private lateinit var bDrawerDim : View
    private lateinit var viewModeSelector : MaterialButtonToggleGroup

    // Collection
    private var collectionId by Delegates.notNull<Long>()
    private lateinit var collectionName : String
    private lateinit var collectionType : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configTheme(globalPrefs, null)
        supportActionBar?.hide()
        binding = ImageListStandaloneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // layoutInflater.inflate(R.layout.image_list_standalone, null, false)

        collectionId = intent.getLongExtra("collectionId", 0)
        collectionName = intent.getStringExtra("collectionName").toString()
        collectionType = intent.getStringExtra("collectionType").toString()

        binding.bdrawerImageList.collectionName.text = collectionName

        initBottomDrawer()
        refreshCollection()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.uiMode != resources.configuration.uiMode) {
            configTheme(globalPrefs, newConfig.uiMode)
        }
        recreate()
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
                            this@ViewCollectionActivity,
                            R.drawable.ic_bdrawer_up
                        )
                        bDrawerBtnExpand.setImageDrawable(drawable)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bDrawerDim.visibility = View.VISIBLE
                        val drawable = ContextCompat.getDrawable(
                            this@ViewCollectionActivity,
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
                R.id.btn_viewmode_all_list -> {
                    globalPrefs.setViewMode("all", "list")
                }
                R.id.btn_viewmode_all_grid_3 -> {
                    globalPrefs.setViewMode("all", "grid_3")
                }
                R.id.btn_viewmode_all_grid_4 -> {
                    globalPrefs.setViewMode("all", "grid_4")
                }
                R.id.btn_viewmode_all_grid_5 -> {
                    globalPrefs.setViewMode("all", "grid_5")
                }
            }

            // Dirty reload the current RecyclerView
            refreshCollection()
        }
    }

    private fun refreshCollection() {
        binding.recyclerView.apply {
            layoutManager = when(globalPrefs.getViewMode("all")) {
                "list" -> LinearLayoutManager(context)
                "grid_3" -> GridLayoutManager(context, 3)
                "grid_4" -> GridLayoutManager(context, 4)
                "grid_5" -> GridLayoutManager(context, 5)
                else -> GridLayoutManager(context, 3)
            }
            adapter = when (collectionType) {
                "album" -> ItemListAdapter(
                    items = contentResolver.getItems(collectionId),
                    isCompactLayout = globalPrefs.getViewMode("all") == "list"
                )
                "date" -> ItemListAdapter(
                    items = contentResolver.getItemsByDate(collectionId),
                    isCompactLayout = globalPrefs.getViewMode("all") == "list"
                )
                else -> ItemListAdapter(
                    items = contentResolver.getItems(collectionId),
                    isCompactLayout = globalPrefs.getViewMode("all") == "list"
                )
            }
        }
    }

    fun btnCloseCollection(view: View) {
        if (view.id == R.id.btn_close) {
            finish()
        }
    }
}
