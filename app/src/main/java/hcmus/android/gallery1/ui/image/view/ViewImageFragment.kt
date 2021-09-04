package hcmus.android.gallery1.ui.image.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.FragmentViewImageNopagerBinding
import hcmus.android.gallery1.helpers.RecyclerViewListState
import hcmus.android.gallery1.ui.base.BottomDrawerFragment
import hcmus.android.gallery1.ui.image.list.FavouritesViewModel

class ViewImageFragment
    : BottomDrawerFragment<FragmentViewImageNopagerBinding, LinearLayout>(R.layout.fragment_view_image_nopager) {
    companion object {
        const val ARGS_ITEM = "item"
    }

    private val favouritesViewModel by activityViewModels<FavouritesViewModel> {
        FavouritesViewModel.Factory(mainActivity!!.favouriteRepository)
    }
    private val viewModel by viewModels<ViewImageViewModel> {
        ViewImageViewModel.Factory(mainActivity!!.photoRepository)
    }

    private lateinit var item: Item

    // private val CREATE_FILE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        item = requireArguments().getParcelable(ARGS_ITEM)!!
        viewModel.setItem(item)
    }

    override fun subscribeUi() {
        viewModel.item.observe(viewLifecycleOwner) {
            if (it != null) {
                item = it
                populateImageAndInfo()
            }
        }
    }

    override fun bindData() {
        binding.fragment = this
    }

    override fun initBottomDrawerElements() {

        binding.bdrawerViewImageLayout.apply {
            bottomDrawerView = root
            bottomSheetBehavior = BottomSheetBehavior.from(bdrawerViewImage)
            bottomSheetExpandButton = btnBdrawerViewImageExpand
        }

        bottomDrawerDim = binding.bdrawerViewImageDim
        bottomDrawerDim.setOnClickListener {
            toggleFullScreenMode()
        }
    }

    override fun onDestroyView() {
        mainActivity?.hideFullScreen()
        mainActivity?.setLowProfileUI(false)
        super.onDestroyView()
    }

    // A dirty workaround to disable (nearly) all buttons when an external URI is detected
    // (i.e. an image was opened but NOT from the gallery)
    private fun workaroundDisableButtons() = binding.bdrawerViewImageLayout.let {
        val toDisableBtns: List<ImageButton> = listOf(
            it.btnDelete,
            it.btnCopy,
            it.btnMove,
            it.btnSlideshow,
            it.btnFavorite
        )
        for (each in toDisableBtns) {
            each.isEnabled = false
            each.alpha = 0.25f
        }
    }

    private fun populateImageAndInfo() {
        binding.item = item
        binding.executePendingBindings()
    }

    fun toggleFullScreenMode() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED){
            return
        }
        if (!fullScreenMode) {
            showFullScreen()
        }
        else {
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
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.action_delete_confirm),
            Toast.LENGTH_SHORT
        ).show()
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
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.action_favorite_add_confirm),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if (it is RecyclerViewListState.ItemRemoved) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.action_favorite_remove_confirm),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}
