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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hcmus.android.gallery1.*
import hcmus.android.gallery1.fragments.collection.TabAlbumFragment
import hcmus.android.gallery1.fragments.collection.TabAllFragment
import hcmus.android.gallery1.fragments.collection.TabDateFragment
import hcmus.android.gallery1.fragments.collection.TabFavoritesFragment
import hcmus.android.gallery1.helpers.*

class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findElements()
        initBottomDrawer()
        initNavbar()
        initViewModeSelectors()
        bindButtons()

        // Insert first fragment into view
        childFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.main_fragment_container, TabAllFragment())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    // Hold all references to elements on screen
    private lateinit var bDrawerNavbar: BottomNavigationView
    private lateinit var bDrawerBehavior: BottomSheetBehavior<BottomNavigationView>
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

    ////////////////////////////////////////////////////////////////////////////////

    // Find all elements on screen
    private fun findElements() {
        val view = requireView()

        // Bottom drawer
        bDrawerNavbar    = view.findViewById(R.id.main_navbar)
        bDrawerBehavior  = BottomSheetBehavior.from(view.findViewById(R.id.bdrawer_main))
        bDrawerBtnExpand = view.findViewById(R.id.btn_bottom_sheet_expand)
        bDrawerDim       = view.findViewById(R.id.bdrawer_dim)

        // Bottom drawer -> View mode selectors
        viewModeSelectorAll   = view.findViewById(R.id.viewmode_all)
        viewModeSelectorAlbum = view.findViewById(R.id.viewmode_album)
        viewModeSelectorDate  = view.findViewById(R.id.viewmode_date)
        viewModeSelectorFav   = view.findViewById(R.id.viewmode_fav)

        // Bottom drawer -> Buttons
        btnNewAlbum = view.findViewById(R.id.btn_new_album)
        btnNewPhoto = view.findViewById(R.id.btn_new_photo)
        btnNewVideo = view.findViewById(R.id.btn_new_video)
        btnSetAbout = view.findViewById(R.id.btn_more_about)
        btnSetTheme = view.findViewById(R.id.btn_more_theme)
        btnSetLanguage = view.findViewById(R.id.btn_more_language)
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
                        bDrawerBtnExpand.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_bdrawer_up))
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bDrawerDim.visibility = View.VISIBLE
                        bDrawerBtnExpand.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_bdrawer_down))
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
        bDrawerNavbar.setOnNavigationItemSelectedListener {
            bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            childFragmentManager.commit {
                when (it.itemId) {
                    R.id.tab_all -> replace(R.id.main_fragment_container, TabAllFragment())
                    R.id.tab_album -> replace(R.id.main_fragment_container, TabAlbumFragment())
                    R.id.tab_date -> replace(R.id.main_fragment_container, TabDateFragment())
                    R.id.tab_favorites -> replace(R.id.main_fragment_container, TabFavoritesFragment())
                }
            }
            setVisibleViewModeSelector(it.itemId)
            true
        }
    }

    // Bottom drawer: view mode selectors
    private fun initViewModeSelectors() {
        // View mode: all
        setVisibleViewModeSelector(R.id.tab_all)

        // Set initial state from globalPrefs

        viewModeSelectorAll.check(
            when(globalPrefs.getViewMode(TAB_ALL)) {
                VIEW_LIST -> R.id.btn_viewmode_all_list
                VIEW_GRID_3 -> R.id.btn_viewmode_all_grid_3
                VIEW_GRID_4 -> R.id.btn_viewmode_all_grid_4
                VIEW_GRID_5 -> R.id.btn_viewmode_all_grid_5
                else -> R.id.btn_viewmode_all_grid_3
            }
        )

        viewModeSelectorAlbum.check(
            when(globalPrefs.getViewMode(TAB_ALBUM)) {
                VIEW_LIST -> R.id.btn_viewmode_album_list
                VIEW_GRID_2 -> R.id.btn_viewmode_album_grid_2
                else -> R.id.btn_viewmode_album_grid_2
            }
        )

        viewModeSelectorDate.check(
            when(globalPrefs.getViewMode(TAB_DATE)) {
                VIEW_LIST -> R.id.btn_viewmode_date_list
                VIEW_GRID_2 -> R.id.btn_viewmode_date_grid_2
                else -> R.id.btn_viewmode_date_grid_2
            }
        )

        viewModeSelectorFav.check(
            when(globalPrefs.getViewMode(TAB_FAV)) {
                VIEW_LIST -> R.id.btn_viewmode_fav_list
                VIEW_GRID_3 -> R.id.btn_viewmode_fav_grid_3
                VIEW_GRID_4 -> R.id.btn_viewmode_fav_grid_4
                VIEW_GRID_5 -> R.id.btn_viewmode_fav_grid_5
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
                    globalPrefs.setViewMode(TAB_ALL, VIEW_GRID_3)
                }
                R.id.btn_viewmode_all_grid_4 -> {
                    globalPrefs.setViewMode(TAB_ALL, VIEW_GRID_4)
                }
                R.id.btn_viewmode_all_grid_5 -> {
                    globalPrefs.setViewMode(TAB_ALL, VIEW_GRID_5)
                }
            }

            // Dirty reload current fragment
            childFragmentManager.commit {
                replace(R.id.main_fragment_container, TabAllFragment())
            }
        }

        viewModeSelectorAlbum.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.btn_viewmode_album_list -> {
                    globalPrefs.setViewMode(TAB_ALBUM, VIEW_LIST)
                }
                R.id.btn_viewmode_album_grid_2 -> {
                    globalPrefs.setViewMode(TAB_ALBUM, VIEW_GRID_2)
                }
            }

            // Dirty reload current fragment
            childFragmentManager.commit {
                replace(R.id.main_fragment_container, TabAlbumFragment())
            }
        }

        viewModeSelectorDate.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.btn_viewmode_date_list -> {
                    globalPrefs.setViewMode(TAB_DATE, VIEW_LIST)
                }
                R.id.btn_viewmode_date_grid_2 -> {
                    globalPrefs.setViewMode(TAB_DATE, VIEW_GRID_2)
                }
            }

            // Dirty reload current fragment
            childFragmentManager.commit {
                replace(R.id.main_fragment_container, TabDateFragment())
            }
        }

        viewModeSelectorFav.addOnButtonCheckedListener { _, checkedId, _ ->
            // Write to settings
            when (checkedId) {
                R.id.btn_viewmode_fav_list -> {
                    globalPrefs.setViewMode(TAB_FAV, VIEW_LIST)
                }
                R.id.btn_viewmode_fav_grid_3 -> {
                    globalPrefs.setViewMode(TAB_FAV, VIEW_GRID_3)
                }
                R.id.btn_viewmode_fav_grid_4 -> {
                    globalPrefs.setViewMode(TAB_FAV, VIEW_GRID_4)
                }
                R.id.btn_viewmode_fav_grid_5 -> {
                    globalPrefs.setViewMode(TAB_FAV, VIEW_GRID_5)
                }
            }

            // Dirty reload current fragment
            childFragmentManager.commit {
                replace(R.id.main_fragment_container, TabFavoritesFragment())
            }
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
            .setSingleChoiceItems(resources.getStringArray(R.array.settings_theme), globalPrefs.validThemes.indexOf(globalPrefs.theme)) { _, which ->
                when (globalPrefs.validThemes[which]) {
                    THEME_FOLLOW_SYSTEM -> {
                        Toast.makeText(requireContext(), "You chose to follow the system", Toast.LENGTH_SHORT).show()
                    }
                    THEME_DAY -> {
                        Toast.makeText(requireContext(), "You chose day mode", Toast.LENGTH_SHORT).show()
                    }
                    THEME_NIGHT -> {
                        Toast.makeText(requireContext(), "You chose night mode", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "This should not happen", Toast.LENGTH_SHORT).show()
                    }
                }
                globalPrefs.theme = globalPrefs.validThemes[which]
            }
            .show()
    }

    private fun handleBtnSetLanguage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_language)
            .setSingleChoiceItems(resources.getStringArray(R.array.settings_language),
                globalPrefs.validLanguages.indexOf(globalPrefs.language)
            ) { _, which ->
                val language = globalPrefs.validLanguages[which]
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
        globalFragmentManager.commit {
            addToBackStack("main")
            // replace(R.id.fragment_container, SecretAlbumFragment(), "CURRENT")
        }
        bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}
