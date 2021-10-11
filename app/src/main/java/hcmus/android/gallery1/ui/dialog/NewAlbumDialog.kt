package hcmus.android.gallery1.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.DialogNewAlbumBinding

class NewAlbumDialog private constructor() : DialogFragment() {

    private lateinit var binding: DialogNewAlbumBinding

    internal var callback: Callback? = null

    interface Callback {
        fun onButtonNewAlbumClicked(dialog: NewAlbumDialog, text: String)
        fun onButtonCancelClicked(dialog: NewAlbumDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewAlbumBinding.inflate(
            requireActivity().layoutInflater,
            null,
            false
        ).apply {
            btnNewAlbum.setOnClickListener {
                callback?.onButtonNewAlbumClicked(
                    this@NewAlbumDialog, textInput.text.toString()
                ) ?: cancel()
            }

            btnCancel.setOnClickListener {
                callback?.onButtonCancelClicked(this@NewAlbumDialog) ?: cancel()
            }

        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
        // .apply { requestWindowFeature(Window.FEATURE_NO_TITLE) }
    }

    private fun cancel() = dismiss()

    fun setErrorExistedName() = setError(R.string.dialog_new_album_error_name_existed)

    fun setErrorBlankName() = setError(R.string.dialog_new_album_error_blank_name)

    private fun setError(@StringRes res: Int) {
        binding.textInputLayout.error = context?.getString(res)
    }

    companion object {
        fun AppCompatActivity.showNewAlbumDialog(
            callback: Callback
        ) = NewAlbumDialog().also {
            it.callback = callback
            it.show(supportFragmentManager, it::class.java.name)
        }

    }
}