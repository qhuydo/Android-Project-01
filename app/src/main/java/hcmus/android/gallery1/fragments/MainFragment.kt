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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hcmus.android.gallery1.Activity2
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.ButtonGroupViewModeAdapter
import hcmus.android.gallery1.adapters.OnViewModeSelectedCallback
import hcmus.android.gallery1.adapters.TabFragmentAdapter
import hcmus.android.gallery1.databinding.FragmentMainBinding
import hcmus.android.gallery1.fragments.base.CollectionListFragment
import hcmus.android.gallery1.fragments.base.ImageListFragment
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.PreferenceFacility
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.toTabKey

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
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

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            currentPosition = position

            // bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModeRecyclerView.smoothScrollToPosition(position)
        }
    }

    private val onViewModeSelectedCallback = object : OnViewModeSelectedCallback {
        override fun onViewModeSelected(tab: TAB, viewMode: String) {
            globalPrefs.setViewMode(tab.toTabKey(), viewMode)

            val fragment = childFragmentManager.findFragmentByTag("f${tab.ordinal}")
            fragment?.let {
                if (tab == TAB.ALL || tab == TAB.FAV) {
                    (fragment as? ImageListFragment)?.notifyViewTypeChanged()
                } else {
                    (fragment as? CollectionListFragment)?.notifyViewTypeChanged()
                }

                tabFragmentAdapter.notifyItemChanged(tab.ordinal)

            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    // Hold all references to elements on screen
    private lateinit var bDrawerNavbar: TabLayout
    private lateinit var bDrawerBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bDrawerBtnExpand: ImageButton
    private lateinit var bDrawerDim: View
    private lateinit var btnNewAlbum: Button
    private lateinit var btnNewPhoto: Button
    private lateinit var btnNewVideo: Button
    private lateinit var btnSetTheme: Button
    private lateinit var btnSetLanguage: Button
    private lateinit var btnSetAbout: Button
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewModeRecyclerView: RecyclerView

    ////////////////////////////////////////////////////////////////////////////////

    // Find all elements on screen
    private fun findElements() {
        requireView().apply {

            binding.bottomDrawerMain.apply {

                // Bottom drawer
                bDrawerNavbar = mainNavbar
                bDrawerBehavior = BottomSheetBehavior.from(bdrawerMain)
                bDrawerBtnExpand = btnBottomSheetExpand
                bDrawerDim = binding.bdrawerDim

                // Bottom drawer -> Buttons
                this@MainFragment.btnNewAlbum = btnNewAlbum
                this@MainFragment.btnNewPhoto = btnNewPhoto
                this@MainFragment.btnNewVideo = btnNewVideo
                btnSetAbout = btnMoreAbout
                btnSetTheme = btnMoreTheme
                btnSetLanguage = btnMoreLanguage

                viewModeRecyclerView = binding.bottomDrawerMain.viewmode
            }

            viewPager2 = binding.mainFragmentContainer
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
                    else -> {
                    }
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
                    BottomSheetBehavior.STATE_COLLAPSED -> bDrawerBehavior.state =
                        BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_EXPANDED -> bDrawerBehavior.state =
                        BottomSheetBehavior.STATE_COLLAPSED
                    else -> {
                    }
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
            } else {
                tab.text = null
            }
            val iconIdRes = when (position) {
                TAB.ALBUM.ordinal -> R.drawable.ic_tab_album
                TAB.DATE.ordinal -> R.drawable.ic_tab_date
                TAB.FAV.ordinal -> R.drawable.ic_tab_favorite
                else -> R.drawable.ic_tab_all
            }
            tab.setIcon(iconIdRes)
        }.attach()

        bDrawerNavbar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

    }

    // Bottom drawer: view mode selectors
    private fun initViewModeSelectors() {
        viewModeRecyclerView.adapter = ButtonGroupViewModeAdapter(onViewModeSelectedCallback)
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
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Failed to open camera (do you have camera app installed?)",
                Toast.LENGTH_LONG
            ).show()
        }
        bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun handleBtnNewVideo() {
        try {
            startActivity(Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Failed to open camera (do you have camera app installed?)",
                Toast.LENGTH_LONG
            ).show()
        }
        bDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun handleBtnSetTheme() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_theme)
            .setSingleChoiceItems(
                resources.getStringArray(R.array.settings_theme),
                PreferenceFacility.validThemes.indexOf(globalPrefs.theme)
            ) { _, which ->
                (activity as? Activity2)?.changeTheme(PreferenceFacility.validThemes[which])
            }
            .show()
    }

    private fun handleBtnSetLanguage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_language)
            .setSingleChoiceItems(
                resources.getStringArray(R.array.settings_language),
                PreferenceFacility.validLanguages.indexOf(globalPrefs.language)
            ) { _, which ->
                val language = PreferenceFacility.validLanguages[which]
                (activity as? Activity2)?.changeLanguage(language)
            }
            .show()
    }

    private fun handleBtnAbout() {
        MaterialAlertDialogBuilder(requireContext())
            .setView(
                LayoutInflater.from(requireContext()).inflate(R.layout.about_dialog, null, false)
            )
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
