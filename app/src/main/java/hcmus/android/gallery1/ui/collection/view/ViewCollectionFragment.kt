package hcmus.android.gallery1.ui.collection.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.databinding.FragmentViewCollectionBinding
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.*
import hcmus.android.gallery1.helpers.navigation.navigateToViewImageFragment
import hcmus.android.gallery1.ui.adapters.recyclerview.ItemListAdapter
import hcmus.android.gallery1.ui.base.BottomDrawerFragment
import hcmus.android.gallery1.ui.base.collection.CollectionListViewModel
import hcmus.android.gallery1.ui.main.MainFragment
import timber.log.Timber

class ViewCollectionFragment :
    BottomDrawerFragment<FragmentViewCollectionBinding>(R.layout.fragment_view_collection) {

    companion object {
        const val ARGS_COLLECTION = "collection"
    }

    private val mainFragment by lazy {
        activity?.supportFragmentManager?.findFragmentByTag(MainFragment::class.java.name)
                as? MainFragment
    }

    private val tab = TAB.ALL

    // Collection
    private val collection: Collection by lazy {
        requireArguments().getParcelable(ARGS_COLLECTION)!!
    }

    private val itemListAdapterCallback = ItemListAdapter.Callback { _, itemPosition ->
        viewModel.navigateToImageView(itemPosition)
    }

    private val viewModel by viewModels<ViewCollectionViewModel> {
        ViewCollectionViewModel.Factory(
            mainActivity!!.photoRepository,
            preferenceRepository
        )
    }

    private val itemListAdapter: ItemListAdapter by lazy {
        ItemListAdapter(requireContext(), callback = itemListAdapterCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity?.setViewPaddingWindowInset(binding.recyclerView)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun subscribeUi() = with(viewModel) {

        setCollection(this@ViewCollectionFragment.collection)

        collection.observeOnce(viewLifecycleOwner) { viewModel.getPhotos() }

        photos.observeOnce(viewLifecycleOwner) { itemListAdapter.submitList(it) }

        navigateToImageView.observe(viewLifecycleOwner) {
            if (it != null) {
                mainActivity?.navigateToViewImageFragment(tab, it, viewModel)
            }
        }

        viewMode.observe(viewLifecycleOwner) {
            if (it != null) {
                mainFragment?.notifyViewModeChange(tab)
                initRecyclerView(it)
            }
        }

        sharedViewModel.removedItem.observe(viewLifecycleOwner) {
            if (it != null) {
                val (item, _, fragmentName) = it

                if (fragmentName != this::class.java.name) {
                    Timber.d(
                        "removedItem observe from ${
                            this@ViewCollectionFragment::class.java.name
                        }"
                    )
                    viewModel.removeItemFromList(item) { itemPosition ->
                        itemListAdapter.notifyItemRemoved(itemPosition)
                    }
                }
            }

        }
    }


    override fun bindData() = with(binding) {
        collection = this@ViewCollectionFragment.collection
        viewModel = this@ViewCollectionFragment.viewModel
    }

    override fun calculatePeekHeight() = with(binding.bdrawerImageList) {
        listDivider.measuredHeight + topRow.measuredHeight
    }

    override fun initBottomDrawerElements() {
        binding.bdrawerImageList.apply {
            bottomDrawerView = bdrawerImageListStandalone
            bottomSheetBehavior = BottomSheetBehavior.from(bdrawerImageListStandalone)
            bottomSheetExpandButton = btnBottomSheetExpand
        }
        bottomDrawerDim = binding.bdrawerDim
    }

    override fun initBottomDrawerElementsCallback() = with(binding.bdrawerImageList) {
        super.initBottomDrawerElementsCallback()

        btnClose.setOnClickListener { closeCollection() }

        viewmodeAll.viewmodeItem.addOnButtonCheckedListener { _, checkedId, _ ->
            val viewMode = checkedId.viewIdToViewMode()
            preferenceRepository.setViewMode(tab.key, viewMode)
        }
    }

    override fun paddingContainerToFitWithPeekHeight(peekHeight: Int) {
        binding.recyclerView.padding(bottom = peekHeight)
    }

    private fun initRecyclerView(viewMode: String) {
        binding.recyclerView.apply {
            adapter = itemListAdapter
            itemListAdapter.changeCompactLayout(viewMode.isCompactLayout())
            val spanCount = requireContext().getSpanCountOf(tab.key, viewMode)
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    private fun closeCollection() {
        forceBack = true
        activity?.onBackPressed()
    }
}
