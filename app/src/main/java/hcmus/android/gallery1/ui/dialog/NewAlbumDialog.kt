package hcmus.android.gallery1.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.DialogNewAlbumBinding
import hcmus.android.gallery1.helpers.extensions.observeOnce
import hcmus.android.gallery1.repository.InsertAlbumResult
import hcmus.android.gallery1.ui.main.MainActivity
import hcmus.android.gallery1.ui.main.MainViewModel

class NewAlbumDialog private constructor() : DialogFragment() {

    private val mainActivity by lazy { requireActivity() as MainActivity }
    private lateinit var binding: DialogNewAlbumBinding

    private val sharedViewModel by activityViewModels<MainViewModel> {
        MainViewModel.Factory(
            requireActivity().application,
            mainActivity.customAlbumRepository
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewAlbumBinding.inflate(
            requireActivity().layoutInflater,
            null,
            false
        ).apply {
            btnNewAlbum.setOnClickListener {
                onButtonNewAlbum()
            }
            btnCancel.setOnClickListener { dismiss() }
            lifecycleOwner = this@NewAlbumDialog
            executePendingBindings()
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
        // .apply { requestWindowFeature(Window.FEATURE_NO_TITLE) }
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