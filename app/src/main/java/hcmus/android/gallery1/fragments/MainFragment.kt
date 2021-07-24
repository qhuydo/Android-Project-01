package hcmus.android.gallery1.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hcmus.android.gallery1.Activity2
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.TabFragmentAdapter
import hcmus.android.gallery1.fragments.base.CollectionListFragment
import hcmus.android.gallery1.fragments.base.ImageListFragment
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.*

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("pos")
        }
        findElements()
        initViewPager()
        initBottomDrawer()
        initNavbar()
        initViewModeSelectors()
        bindButtons()

    }

    override fun onDestroy() {
        viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("pos", currentPosition)
    }

    private val tabFragmentAdapter by lazy { TabFragmentAdapter(this) }

    private var currentPosition = TAB.ALL.ordinal

    private val onPageChangeCallback = object: ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            currentPosition = position

            val itemId = when (position) {
                TAB.ALBUM.ordinal -> R.id.tab_album
                TAB.DATE.ordinal -> R.id.tab_date
                TAB.FAV.ordinal -> R.id.tab_favorites
                else -> R.id.tab_all
            }
            onNavigationItemSelected(itemId)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    // Hold all references to elements on screen
    private lateinit var bDrawerNavbar: TabLayout
    private lateinit var bDrawerBehavior: BottomSheetBehavior<TabLayout>
    private lateinit var bDrawerBtnExpand: ImageButton
    private lateinit var bDrawerDim: View
    private lateinit var viewModeSelectorAll: MaterialButtonToggleGroup
    private lateinit var viewModeSelectorAlbum: MaterialButtonToggleGroup
    private lateinit var viewModeSelectorDate: MaterialButtonToggleGroup
    private lateinit var viewModeSelectorFav: MaterialButtonToggleGroup
    private lateinit var btnNewAlbum: Button
    private lateinit var btnNewPhoto: Button
    private lateinit var btnNewVideo: Button
    private lateinit var btnSetTheme: Button
    private lateinit var btnSetLanguage: Button
    private lateinit var btnSetAbout: Button
    private lateinit var viewPager2: ViewPager2

    ////////////////////////////////////////////////////////////////////////////////

    // Find all elements on screen
    private fun findElements() {
        requireView().apply {

            // Bottom drawer
            bDrawerNavbar    = findViewById(R.id.main_navbar)
            bDrawerBehavior  = BottomSheetBehavior.from(findViewById(R.id.bdrawer_main))
            bDrawerBtnExpand = findViewById(R.id.btn_bottom_sheet_expand)
            bDrawerDim       = findViewById(R.id.bdrawer_dim)

            // Bottom drawer -> View mode selectors
            viewModeSelectorAll   = findViewById(R.id.viewmode_all)
            viewModeSelectorAlbum = findViewById(R.id.viewmode_album)
            viewModeSelectorDate  = findViewById(R.id.viewmode_date)
            viewModeSelectorFav   = findViewById(R.id.viewmode_fav)

            // Bottom drawer -> Buttons
            btnNewAlbum = findViewById(R.id.btn_new_album)
            btnNewPhoto = findViewById(R.id.btn_new_photo)
            btnNewVideo = findViewById(R.id.btn_new_video)
            btnSetAbout = findViewById(R.id.btn_more_about)
            btnSetTheme = findViewById(R.id.btn_more_theme)
            btnSetLanguage = findViewById(R.id.btn_more_language)

            viewPager2 = findViewById<ViewPager2>(R.id.main_fragment_container)
        }

    }

    private fun initViewPager() {
        viewPager2.adapter = tabFragmentAdapter
        viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
    }

    // Bottom drawer: behavior
    private fun initBottomDrawer() {

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
                        bDrawerBtnExpand.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_bdrawer_up
                            )
                        )
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bDrawerDim.visibility = View.VISIBLE
                        bDrawerBtnExpand.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_bdrawer_down
                            )
                        )
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
    }

    // Bottom drawer: navbar
    private fun initNavbar() {
        // Navbar behavior
        TabLayoutMediator(bDrawerNavbar, viewPager2) { tab, position ->
            if (position == currentPosition) {
                tab.text = when (tab.position) {
                    TAB.ALBUM.ordinal -> getText(R.string.tab_album)
                    TAB.DATE.ordinal -> getText(R.string.tab_date)
                    TAB.FAV.ordinal -> getText(R.string.tab_favorites)
                    else -> getText(R.string.tab_all)
                }
            }
            else {
                tab.text = null
            }
            val iconIdRes =  when(position) {
                TAB.ALBUM.ordinal -> R.drawable.ic_tab_album
                TAB.DATE.ordinal -> R.drawable.ic_tab_date
                TAB.FAV.ordinal -> R.drawable.ic_tab_favorite
                else -> R.drawable.ic_tab_all
            }
            tab.setIcon(iconIdRes)
        }.attach()

        bDrawerNavbar.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    tab.text = when (tab.position) {
                        TAB.ALBUM.ordinal -> getText(R.string.tab_album)
                        TAB.DATE.ordinal -> getText(R.string.tab_date)
                        TAB.FAV.ordinal -> getText(R.string.tab_favorites)
                        else -> getText(R.string.tab_all)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.text = null
            }

            override fun onTabReselected(tab: TabLayout.Tab?) { }

        })

    }

    private fun onNavigationItemSelected(itemId: Int) {
        bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        setVisibleViewModeSelector(itemId)

    }

    // Bottom drawer: view mode selectors
    private fun initViewModeSelectors() {
        // View mode: all
        setVisibleViewModeSelector(R.id.tab_all)

        // Set initial state from globalPrefs

        viewModeSelectorAll.check(
            when(globalPrefs.getViewMode(TAB_ALL)) {
                VIEW_LIST -> R.id.btn_viewmode_all_list
                VIEW_ITEM_GRID_L -> R.id.btn_viewmode_all_grid_3
                VIEW_ITEM_GRID_M -> R.id.btn_viewmode_all_grid_4
                VIEW_ITEM_GRID_S -> R.id.btn_viewmode_all_grid_5
                else -> R.id.btn_viewmode_all_grid_3
            }
        )

        viewModeSelectorAlbum.check(
            when(globalPrefs.getViewMode(TAB_ALBUM)) {
                VIEW_LIST -> R.id.btn_viewmode_album_list
                VIEW_COLLECTION_GRID -> R.id.btn_viewmode_album_grid_2
                else -> R.id.btn_viewmode_album_grid_2
            }
        )

        viewModeSelectorDate.check(
            when(globalPrefs.getViewMode(TAB_DATE)) {
                VIEW_LIST -> R.id.btn_viewmode_date_list
                VIEW_COLLECTION_GRID -> R.id.btn_viewmode_date_grid_2
                else -> R.id.btn_viewmode_date_grid_2
            }
        )

        viewModeSelectorFav.check(
            when(globalPrefs.getViewMode(TAB_FAV)) {
                VIEW_LIST -> R.id.btn_viewmode_fav_list
                VIEW_ITEM_GRID_L -> R.id.btn_viewmode_fav_grid_3
                VIEW_ITEM_GRID_M -> R.id.btn_viewmode_fav_grid_4
                VIEW_ITEM_GRID_S -> R.id.btn_viewmode_fav_grid_5
                else -> R.id.btn_viewmode_fav_grid_3
            }
        )

        // Add listeners

        viewModeSelectorAll.addOnButtonCheckedListener { _, checkedId, _ ->
            // Write to settings
            when (checkedId) {
                R.id.btn_viewmode_all_list -> {
                    globalPrefs.setViewMode(TAB_ALL, VIEW_LIST)
                }
                R.id.btn_viewmode_all_grid_3 -> {
                    globalPrefs.setViewMode(TAB_ALL, VIEW_ITEM_GRID_L)
                }
                R.id.btn_viewmode_all_grid_4 -> {
                    globalPrefs.setViewMode(TAB_ALL, VIEW_ITEM_GRID_M)
                }
                R.id.btn_viewmode_all_grid_5 -> {
                    globalPrefs.setViewMode(TAB_ALL, VIEW_ITEM_GRID_S)
                }
            }

            // Dirty reload current fragment
            (tabFragmentAdapter.fragmentAt(TAB.ALL.ordinal) as? ImageListFragment)?.notifyViewTypeChanged()
            tabFragmentAdapter.notifyItemChanged(TAB.ALL.ordinal)
        }

        viewModeSelectorAlbum.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.btn_viewmode_album_list -> {
                    globalPrefs.setViewMode(TAB_ALBUM, VIEW_LIST)
                }
                R.id.btn_viewmode_album_grid_2 -> {
                    globalPrefs.setViewMode(TAB_ALBUM, VIEW_COLLECTION_GRID)
                }
            }

            // Dirty reload current fragment
            (tabFragmentAdapter.fragmentAt(TAB.ALBUM.ordinal) as? CollectionListFragment)?.notifyViewTypeChanged()
            tabFragmentAdapter.notifyItemChanged(TAB.ALBUM.ordinal)
        }

        viewModeSelectorDate.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.btn_viewmode_date_list -> {
                    globalPrefs.setViewMode(TAB_DATE, VIEW_LIST)
                }
                R.id.btn_viewmode_date_grid_2 -> {
                    globalPrefs.setViewMode(TAB_DATE, VIEW_COLLECTION_GRID)
                }
            }

            // Dirty reload current fragment
            (tabFragmentAdapter.fragmentAt(TAB.DATE.ordinal) as? CollectionListFragment)?.notifyViewTypeChanged()
            tabFragmentAdapter.notifyItemChanged(TAB.DATE.ordinal)
        }

        viewModeSelectorFav.addOnButtonCheckedListener { _, checkedId, _ ->
            // Write to settings
            when (checkedId) {
                R.id.btn_viewmode_fav_list -> {
                    globalPrefs.setViewMode(TAB_FAV, VIEW_LIST)
                }
                R.id.btn_viewmode_fav_grid_3 -> {
                    globalPrefs.setViewMode(TAB_FAV, VIEW_ITEM_GRID_L)
                }
                R.id.btn_viewmode_fav_grid_4 -> {
                    globalPrefs.setViewMode(TAB_FAV, VIEW_ITEM_GRID_M)
                }
                R.id.btn_viewmode_fav_grid_5 -> {
                    globalPrefs.setViewMode(TAB_FAV, VIEW_ITEM_GRID_S)
                }
            }

            // Dirty reload current fragment
            (tabFragmentAdapter.fragmentAt(TAB.FAV.ordinal) as? ImageListFragment)?.notifyViewTypeChanged()
            tabFragmentAdapter.notifyItemChanged(TAB.FAV.ordinal)
        }
    }

    private fun setVisibleViewModeSelector(itemId: Int) {
        viewModeSelectorAll.visibility = View.GONE
        viewModeSelectorAlbum.visibility = View.GONE
        viewModeSelectorDate.visibility = View.GONE
        viewModeSelectorFav.visibility = View.GONE
        when(itemId) {
            R.id.tab_all -> viewModeSelectorAll.visibility = View.VISIBLE
            R.id.tab_album -> viewModeSelectorAlbum.visibility = View.VISIBLE
            R.id.tab_date -> viewModeSelectorDate.visibility = View.VISIBLE
            R.id.tab_favorites -> viewModeSelectorFav.visibility = View.VISIBLE
            else -> {}
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    // Bind handling functions to buttons
    private fun bindButtons() {
        btnNewAlbum.setOnClickListener { handleBtnNewAlbum() }
        btnNewPhoto.setOnClickListener { handleBtnNewPhoto() }
        btnNewVideo.setOnClickListener { handleBtnNewVideo() }
        btnSetTheme.setOnClickListener { handleBtnSetTheme() }
        btnSetLanguage.setOnClickListener { handleBtnSetLanguage() }

        // The "About" button is a little bit special
        btnSetAbout.setOnClickListener { handleBtnAbout() }
        btnSetAbout.setOnLongClickListener { handleBtnSecret(); true }
    }

    private fun handleBtnNewAlbum() {
        Toast.makeText(requireContext(), "Not implemented", Toast.LENGTH_SHORT).show()
    }

    private fun handleBtnNewPhoto() {
        try {
            startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
        }
        catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Failed to open camera (do you have camera app installed?)", Toast.LENGTH_LONG).show()
        }
        bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun handleBtnNewVideo() {
        try {
            startActivity(Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA))
        }
        catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Failed to open camera (do you have camera app installed?)", Toast.LENGTH_LONG).show()
        }
        bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun handleBtnSetTheme() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_theme)
            .setSingleChoiceItems(resources.getStringArray(R.array.settings_theme),
                PreferenceFacility.validThemes.indexOf(globalPrefs.theme)) { _, which ->
                (activity as? Activity2)?.changeTheme(PreferenceFacility.validThemes[which])
            }
            .show()
    }

    private fun handleBtnSetLanguage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_language)
            .setSingleChoiceItems(resources.getStringArray(R.array.settings_language),
                PreferenceFacility.validLanguages.indexOf(globalPrefs.language)
            ) { _, which ->
                val language = PreferenceFacility.validLanguages[which]
                (activity as? Activity2)?.changeLanguage(language)
            }
            .show()
    }

    private fun handleBtnAbout() {
        MaterialAlertDialogBuilder(requireContext())
            .setView(LayoutInflater.from(requireContext()).inflate(R.layout.about_dialog, null, false))
            .show()
    }

    private fun handleBtnSecret() {
        activity?.supportFragmentManager?.commit {
            addToBackStack("main")
            // replace(R.id.fragment_container, SecretAlbumFragment(), "CURRENT")
        }
        bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}
