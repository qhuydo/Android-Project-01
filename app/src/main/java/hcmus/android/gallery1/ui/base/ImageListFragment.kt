package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.FragmentMainAllPhotosBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.ui.main.MainActivity
import hcmus.android.gallery1.ui.main.globalPrefs

abstract class ImageListFragment(private val tabName: String = TAB_ALL) : Fragment() {

    private val mainActivity by lazy { requireActivity() as? MainActivity }
    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var binding: FragmentMainAllPhotosBinding

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

        // Inflate the root view
        binding = FragmentMainAllPhotosBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Init that RecyclerView
        initRecyclerView()

        super.onViewCreated(view, savedInstanceState)
    }

//    override fun onResume() {
//        super.onResume()
//        itemListAdapter.notifyDataSetChanged()
//    }

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
            val spanCount = requireContext().getSpanCountOf(tabName, globalPrefs.getViewMode(tabName))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }
}
