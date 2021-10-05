package hcmus.android.gallery1.ui.base

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.BottomDrawerViewImageBinding
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.ui.image.list.FavouritesViewModel
import hcmus.android.gallery1.ui.image.view.ViewImageViewModel
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

    protected lateinit var item: Item

    private lateinit var removeItemResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    abstract fun getBottomDrawer(): BottomDrawerViewImageBinding

    abstract fun getBottomDrawerDimView(): View

    abstract fun notifyItemRemoved()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerResultLaunchers()

        item = requireArguments().getParcelable(ARGS_ITEM)!!
        viewModel.setItem(item)
    }

    override fun onDestroyView() {
        mainActivity?.hideFullScreen()
        mainActivity?.setLowProfileUI(false)
        super.onDestroyView()
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
        if (bottomSheetBehavior.isNotCollapsed()) {
            return
        }
        if (!fullScreenMode) {
            showFullScreen()
        } else {
            hideFullScreen()
        }
    }

    fun closeViewer() {
        forceBack = true
        mainActivity?.onBackPressed()
    }

    fun openEditor() {

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