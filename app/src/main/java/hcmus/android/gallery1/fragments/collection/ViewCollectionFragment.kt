package hcmus.android.gallery1.fragments.collection

/*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.data.getItems
import hcmus.android.gallery1.fragments.base.ImageListFragment
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.*

class ViewCollectionFragment(private val collectionId: Long): ImageListFragment() {
    private lateinit var itemAdapter: ItemListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the root view
        val fragmentView = inflater.inflate(R.layout.fragment_main_all_photos, container, false)

        itemAdapter = ItemListAdapter(
            items = requireContext().contentResolver.getItems(collectionId),
            isCompactLayout = globalPrefs.getViewMode(TAB_ALL) == VIEW_LIST
        )

        // Init that RecyclerView
        fragmentView.findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = itemAdapter
            layoutManager = when (globalPrefs.getViewMode(TAB_ALL)) {
                VIEW_LIST   -> LinearLayoutManager(requireContext())
                VIEW_GRID_3 -> GridLayoutManager(requireContext(), 3)
                VIEW_GRID_4 -> GridLayoutManager(requireContext(), 4)
                VIEW_GRID_5 -> GridLayoutManager(requireContext(), 5)
                else     -> GridLayoutManager(requireContext(), 3)
            }
        }

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        itemAdapter.notifyDataSetChanged()
    }
} */
