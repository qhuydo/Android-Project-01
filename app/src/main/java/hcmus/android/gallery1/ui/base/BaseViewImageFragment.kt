package hcmus.android.gallery1.ui.base

import android.animation.LayoutTransition
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.data.ItemType
import hcmus.android.gallery1.databinding.BottomDrawerViewImageBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.helpers.widgets.ImageItemView
import hcmus.android.gallery1.ui.image.list.FavouritesViewModel
import hcmus.android.gallery1.ui.image.view.ViewImageViewModel
import hcmus.android.gallery1.ui.main.MainFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewImageFragment<B : ViewDataBinding>(@LayoutRes layoutId: Int) :
    BottomDrawerFragment<B>(layoutId) {
    companion object {
        const val ARGS_ITEM = "item"
    }

    private val favouritesViewModel by activityViewModels<FavouritesViewModel> {
        FavouritesViewModel.Factory(
            mainActivity!!.favouriteRepository,
            mainActivity!!.preferenceRepository
        )
    }
    protected val viewModel by viewModels<ViewImageViewModel> {
        ViewImageViewModel.Factory(
            requireActivity().application,
            mainActivity!!.photoRepository,
            mainActivity!!.favouriteRepository
        )
    }

    protected val previousFragment by lazy {
        mainActivity?.supportFragmentManager?.run {
            val tag = if (backStackEntryCount > 1) {
                getBackStackEntryAt(backStackEntryCount - 2).name
            } else MainFragment::class.java.name

            findFragmentByTag(tag)
        }
    }

    private var defaultStatusBarColor: Int = 0
    private var defaultIsLightStatusBar: Boolean = false

    private val currentStatusBarColor by lazy {
        ContextCompat.getColor(requireContext(), R.color.system_ui_scrim_dark)
    }

    protected lateinit var item: Item
    protected var exoPlayer: ExoPlayer? = null

    private lateinit var removeItemResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    abstract fun getBottomDrawer(): BottomDrawerViewImageBinding

    abstract fun getBottomDrawerDimView(): View

    abstract fun notifyItemRemoved()

    abstract fun getCurrentImageItemView(): ImageItemView?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerResultLaunchers()

        item = requireArguments().getParcelable(ARGS_ITEM)!!
        viewModel.setItem(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (previousFragment as? BottomDrawerFragment<*>)?.animateHideHiddenRows()

        defaultStatusBarColor = mainActivity?.window?.statusBarColor ?: 0
        defaultIsLightStatusBar = mainActivity?.window?.decorView?.isLightStatusBar() ?: true
        mainActivity?.window?.statusBarColor = currentStatusBarColor
        mainActivity?.setLightStatusBarFlagFromColor()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        previousFragment?.view?.alpha = ALPHA_INVISIBLE
        super.onResume()
    }

    override fun onDestroyView() {
        mainActivity?.hideFullScreen()
        mainActivity?.setLowProfileUI(false)
        releaseExoPlayer()
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        removeItemResultLauncher.unregister()
        copyItemResultLauncher.unregister()
        moveItemResultLauncher.unregister()
    }

    override fun onBackPressed(): Boolean {
        val omitDefaultBackPress = super.onBackPressed()
        if (!omitDefaultBackPress) {
            previousFragment?.view?.animate()?.alpha(ALPHA_VISIBLE)
            (previousFragment as? BottomDrawerFragment<*>)?.animateShowHiddenRows()

            mainActivity?.window?.statusBarColor = defaultStatusBarColor
            mainActivity?.setLightStatusBarFlag(defaultIsLightStatusBar)
        }

        return omitDefaultBackPress.also {
            if (it) {
                sharedViewModel.currentDisplayingList = null
            }
        }
    }

    override fun subscribeUi() = with(sharedViewModel) {

        removedItem.observe(viewLifecycleOwner) {
            if (it != null) {
                val (item, _, _) = it
                if (item.id == this@BaseViewImageFragment.item.id) {
                    Timber.d(
                        "removedItem observe from ${
                            this@BaseViewImageFragment::class.java.name
                        }"
                    )
                    notifyItemRemoved()
                }
            }
        }

        permissionNeededForDelete.observe(viewLifecycleOwner) { intentSender ->
            intentSender?.let {
                val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                removeItemResultLauncher.launch(intentSenderRequest)
            }
        }
    }

    override fun calculatePeekHeight() = with(getBottomDrawer()) {
        listDivider.measuredHeight + topRow.measuredHeight
    }

    override fun initBottomDrawerElements() {

        getBottomDrawer().apply {
            bottomDrawerView = bdrawerViewImage
            bottomSheetBehavior = BottomSheetBehavior.from(bdrawerViewImage)
            bottomSheetExpandButton = btnBdrawerViewImageExpand
        }

        bottomDrawerDim = getBottomDrawerDimView()

        getBottomDrawer().bdrawerViewImage.layoutTransition = LayoutTransition().apply {
            setAnimateParentHierarchy(false)
        }
    }

    private fun registerResultLaunchers() {
        removeItemResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                sharedViewModel.deletePendingItem()
            }
        }
    }

    fun toggleFullScreenMode() {
        if (bottomSheetBehavior.isNotCollapsed()) return
        if (!fullScreenMode) showFullScreen() else hideFullScreen()
    }

    override fun showFullScreen() {
        getBottomDrawer().apply {
            secondRow.gone()
            thirdRow.gone()
        }
        super.showFullScreen()
    }

    override fun hideFullScreen() {
        super.hideFullScreen()

        lifecycleScope.launch {
            delay(DURATION_BOTTOM_SHEET_ANIMATION)
            getBottomDrawer().secondRow.visible()
            getBottomDrawer().thirdRow.visible()
        }
    }

    protected fun setUpVideoPlayer() {

        if (item.getType() == ItemType.VIDEO) {
            getBottomDrawer().videoController.visible()
            hideFullScreen()

            releaseExoPlayer()
            exoPlayer = SimpleExoPlayer.Builder(requireContext())
                .setSeekForwardIncrementMs(10_000) // ms
                .build()

            val mediaItem = MediaItem.fromUri(item.getUri())
            exoPlayer?.apply {
                setMediaItem(mediaItem)
            }

            getBottomDrawer().exoController2.player = exoPlayer

            getCurrentImageItemView()?.let { view ->
                view.binding.playerView.player = exoPlayer
                exoPlayer?.prepare()
                // exoPlayer?.play()
            }

        } else {
            releaseExoPlayer()
            getBottomDrawer().videoController.gone()
        }
        TransitionManager.beginDelayedTransition(getBottomDrawer().bdrawerViewImage)
        changePeekHeight()

    }

    private fun releaseExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
        getBottomDrawer().exoController2.player = null
    }

    fun closeViewer() {
        forceBack = true
        mainActivity?.onBackPressed()
    }

    fun openEditor() {
        val intent = Intent().apply {
            action = Intent.ACTION_EDIT
            setDataAndType(item.getUri(), item.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            // putExtra(MediaStore.EXTRA_OUTPUT, item.getUri())
        }

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            try {
                val chooser = Intent.createChooser(
                    intent,
                    requireContext().getString(R.string.edit_with)
                )
                startActivity(chooser)
            } catch (e: SecurityException) {
                toast(e.toString())
            }
        } else {
            toast(R.string.no_edit_app_found)
        }

    }

    fun shareImage() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, item.getUri())
            type = item.mimeType
        }
        startActivity(
            Intent.createChooser(
                intent,
                resources.getString(R.string.action_send_to_header)
            )
        )

    }

    fun setAs() {
        val intent = Intent().apply {
            action = Intent.ACTION_ATTACH_DATA
            addCategory(Intent.CATEGORY_DEFAULT)
            setDataAndType(item.getUri(), item.mimeType)
            putExtra("mimeType", item.mimeType)
        }
        startActivity(
            Intent.createChooser(
                intent,
                resources.getString(R.string.action_set_as_header)
            )
        )

    }

    fun deleteImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sharedViewModel.deleteItem(item, this::class.java.name)
        } else {
            if (!requireContext().hasWriteExternalPermission()) mainActivity?.toStartActivity()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_warning_dialog_title)
                .setMessage(R.string.delete_warning_dialog_msg)
                .setPositiveButton(R.string.yes) { _, _ ->
                    sharedViewModel.deleteItem(item, this::class.java.name)
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .show()
        }
    }

    fun copyAsFile() {


    }

    fun moveAsFile() {

    }

    fun toggleFavorite() {
        favouritesViewModel.toggleFavourite(item).observe(viewLifecycleOwner) {
            if (it is RecyclerViewListState.ItemInserted) {
                toast(R.string.action_favorite_add_confirm)
            } else if (it is RecyclerViewListState.ItemRemoved) {
                toast(R.string.action_favorite_remove_confirm)
                if (sharedViewModel.itemListFromTab == TAB.FAV) {
                    notifyItemRemoved()
                }
            }
        }
    }

}