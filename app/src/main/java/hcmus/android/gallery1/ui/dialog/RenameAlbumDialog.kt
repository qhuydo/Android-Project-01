package hcmus.android.gallery1.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.DialogRenameAlbumBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.extensions.observeOnce
import hcmus.android.gallery1.repository.RenameAlbumResult
import hcmus.android.gallery1.ui.base.BaseDialogFragment
import hcmus.android.gallery1.ui.collection.view.ViewCustomAlbumFragment
import hcmus.android.gallery1.ui.collection.view.ViewCustomAlbumViewModel

class RenameAlbumDialog : BaseDialogFragment<DialogRenameAlbumBinding>(
    R.layout.dialog_rename_album,
    ScreenConstant.DIALOG_RENAME
) {

    private val viewModel by viewModels<ViewCustomAlbumViewModel>(
        ownerProducer = { requireParentFragment() }
    )

    override fun bindData() = with(binding) {
        btnDialogRenameAlbum.setOnClickListener {
            onButtonRenameAlbum()
        }
        btnCancel.setOnClickListener { dismiss() }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customAlbum.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.textInput.setText(it.name)
                binding.btnDialogRenameAlbum.isEnabled = true
            }
        }
    }

    private fun onButtonRenameAlbum() {
        val name = binding.textInput.text.toString()
        viewModel.renameCollection(name).observeOnce(
            this
        ) { result ->
            when (result) {
                RenameAlbumResult.SUCCEED -> dismiss()
                RenameAlbumResult.FAILED_BLANK_NAME -> setErrorBlankName()
                RenameAlbumResult.FAILED_EXISTED_NAME -> setErrorExistedName()
                RenameAlbumResult.FAILED_OTHER -> {
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
        fun ViewCustomAlbumFragment.showRenameAlbumDialog() = RenameAlbumDialog().also {
            it.show(childFragmentManager, it::class.java.name)
        }
    }

}