package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.adapters.CollectionListAdapter
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.databinding.FragmentMainAlbumBinding
import hcmus.android.gallery1.helpers.TAB_ALBUM
import hcmus.android.gallery1.helpers.VIEW_LIST
import hcmus.android.gallery1.helpers.getSpanCountOf
import hcmus.android.gallery1.ui.collection.ViewCollectionFragment
import hcmus.android.gallery1.ui.main.MainFragment
import hcmus.android.gallery1.ui.main.globalPrefs

abstract class CollectionListFragment(private val tabName: String = TAB_ALBUM)
    : BaseFragment<FragmentMainAlbumBinding>(R.layout.fragment_main_album) {

    protected lateinit var collectionListAdapter: CollectionListAdapter

    private val adapterCallback = CollectionListAdapter.Callback { item ->
        val fm = requireActivity().supportFragmentManager
        val bundle = Bundle().apply {
            putParcelable(ViewCollectionFragment.BUNDLE_COLLECTION, item)
        }
        val tag = ViewCollectionFragment::class.java.name
        fm.beginTransaction()
            .hide(fm.findFragmentByTag(MainFragment::class.java.name)!!)
            .add(R.id.fragment_container, ViewCollectionFragment::class.java, bundle, tag)
            .addToBackStack(tag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectionListAdapter = CollectionListAdapter(
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST,
            callback = adapterCallback
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun bindData() {
        initRecyclerView()
    }

    fun notifyViewTypeChanged() {

        val collections = collectionListAdapter.currentList
        collectionListAdapter = CollectionListAdapter(
            isCompactLayout = globalPrefs.getViewMode(tabName) == VIEW_LIST,
            callback = adapterCallback
        ).also {
            it.submitList(collections)
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = collectionListAdapter
            val spanCount =
                requireContext().getSpanCountOf(tabName, globalPrefs.getViewMode(tabName))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    abstract fun getCollectionList(): List<Collection>

}
