package hcmus.android.gallery1.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.CollectionListAdapter
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.TAB_ALBUM
import hcmus.android.gallery1.helpers.VIEW_LIST
import hcmus.android.gallery1.helpers.getSpanCountOf

abstract class CollectionListFragment(private val tabName: String = TAB_ALBUM) : Fragment() {

    private lateinit var collectionListAdapter: CollectionListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        collectionListAdapter = CollectionListAdapter(
            items = getCollectionList(),
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST
        )

        // Inflate the root view
        val fragmentView = inflater.inflate(R.layout.fragment_main_album, container, false)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Init that RecyclerView
        initRecyclerView()

        super.onViewCreated(view, savedInstanceState)
    }

    fun notifyViewTypeChanged() {
        collectionListAdapter = CollectionListAdapter(
            items = getCollectionList(),
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST
        )

        initRecyclerView()
    }

    private fun initRecyclerView() {
        requireView().findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = collectionListAdapter
            val spanCount = requireContext().getSpanCountOf(tabName, globalPrefs.getViewMode(tabName))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    override fun onResume() {
        super.onResume()
        collectionListAdapter.notifyDataSetChanged()
    }

    abstract fun getCollectionList(): List<Collection>

}
