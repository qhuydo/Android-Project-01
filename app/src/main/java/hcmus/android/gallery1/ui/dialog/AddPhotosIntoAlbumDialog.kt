package hcmus.android.gallery1.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hcmus.android.gallery1.databinding.DialogAddPhotosIntoAlbumBinding
import hcmus.android.gallery1.ui.adapters.recyclerview.SelectableItemListAdapter
import hcmus.android.gallery1.ui.collection.view.ViewCustomAlbumFragment
import hcmus.android.gallery1.ui.collection.view.ViewCustomAlbumViewModel
import hcmus.android.gallery1.ui.image.list.AllPhotosViewModel
import hcmus.android.gallery1.ui.main.MainActivity

class AddPhotosIntoAlbumDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogAddPhotosIntoAlbumBinding

    private val photosViewModel by activityViewModels<AllPhotosViewModel>()
    private val mainActivity by lazy { requireActivity() as? MainActivity }
    private val viewCustomAlbumViewModel by viewModels<ViewCustomAlbumViewModel>(
        ownerProducer = {
            parentFragmentManager.findFragmentByTag(ViewCustomAlbumFragment::class.java.name)
                ?: this
        }
    ) {
        ViewCustomAlbumViewModel.Factory(
            mainActivity!!.photoRepository,
            mainActivity!!.customAlbumRepository,
            mainActivity!!.preferenceRepository
        )
    }

    private val itemAdapter by lazy {
        SelectableItemListAdapter(
            selectionTriggerAction = SelectableItemListAdapter.SelectionTriggerAction.ON_CLICK,
            callback = SelectableItemListAdapter.Callback(
                onClickFn = { _, _ -> },
                onSelectionFinished = {
                    viewCustomAlbumViewModel.addItemsIntoCustomAlbums(it)
                    dismiss()
                }
            )
        )
    }

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

    private fun bindData() = with(binding) {
        btnBackToMain.setOnClickListener { dismiss() }
        photosViewModel = this@AddPhotosIntoAlbumDialog.photosViewModel
        recyclerView.adapter = itemAdapter
        btnAddPhoto.setOnClickListener { itemAdapter.finishSelection() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    companion object {
        fun AppCompatActivity.showAddPhotosIntoAlbumDialog() = AddPhotosIntoAlbumDialog().also {
            it.show(supportFragmentManager, AddPhotosIntoAlbumDialog::class.java.name)
        }
    }
}