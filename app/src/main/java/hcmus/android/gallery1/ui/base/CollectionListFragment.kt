package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import hcmus.android.gallery1.adapters.CollectionListAdapter
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.databinding.FragmentMainAlbumBinding
import hcmus.android.gallery1.helpers.TAB_ALBUM
import hcmus.android.gallery1.helpers.VIEW_LIST
import hcmus.android.gallery1.helpers.getSpanCountOf
import hcmus.android.gallery1.ui.main.globalPrefs

abstract class CollectionListFragment(private val tabName: String = TAB_ALBUM) : Fragment() {

    private lateinit var collectionListAdapter: CollectionListAdapter
    internal lateinit var binding: FragmentMainAlbumBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectionListAdapter = CollectionListAdapter(
            items = getCollectionList(),
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST
        )

        // Inflate the root view
        binding = FragmentMainAlbumBinding.inflate(inflater, container, false)

        return binding.root
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
        binding.recyclerView.apply {
            adapter = collectionListAdapter
            val spanCount = requireContext().getSpanCountOf(tabName, globalPrefs.getViewMode(tabName))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

//    override fun onResume() {
//        super.onResume()
//        collectionListAdapter.notifyDataSetChanged()
//    }

    abstract fun getCollectionList(): List<Collection>

}
