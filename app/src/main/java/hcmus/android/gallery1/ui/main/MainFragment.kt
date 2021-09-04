package hcmus.android.gallery1.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hcmus.android.gallery1.R
import hcmus.android.gallery1.ui.adapters.ButtonGroupViewModeAdapter
import hcmus.android.gallery1.ui.adapters.OnViewModeSelectedCallback
import hcmus.android.gallery1.ui.adapters.TabFragmentAdapter
import hcmus.android.gallery1.databinding.FragmentMainBinding
import hcmus.android.gallery1.ui.base.collectionlist.CollectionListFragment
import hcmus.android.gallery1.ui.base.imagelist.ImageListFragment
import hcmus.android.gallery1.repository.PreferenceRepository
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.toTabKey
import hcmus.android.gallery1.ui.base.BottomDrawerFragment

class MainFragment : BottomDrawerFragment<FragmentMainBinding, LinearLayout>(R.layout.fragment_main) {

    companion object {
        const val BUNDLE_POS = "pos"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(BUNDLE_POS)
        }
        findElements()
        initViewPager()
        initNavbar()
        initViewModeSelectors()
    }

    override fun onDestroy() {
        viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_POS, currentPosition)
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
            preferenceRepository.setViewMode(tab.toTabKey(), viewMode)

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
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewModeRecyclerView: RecyclerView

    ////////////////////////////////////////////////////////////////////////////////

    // Find all elements on screen
    private fun findElements() {

        binding.bottomDrawerMain.apply {
            // Bottom drawer
            tabLayout = mainNavbar
            viewModeRecyclerView = binding.bottomDrawerMain.viewmode
        }

        viewPager2 = binding.mainFragmentContainer

    }

    override fun initBottomDrawerElements() {
        binding.bottomDrawerMain.apply {
            // Bottom drawer
            bottomDrawerView = root
            bottomSheetBehavior = BottomSheetBehavior.from(bdrawerMain)
            bottomSheetExpandButton = btnBottomSheetExpand
            bottomDrawerDim = binding.bdrawerDim
        }
    }

    override fun bindData() {
        binding.fragment = this
    }

    private fun initViewPager() {
        viewPager2.adapter = tabFragmentAdapter
        viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
    }

    // Bottom drawer: navbar
    private fun initNavbar() {
        // Navbar behavior
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
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

        // remove the gap between tab icon & tab text of the first tab
        val params = tabLayout.getTabAt(0)?.view?.getChildAt(0)?.layoutParams as LinearLayout.LayoutParams?
        params?.bottomMargin = 0
        tabLayout.getTabAt(0)?.view?.getChildAt(0)?.layoutParams = params

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    tab.text = when (tab.position) {
                        TAB.ALBUM.ordinal -> getText(R.string.tab_album)
                        TAB.DATE.ordinal -> getText(R.string.tab_date)
                        TAB.FAV.ordinal -> getText(R.string.tab_favorites)
                        else -> getText(R.string.tab_all)
                    }

                    // remove the gap between tab icon & tab text
                    val params = tabLayout.getTabAt(tab.position)?.view?.getChildAt(0)?.layoutParams as LinearLayout.LayoutParams?
                    params?.bottomMargin = 0
                    tabLayout.getTabAt(tab.position)?.view?.getChildAt(0)?.layoutParams = params
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.text = null
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        mainActivity?.setViewPaddingWindowInset(viewPager2)

    }

    // Bottom drawer: view mode selectors
    private fun initViewModeSelectors() {
        viewModeRecyclerView.adapter = ButtonGroupViewModeAdapter(onViewModeSelectedCallback)
    }

    ////////////////////////////////////////////////////////////////////////////////

    fun handleBtnNewAlbum() {
        Toast.makeText(requireContext(), "Not implemented", Toast.LENGTH_SHORT).show()
    }

    fun handleBtnNewPhoto() {
        try {
            startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Failed to open camera (do you have camera app installed?)",
                Toast.LENGTH_LONG
            ).show()
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun handleBtnNewVideo() {
        try {
            startActivity(Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Failed to open camera (do you have camera app installed?)",
                Toast.LENGTH_LONG
            ).show()
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun handleBtnSetTheme() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_theme)
            .setSingleChoiceItems(
                resources.getStringArray(R.array.settings_theme),
                PreferenceRepository.validThemes.indexOf(preferenceRepository.theme)
            ) { _, which ->
                (activity as? MainActivity)?.changeTheme(PreferenceRepository.validThemes[which])
            }
            .show()
    }

    fun handleBtnSetLanguage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_language)
            .setSingleChoiceItems(
                resources.getStringArray(R.array.settings_language),
                PreferenceRepository.validLanguages.indexOf(preferenceRepository.language)
            ) { _, which ->
                val language = PreferenceRepository.validLanguages[which]
                (activity as? MainActivity)?.changeLanguage(language)
            }
            .show()
    }

    fun handleBtnAbout() {
        MaterialAlertDialogBuilder(requireContext())
            .setView(
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_about, null, false)
            )
            .show()
    }

    fun handleBtnSecret(): Boolean {
//        activity?.supportFragmentManager?.commit {
//            addToBackStack("main")
//            // replace(R.id.fragment_container, SecretAlbumFragment(), "CURRENT")
//        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        return true
    }
}
