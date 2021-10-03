package hcmus.android.gallery1.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.BottomDrawerViewImageBinding
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.hideFullScreen
import hcmus.android.gallery1.helpers.extensions.isNotCollapsed
import hcmus.android.gallery1.helpers.extensions.setLowProfileUI
import hcmus.android.gallery1.helpers.extensions.toast
import hcmus.android.gallery1.ui.image.list.FavouritesViewModel
import hcmus.android.gallery1.ui.image.view.ViewImageFragment
import hcmus.android.gallery1.ui.image.view.ViewImageViewModel

abstract class BaseViewImageFragment<B : ViewDataBinding>(@LayoutRes layoutId: Int) :
    BottomDrawerFragment<B>(layoutId) {
    companion object {
        const val ARGS_ITEM = "item"
    }

    protected val favouritesViewModel by activityViewModels<FavouritesViewModel> {
        FavouritesViewModel.Factory(
            mainActivity!!.favouriteRepository,
            mainActivity!!.preferenceRepository
        )
    }
    protected val viewModel by viewModels<ViewImageViewModel> {
        ViewImageViewModel.Factory(
            mainActivity!!.photoRepository,
            mainActivity!!.favouriteRepository
        )
    }

    protected lateinit var item: Item

    abstract fun getBottomDrawer(): BottomDrawerViewImageBinding

    abstract fun getBottomDrawerDimView(): View

    abstract fun notifyItemRemoved()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        item = requireArguments().getParcelable(ARGS_ITEM)!!
        viewModel.setItem(item)
    }

    override fun onDestroyView() {
        mainActivity?.hideFullScreen()
        mainActivity?.setLowProfileUI(false)
        super.onDestroyView()
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
//        mainActivity?.setLowProfileUI(false)
//        val intent = Intent(this, EditImageActivity::class.java)
//        intent.putExtra("uri", item.getUri())
//        startActivity(intent)
    }

    fun shareImage() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, Uri.parse(item.getUri()))
            type = "image/*"
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
            setDataAndType(Uri.parse(item.getUri()), "image/*")
            putExtra("mimeType", "image/*")
        }
        startActivity(
            Intent.createChooser(
                intent,
                resources.getString(R.string.action_set_as_header)
            )
        )

    }

    fun deleteImage() {
        requireContext().contentResolver.delete(Uri.parse(item.getUri()), null, null)
        toast(R.string.action_delete_confirm)
        mainActivity?.onBackPressed()
    }

    fun copyAsFile() {
        /* val intent = Intent().apply {
            action = Intent.ACTION_CREATE_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image"
            putExtra(Intent.EXTRA_TITLE, item.fileName)
        }
        startActivityForResult(intent, CREATE_FILE) */

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