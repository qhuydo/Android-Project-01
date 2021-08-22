package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.FragmentMainAllPhotosBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.ui.main.globalPrefs

abstract class ImageListFragment(private val tabName: String = TAB_ALL) :
    BaseFragment<FragmentMainAllPhotosBinding>(R.layout.fragment_main_all_photos) {

    private lateinit var itemListAdapter: ItemListAdapter

    private val itemListAdapterCallback = ItemListAdapter.Callback { item ->
        mainActivity?.pushViewImageFragment(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        itemListAdapter = ItemListAdapter(
            items = getItemList(),
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST,
            callback = itemListAdapterCallback
        )

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun bindData() {
        initRecyclerView()
    }

    abstract fun getItemList(): List<Item>

    fun notifyViewTypeChanged() {
        itemListAdapter = ItemListAdapter(
            items = getItemList(),
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST,
            callback = itemListAdapterCallback
        )

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = itemListAdapter
            val spanCount =
                requireContext().getSpanCountOf(tabName, globalPrefs.getViewMode(tabName))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }
}
