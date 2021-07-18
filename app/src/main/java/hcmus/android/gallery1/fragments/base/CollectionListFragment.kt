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
import hcmus.android.gallery1.adapters.CollectionListAdapter
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.TAB_ALBUM
import hcmus.android.gallery1.helpers.VIEW_GRID_2
import hcmus.android.gallery1.helpers.VIEW_LIST

abstract class CollectionListFragment(private val tabName: String = TAB_ALBUM) : Fragment() {

    private lateinit var collectionListAdapter: CollectionListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        collectionListAdapter = getCollectionListAdapter()
        // Inflate the root view
        val fragmentView = inflater.inflate(R.layout.fragment_main_album, container, false)

        // Init that RecyclerView
        fragmentView.findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = collectionListAdapter
            layoutManager = when (globalPrefs.getViewMode(tabName)) {
                VIEW_LIST -> LinearLayoutManager(requireContext())
                VIEW_GRID_2 -> GridLayoutManager(requireContext(), 2)
                else     -> GridLayoutManager(requireContext(), 2)
            }
        }

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        collectionListAdapter.notifyDataSetChanged()
    }

    abstract fun getCollectionListAdapter(): CollectionListAdapter
}
