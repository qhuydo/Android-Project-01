package hcmus.android.gallery1.ui.image

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.FragmentViewImageNopagerBinding
import hcmus.android.gallery1.ui.main.MainActivity
import hcmus.android.gallery1.ui.main.globalPrefs

class ViewImageFragment : Fragment() {
    companion object {
        const val BUNDLE_ITEM = "item"
    }

    private val mainActivity by lazy { requireActivity() as? MainActivity }
    private lateinit var binding: FragmentViewImageNopagerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetExpandButton: ImageButton
    private lateinit var bottomDrawerDim: View

    private val item: Item by lazy {
        requireArguments().getParcelable(BUNDLE_ITEM)!!
    }
    // private val CREATE_FILE: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity?.setLowProfileUI(true)

        binding = FragmentViewImageNopagerBinding.inflate(inflater, container, false)
        binding.fragment = this

        initBottomSheet()
        // Populate
        populateImageAndInfo()
        binding.executePendingBindings()
        return binding.root
    }


    private fun initBottomSheet() {
        binding.bdrawerViewImageLayout.apply {
            bottomSheetBehavior = BottomSheetBehavior.from(bdrawerViewImage)
            bottomSheetExpandButton = btnBdrawerViewImageExpand
        }

        bottomDrawerDim = binding.bdrawerViewImageDim

        // Behavior
        bottomSheetBehavior.apply { isFitToContents = true }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomDrawerDim.visibility = View.GONE
                        bottomSheetExpandButton.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_bdrawer_up
                            )
                        )
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomDrawerDim.visibility = View.VISIBLE
                        bottomSheetExpandButton.setImageDrawable(
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
                bottomDrawerDim.visibility = View.VISIBLE
                bottomDrawerDim.alpha = slideOffset / 2f
            }
        })

        bottomDrawerDim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Button expansion behavior
        bottomSheetExpandButton.apply {
            setOnClickListener {
                when (bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_COLLAPSED -> bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_EXPANDED

                    BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_COLLAPSED
                    else -> {
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
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

        val imageHolder = binding.image

        Glide.with(imageHolder.context)
            .load(item.getUri())
            .error(R.drawable.placeholder_item)
            .into(imageHolder)

        binding.bdrawerViewImageLayout.bdrawerViewImageInfo.apply {
            val imageName = infoFileName
            imageName.text = item.fileName

            val imageTime = infoTimestamp
            imageTime.text = item.dateModified.toString()

            val imageResolution = infoResolution
            imageResolution.text = item.width.toString()

            val imageFileSize = infoFileSize
            imageFileSize.text = "${item.fileSize} Bytes"

            val imageFilepath = infoFilePath
            imageFilepath.text = item.filePath
        }

    }

    fun closeViewer() {
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
        if (!globalPrefs.isInFavorite(item.id)) {
            globalPrefs.addFavorite(item.id)
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.action_favorite_add_confirm),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            globalPrefs.removeFavorite(item.id)
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.action_favorite_remove_confirm),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

}
