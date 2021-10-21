package hcmus.android.gallery1.ui.dialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.DialogAddToAlbumBinding
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.ui.adapters.recyclerview.SelectableCollectionListAdapter
import hcmus.android.gallery1.ui.base.BaseDialogFragment
import hcmus.android.gallery1.ui.collection.list.AlbumViewModel

class AddToAlbumDialog : BaseDialogFragment<DialogAddToAlbumBinding>(
    R.layout.dialog_add_to_album,
    ScreenConstant.DIALOG_ADD_TO_ALBUM
) {

    private val viewModel by activityViewModels<AlbumViewModel> {
        AlbumViewModel.Factory(
            mainActivity.collectionRepository,
            mainActivity.customAlbumRepository,
            mainActivity.preferenceRepository
        )
    }

    private var item: Item? = null
    fun setItem(value: Item) {
        item = value
    }

    private val collectionListAdapter by lazy {
        SelectableCollectionListAdapter(
            true,
            SelectableCollectionListAdapter.SelectionTriggerAction.ON_CLICK,
            SelectableCollectionListAdapter.Callback(
                onClickFn = {},
                onSelectionFinished = { list ->
                    item?.let { item ->
                        viewModel.addItemIntoCustomAlbums(item, list)
                    }
                    dismiss()
                }
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState?.containsKey(BUNDLE_ITEM) == true) {
            item = savedInstanceState.getParcelable(BUNDLE_ITEM)
        }
    }

    override fun bindData() = with(binding) {
        customAlbumList.adapter = collectionListAdapter
        viewModel = this@AddToAlbumDialog.viewModel
        actionCancel.setOnClickListener { dismiss() }
        actionAddToAlbum.setOnClickListener { collectionListAdapter.finishSelection() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_ITEM, item)
    }

    companion object {
        fun AppCompatActivity.showAddToAlbumDialog(item: Item) = AddToAlbumDialog().also {
            it.show(supportFragmentManager, it::class.java.name)
            it.setItem(item)
        }

        private const val BUNDLE_ITEM = "item"
    }

}