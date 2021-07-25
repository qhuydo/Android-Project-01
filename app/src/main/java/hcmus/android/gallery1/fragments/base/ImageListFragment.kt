package hcmus.android.gallery1.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.FragmentMainAllPhotosBinding
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.*

abstract class ImageListFragment(private val tabName: String = TAB_ALL) : Fragment() {

    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var binding: FragmentMainAllPhotosBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        itemListAdapter = ItemListAdapter(
            items = getItemList(),
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST
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
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST
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
