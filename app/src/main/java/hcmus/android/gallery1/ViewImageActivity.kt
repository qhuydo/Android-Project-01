package hcmus.android.gallery1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.data.Item

class ViewImageActivity : AppCompatActivity() {
    lateinit var bottomSheetBehavior: BottomSheetBehavior<BottomNavigationView>
    lateinit var bottomSheetExpandButton: ImageButton
    lateinit var bottomDrawerDim: View

    private lateinit var item: Item
    private val CREATE_FILE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Reset: splash screen "theme" -> default theme
        setTheme(R.style.Theme_GalleryOne)

        // Hide action bar (title bar)
        supportActionBar?.hide()

        // Hide status bar icons
        setLowProfileUI(true)

        setContentView(R.layout.fragment_view_image_nopager)

        initBottomSheet()
        // Populate
        populateImageAndInfo()
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bdrawer_view_image))
        bottomSheetExpandButton = findViewById(R.id.btn_bdrawer_view_image_expand)
        bottomDrawerDim = findViewById(R.id.bdrawer_view_image_dim)

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
    private fun workaroundDisableButtons() {
        val toDisableBtns: List<ImageButton> = listOf(
            findViewById(R.id.btn_delete),
            findViewById(R.id.btn_copy),
            findViewById(R.id.btn_move),
            findViewById(R.id.btn_slideshow),
            findViewById(R.id.btn_favorite)
        )
        for (each in toDisableBtns) {
            each.isEnabled = false
            each.alpha = 0.25f
        }
    }

    private fun populateImageAndInfo() {

        val imageHolder = findViewById<ImageView>(R.id.image)

        item = intent.getParcelableExtra<Item>("item")!!

        Glide.with(imageHolder.context)
            .load(item.getUri())
            .error(R.drawable.placeholder_item)
            .into(imageHolder)

        val imageName = findViewById<TextView>(R.id.info_file_name)
        imageName.text = item.fileName

        val imageTime = findViewById<TextView>(R.id.info_timestamp)
        imageTime.text = item.dateModified.toString()

        val imageResolution = findViewById<TextView>(R.id.info_resolution)
        imageResolution.text = item.width.toString()

        val imageFileSize = findViewById<TextView>(R.id.info_file_size)
        imageFileSize.text = "${item.fileSize} Bytes"

        val imageFilepath = findViewById<TextView>(R.id.info_file_path)
        imageFilepath.text = item.filePath

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
                    globalContext,
                    resources.getString(R.string.action_favorite_add_confirm),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                globalPrefs.removeFavorite(item.id)
                Toast.makeText(
                    globalContext,
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
        this.window?.decorView?.systemUiVisibility = if (isLowProfile) {
            View.SYSTEM_UI_FLAG_LOW_PROFILE
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

}
