package hcmus.android.gallery1.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.DialogAboutBinding
import hcmus.android.gallery1.databinding.FragmentMainBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.ScrollableToTop
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.repository.PreferenceRepository
import hcmus.android.gallery1.ui.adapters.recyclerview.ButtonGroupViewModeAdapter
import hcmus.android.gallery1.ui.adapters.recyclerview.OnViewModeSelectedCallback
import hcmus.android.gallery1.ui.adapters.viewpager2.TabFragmentAdapter
import hcmus.android.gallery1.ui.base.BottomDrawerFragment
import hcmus.android.gallery1.ui.dialog.ChangeThemeDialog.Companion.showChangeThemeDialog
import hcmus.android.gallery1.ui.dialog.NewAlbumDialog.Companion.showNewAlbumDialog
import java.lang.ref.WeakReference

class MainFragment : BottomDrawerFragment<FragmentMainBinding>(
    R.layout.fragment_main,
    ScreenConstant.MAIN
) {

    companion object {
        const val BUNDLE_POS = "pos"
        val bottomNavItemIds = mapOf(
            TAB.ALL to R.id.menu_tab_all,
            TAB.ALBUM to R.id.menu_tab_album,
            TAB.DATE to R.id.menu_tab_date,
            TAB.FAV to R.id.menu_tab_favorites,
        )
    }

    private val tabFragmentAdapter by lazy { TabFragmentAdapter(this) }

    private var currentPosition = TAB.ALL.ordinal
    private var peekHeight = -1

    private val tabChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            currentPosition = position
            fadeUpMainContainer()

            if (viewmodes.currentItem != position) {
                bottomSheetBehavior.collapse()
                viewmodes.currentItem = position
            }
        }
    }

    private val viewModeSelectorPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (position != currentPosition) {
                bottomNavigationView.selectedItemId = bottomNavItemIds[TAB.fromPosition(position)]!!
            }
        }
    }

    private fun fadeUpMainContainer() {
        binding.mainFragmentContainer.apply {
            invisible()
            animateFadeUp()
            visible()
        }
    }

    private val onViewModeSelectedCallback = object : OnViewModeSelectedCallback {
        override fun onViewModeSelected(tab: TAB, viewMode: String) {
            preferenceRepository.setViewMode(tab.key, viewMode)
            (pagerFragmentFromTab(tab) as? ChildOfMainFragment)?.animateFadeUp()

            // fadeUpMainContainer()
            // tabFragmentAdapter.notifyItemChanged(tab.ordinal)
        }
    }

    override fun calculatePeekHeight(): Int = with(binding.bottomDrawerMain) {
        listDivider.measuredHeight + topRow.measuredHeight
    }

    ////////////////////////////////////////////////////////////////////////////////

    // Hold all references to elements on screen
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var tabPages: ViewPager2
    private lateinit var viewmodes: ViewPager2

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

    override fun onDestroyView() {
        super.onDestroyView()
        tabPages.unregisterOnPageChangeCallback(tabChangeCallback)
        viewmodes.unregisterOnPageChangeCallback(viewModeSelectorPageChangeCallback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_POS, currentPosition)
    }

    override fun subscribeUi() {}

    override fun bottomSheetCallback() = MainBottomDrawerCallback(
        bottomDrawerDim, binding.bottomDrawerMain
    )

    // Find all elements on screen
    private fun findElements() {

        binding.bottomDrawerMain.apply {
            // Bottom drawer
            bottomNavigationView = mainNavbar
            viewmodes = binding.bottomDrawerMain.viewmode
        }

        tabPages = binding.mainFragmentContainer
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

    private fun initViewPager() = tabPages.apply {
        adapter = tabFragmentAdapter
        isUserInputEnabled = false
        registerOnPageChangeCallback(tabChangeCallback)
    }

    // Bottom drawer: navbar
    private fun initNavbar() {
        // Navbar behavior

        bottomNavigationView.setOnItemSelectedListener {
            if (it.itemId in bottomNavItemIds.values) {
                val currentPosition = it.itemId.bottomNavIdToTabPosition()

                if (currentPosition != tabPages.currentItem) {
                    // viewPager2.currentItem = currentPosition
                    tabPages.setCurrentItem(currentPosition, false)

                } else {
                    (currentViewPagerFragment() as? ScrollableToTop)?.scrollToTop()
                }

                return@setOnItemSelectedListener true
            }
            false
        }
        bottomNavigationView.setOnApplyWindowInsetsListener(null)
    }

    // Bottom drawer: view mode selectors
    private fun initViewModeSelectors() {
        viewmodes.adapter = ButtonGroupViewModeAdapter(onViewModeSelectedCallback)
        viewmodes.registerOnPageChangeCallback(viewModeSelectorPageChangeCallback)
    }

    override fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {
        this.peekHeight = peekHeight
        paddingChildPager(currentViewPagerFragment() as? ChildOfMainFragment)
    }

    fun paddingChildPager(childOfMainFragment: ChildOfMainFragment?) {
        if (peekHeight < 0) return
        childOfMainFragment?.apply {
            paddingContainerToFitWithPeekHeight(peekHeight)
            paddingContainerInStatusBarSide()
            view?.invalidate()
            view?.requestLayout()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    fun handleBtnNewAlbum() {
        dialogToDismiss = WeakReference(mainActivity!!.showNewAlbumDialog())
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
        dialogToDismiss = WeakReference(mainActivity!!.showChangeThemeDialog())
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
        val aboutView = DialogAboutBinding.inflate(
            LayoutInflater.from(requireContext())
        ).apply {
            textView.movementMethod = LinkMovementMethod.getInstance()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(aboutView.root)
            .show()

        dialogToDismiss = WeakReference(dialog)
    }

    fun handleBtnSecret(): Boolean {
//        activity?.supportFragmentManager?.commit {
//            addToBackStack("main")
//            // replace(R.id.fragment_container, SecretAlbumFragment(), "CURRENT")
//        }
        bottomSheetBehavior.collapse()
        return true
    }

    private fun Fragment.pagerFragmentFromTab(tab: TAB) = childFragmentManager.findFragmentByTag(
        "f${tab.ordinal}"
    )

    private fun Fragment.currentViewPagerFragment(): Fragment? {
        return childFragmentManager.findFragmentByTag("f${currentPosition}")
    }

    internal fun notifyViewModeChange(tab: TAB) {
        TAB.validTabs().firstOrNull { it == tab }?.let {
            viewmodes.adapter?.notifyItemChanged(it.ordinal)
        }
    }

    override fun getHiddenRows() = binding.bottomDrawerMain.hiddenRows
}
