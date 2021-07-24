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
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.*

abstract class ImageListFragment(private val tabName: String = TAB_ALL) : Fragment() {

    private lateinit var itemListAdapter: ItemListAdapter

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
        val fragmentView = inflater.inflate(
            R.layout.fragment_main_all_photos,
            container,
            false
        )

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Init that RecyclerView
        initRecyclerView()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        itemListAdapter.notifyDataSetChanged()
    }

    abstract fun getItemList(): List<Item>

    fun notifyViewTypeChanged() {
        itemListAdapter = ItemListAdapter(
            items = getItemList(),
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST
        )

        initRecyclerView()
    }

    private fun initRecyclerView() {
        requireView().findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = itemListAdapter
            val spanCount = requireContext().getSpanCountOf(tabName, globalPrefs.getViewMode(tabName))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }
}
