package hcmus.android.gallery1.ui.dialog

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.DialogNewAlbumBinding
import hcmus.android.gallery1.helpers.extensions.observeOnce
import hcmus.android.gallery1.repository.InsertAlbumResult
import hcmus.android.gallery1.ui.base.BaseDialogFragment

class NewAlbumDialog :
    BaseDialogFragment<DialogNewAlbumBinding>(R.layout.dialog_new_album) {

    override fun bindData() = with(binding) {
        btnNewAlbum.setOnClickListener {
            onButtonNewAlbum()
        }
        btnCancel.setOnClickListener { dismiss() }
    }

    private fun onButtonNewAlbum() {
        val text = binding.textInput.text.toString()
        sharedViewModel.addNewCustomAlbum(text).observeOnce(
            this
        ) { result ->
            when (result) {
                InsertAlbumResult.SUCCEED -> dismiss()
                InsertAlbumResult.FAILED_BLANK_NAME -> setErrorBlankName()
                InsertAlbumResult.FAILED_EXISTED_NAME -> setErrorExistedName()
                InsertAlbumResult.FAILED_OTHER -> {
                }
            }
        }
    }

    private fun setErrorExistedName() = setError(R.string.dialog_new_album_error_name_existed)

    private fun setErrorBlankName() = setError(R.string.dialog_new_album_error_blank_name)

    private fun setError(@StringRes res: Int) {
        binding.textInputLayout.error = context?.getString(res)
    }

    companion object {
        fun AppCompatActivity.showNewAlbumDialog() = NewAlbumDialog().also {
            it.show(supportFragmentManager, it::class.java.name)
        }

    }
}