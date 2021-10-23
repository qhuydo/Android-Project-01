package hcmus.android.gallery1.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hcmus.android.gallery1.databinding.DialogAddPhotosIntoAlbumBinding
import hcmus.android.gallery1.ui.adapters.recyclerview.ItemListAdapter
import hcmus.android.gallery1.ui.image.list.AllPhotosViewModel

class AddPhotosIntoAlbumDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogAddPhotosIntoAlbumBinding

    private val photosViewModel by activityViewModels<AllPhotosViewModel>()
    private val itemAdapter by lazy { ItemListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddPhotosIntoAlbumBinding.inflate(
            layoutInflater, container, false
        )

        binding.apply {
            bindData()
            lifecycleOwner = this@AddPhotosIntoAlbumDialog
            binding.executePendingBindings()
        }

        return binding.root
    }

    private fun bindData() {
        binding.btnBackToMain.setOnClickListener { dismiss() }
        binding.photosViewModel = photosViewModel
        binding.recyclerView.adapter = itemAdapter
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    companion object {
        fun Fragment.showAddPhotosIntoAlbumDialog() = AddPhotosIntoAlbumDialog().also {
            it.show(childFragmentManager, AddPhotosIntoAlbumDialog::class.java.name)
        }
    }
}