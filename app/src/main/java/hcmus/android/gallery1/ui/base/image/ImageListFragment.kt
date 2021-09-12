package hcmus.android.gallery1.ui.base.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.TAB_ALL
import hcmus.android.gallery1.helpers.extensions.getSpanCountOf
import hcmus.android.gallery1.ui.adapters.recyclerview.ItemListAdapter
import hcmus.android.gallery1.ui.base.BaseFragment
import timber.log.Timber

abstract class ImageListFragment<B: ViewDataBinding>(
    layoutId: Int = R.layout.fragment_main_all_photos,
    private val tabName: String = TAB_ALL
) : BaseFragment<B>(layoutId) {

    protected lateinit var itemListAdapter: ItemListAdapter
    private val itemListAdapterCallback = ItemListAdapter.Callback { item ->
        imageListViewModel().navigateToImageView(item)
    }

    abstract fun imageListViewModel(): ImageListViewModel

    abstract fun getImageList() : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        itemListAdapter = ItemListAdapter(
            isCompactLayout = preferenceRepository.isCompactLayout(tabName),
            callback = itemListAdapterCallback
        )

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageListViewModel().navigateToImageView.observe(viewLifecycleOwner) {
            if (it != null) {
                mainActivity?.pushViewImageFragment(it)
            }
        }

    }

    override fun bindData() {
        initRecyclerView()
    }

    open fun notifyViewTypeChanged() {
        val items = itemListAdapter.currentList
        itemListAdapter = ItemListAdapter(
            isCompactLayout = preferenceRepository.isCompactLayout(tabName),
            callback = itemListAdapterCallback
        ).apply {
            submitList(items)
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        getImageList().apply {
            adapter = itemListAdapter
            val spanCount = requireContext().getSpanCountOf(
                tabName,
                preferenceRepository.getViewMode(tabName)
            )
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    protected fun startObserveContentChange() {

        sharedViewModel.contentChange.observe(viewLifecycleOwner) {
            Timber.d("Observed content change")
            imageListViewModel().loadData()
            itemListAdapter.notifyDataSetChanged()
        }
    }
}
