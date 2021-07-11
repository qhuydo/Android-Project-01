package hcmus.android.gallery1.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.globalPrefs

open class ImageListFragment(
    private var itemListAdapter: ItemListAdapter,
    private val tabName: String = "all"
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the root view
        val fragmentView = inflater.inflate(
            R.layout.fragment_main_all_photos,
            container,
            false
        )

        // Init that RecyclerView
        fragmentView.findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = itemListAdapter
            layoutManager = when (globalPrefs.getViewMode(tabName)) {
                "list"   -> LinearLayoutManager(requireContext())
                "grid_3" -> GridLayoutManager(requireContext(), 3)
                "grid_4" -> GridLayoutManager(requireContext(), 4)
                "grid_5" -> GridLayoutManager(requireContext(), 5)
                else     -> GridLayoutManager(requireContext(), 3)
            }
        }

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        itemListAdapter.notifyDataSetChanged()
    }
}
