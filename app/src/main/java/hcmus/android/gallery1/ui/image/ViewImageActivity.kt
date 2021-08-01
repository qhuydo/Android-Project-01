package hcmus.android.gallery1.ui.image

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.FragmentViewImageNopagerBinding
import hcmus.android.gallery1.helpers.configTheme
import hcmus.android.gallery1.ui.main.globalPrefs

class ViewImageActivity : AppCompatActivity() {
    private lateinit var binding: FragmentViewImageNopagerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetExpandButton: ImageButton
    private lateinit var bottomDrawerDim: View

    private lateinit var item: Item
    private val CREATE_FILE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configTheme(globalPrefs, null)

        // Reset: splash screen "theme" -> default theme
        setTheme(R.style.Theme_GalleryOne)

        // Hide action bar (title bar)
        supportActionBar?.hide()

        // Hide status bar icons
        setLowProfileUI(true)

        binding = FragmentViewImageNopagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomSheet()
        // Populate
        populateImageAndInfo()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configTheme(globalPrefs, newConfig.uiMode)
        recreate()
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
                                this@ViewImageActivity,
                                R.drawable.ic_bdrawer_up
                            )
                        )
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomDrawerDim.visibility = View.VISIBLE
                        bottomSheetExpandButton.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ViewImageActivity,
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

        item = intent.getParcelableExtra<Item>("item")!!

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

    fun closeViewer(view: View) {
        if (view.id == R.id.btn_close_viewer) {
            setLowProfileUI(false)
            finish()
        }
    }

    fun openEditor(view: View) {
//        if (view.id == R.id.btn_edit) {
//            setLowProfileUI(false)
//            val intent = Intent(this, EditImageActivity::class.java)
//            intent.putExtra("uri", item.getUri())
//            startActivity(intent)
//        }
    }

    fun shareImage(view: View) {
        if (view.id == R.id.btn_share) {
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
    }

    fun setAs(view: View) {
        if (view.id == R.id.btn_wallpaper) {
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
    }

    fun deleteImage(view: View) {
        if (view.id == R.id.btn_delete) {
            contentResolver.delete(Uri.parse(item.getUri()), null, null)
            Toast.makeText(
                this,
                resources.getString(R.string.action_delete_confirm),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    fun copyAsFile(view: View) {
        if (view.id == R.id.btn_copy) {
            /* val intent = Intent().apply {
                action = Intent.ACTION_CREATE_DOCUMENT
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image"
                putExtra(Intent.EXTRA_TITLE, item.fileName)
            }
            startActivityForResult(intent, CREATE_FILE) */
        }
    }

    fun moveAsFile(view: View) {
        if (view.id == R.id.btn_move) {

        }
    }

    fun toggleFavorite(view: View) {
        if (view.id == R.id.btn_favorite) {
            if (!globalPrefs.isInFavorite(item.id)) {
                globalPrefs.addFavorite(item.id)
                Toast.makeText(
                    this,
                    resources.getString(R.string.action_favorite_add_confirm),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                globalPrefs.removeFavorite(item.id)
                Toast.makeText(
                    this,
                    resources.getString(R.string.action_favorite_remove_confirm),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Temporarily turn on "lights out" mode for status bar and navigation bar.
    // This usually means hiding nearly everything and leaving with only the clock and battery status.
    // https://stackoverflow.com/a/44433844
    private fun setLowProfileUI(isLowProfile: Boolean) {
        val flag = this.window?.decorView?.systemUiVisibility
        flag?.let {
            if (isLowProfile) {
                this.window?.decorView?.systemUiVisibility = flag or View.SYSTEM_UI_FLAG_LOW_PROFILE
            } else {
                this.window?.decorView?.systemUiVisibility = flag or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

}
