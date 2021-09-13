package hcmus.android.gallery1.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.FragmentMainBinding
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.bottomNavIdToTabPosition
import hcmus.android.gallery1.helpers.extensions.collapse
import hcmus.android.gallery1.helpers.extensions.padding
import hcmus.android.gallery1.helpers.extensions.toast
import hcmus.android.gallery1.repository.PreferenceRepository
import hcmus.android.gallery1.ui.adapters.recyclerview.ButtonGroupViewModeAdapter
import hcmus.android.gallery1.ui.adapters.recyclerview.OnViewModeSelectedCallback
import hcmus.android.gallery1.ui.adapters.viewpager2.TabFragmentAdapter
import hcmus.android.gallery1.ui.base.BottomDrawerFragment
import hcmus.android.gallery1.ui.base.collection.CollectionListFragment
import hcmus.android.gallery1.ui.base.image.ImageListFragment
import java.lang.ref.WeakReference

class MainFragment : BottomDrawerFragment<FragmentMainBinding>(R.layout.fragment_main) {

    companion object {
        const val BUNDLE_POS = "pos"
        val bottomNavItemIds = setOf(
            R.id.menu_tab_all,
            R.id.menu_tab_album,
            R.id.menu_tab_date,
            R.id.menu_tab_favorites,
        )
    }

    private val tabFragmentAdapter by lazy { TabFragmentAdapter(this) }

    private var currentPosition = TAB.ALL.ordinal

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            currentPosition = position

            bottomSheetBehavior.collapse()
            viewModeRecyclerView.smoothScrollToPosition(position)
        }
    }

    private val onViewModeSelectedCallback = object : OnViewModeSelectedCallback {
        override fun onViewModeSelected(tab: TAB, viewMode: String) {
            preferenceRepository.setViewMode(tab.key, viewMode)

            val fragment = childFragmentManager.findFragmentByTag("f${tab.ordinal}")
            fragment?.let { fm ->

                when (fm) {
                    is ImageListFragment<*> -> fm.notifyViewTypeChanged()
                    is CollectionListFragment -> fm.notifyViewTypeChanged()
                }

                tabFragmentAdapter.notifyItemChanged(tab.ordinal)

            }
        }
    }

    private var dialogToDismiss = WeakReference<AlertDialog>(null)

    override fun calculatePeekHeight(): Int = with(binding.bottomDrawerMain) {
        listDivider.measuredHeight + topRow.measuredHeight
    }

    ////////////////////////////////////////////////////////////////////////////////

    // Hold all references to elements on screen
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewModeRecyclerView: RecyclerView

    ////////////////////////////////////////////////////////////////////////////////

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

    override fun onPause() {
        dialogToDismiss.get()?.dismiss()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_POS, currentPosition)
    }

    override fun subscribeUi() {}

    // Find all elements on screen
    private fun findElements() {

        binding.bottomDrawerMain.apply {
            // Bottom drawer
            bottomNavigationView = mainNavbar
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

    private fun initViewPager() = viewPager2.apply {
        adapter = tabFragmentAdapter
        this.isUserInputEnabled = false
        registerOnPageChangeCallback(onPageChangeCallback)
    }

    // Bottom drawer: navbar
    private fun initNavbar() {
        // Navbar behavior

        bottomNavigationView.setOnItemSelectedListener {
            if (it.itemId in bottomNavItemIds) {
                viewPager2.currentItem = it.itemId.bottomNavIdToTabPosition()
                true
            }
            else {
                false
            }
        }
        bottomNavigationView.setOnApplyWindowInsetsListener(null)

        mainActivity?.setViewPaddingWindowInset(viewPager2)
    }

    // Bottom drawer: view mode selectors
    private fun initViewModeSelectors() {
        viewModeRecyclerView.adapter = ButtonGroupViewModeAdapter(onViewModeSelectedCallback)
    }

    override fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {
        viewPager2.padding(bottom = peekHeight)
    }

    ////////////////////////////////////////////////////////////////////////////////

    fun handleBtnNewAlbum() {
        toast("Not implemented")
    }

    fun handleBtnNewPhoto() {
        try {
            startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
        } catch (e: ActivityNotFoundException) {
            toast(
                "Failed to open camera (do you have camera app installed?)",
                Toast.LENGTH_LONG
            )
        }
        bottomSheetBehavior.collapse()
    }

    fun handleBtnNewVideo() {
        try {
            startActivity(Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA))
        } catch (e: ActivityNotFoundException) {
            toast(
                "Failed to open camera (do you have camera app installed?)",
                Toast.LENGTH_LONG
            )
        }
        bottomSheetBehavior.collapse()
    }

    fun handleBtnSetTheme() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_theme)
            .setSingleChoiceItems(
                resources.getStringArray(R.array.settings_theme),
                PreferenceRepository.validThemes.indexOf(preferenceRepository.theme)
            ) { _, which ->
                (activity as? MainActivity)?.changeTheme(PreferenceRepository.validThemes[which])
            }
            .show()
        dialogToDismiss = WeakReference(dialog)
    }

    fun handleBtnSetLanguage() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bdrawer_more_language)
            .setSingleChoiceItems(
                resources.getStringArray(R.array.settings_language),
                PreferenceRepository.validLanguages.indexOf(preferenceRepository.language)
            ) { _, which ->
                val language = PreferenceRepository.validLanguages[which]
                (activity as? MainActivity)?.changeLanguage(language)
            }
            .show()
        dialogToDismiss = WeakReference(dialog)
    }

    fun handleBtnAbout() {
        val aboutView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_about, binding.root as ViewGroup, false)

        MaterialAlertDialogBuilder(requireContext())
            .setView(aboutView)
            .show()
    }

    fun handleBtnSecret(): Boolean {
//        activity?.supportFragmentManager?.commit {
//            addToBackStack("main")
//            // replace(R.id.fragment_container, SecretAlbumFragment(), "CURRENT")
//        }
        bottomSheetBehavior.collapse()
        return true
    }
}
