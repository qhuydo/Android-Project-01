package hcmus.android.gallery1.ui.base.collectionlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.ui.adapters.CollectionListAdapter
import hcmus.android.gallery1.databinding.FragmentMainAlbumBinding
import hcmus.android.gallery1.helpers.TAB_ALBUM
import hcmus.android.gallery1.helpers.getSpanCountOf
import hcmus.android.gallery1.ui.base.BaseFragment
import hcmus.android.gallery1.ui.collection.ViewCollectionFragment

abstract class CollectionListFragment(private val tabName: String = TAB_ALBUM)
    : BaseFragment<FragmentMainAlbumBinding>(R.layout.fragment_main_album) {

    protected lateinit var collectionListAdapter: CollectionListAdapter

    private val adapterCallback = CollectionListAdapter.Callback { collection ->
       collectionViewModel().navigateToCollectionDetails(collection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionListAdapter = CollectionListAdapter(
            isCompactLayout = preferenceRepository.isCompactLayout(tabName),
            callback = adapterCallback
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectionViewModel().navigateToCollectionDetails.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }

            val fm = requireActivity().supportFragmentManager
            val bundle = Bundle().apply {
                putParcelable(ViewCollectionFragment.ARGS_COLLECTION, it)
            }
            val tag = ViewCollectionFragment::class.java.name
            val fragmentToBeHidden = fm.findFragmentById(R.id.fragment_container)
            fm.commit {
                fragmentToBeHidden?.let { hide(it) }
                add(R.id.fragment_container, ViewCollectionFragment::class.java, bundle, tag)
                addToBackStack(tag)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }
        }
    }

    override fun bindData() {
        initRecyclerView()
    }

    fun notifyViewTypeChanged() {

        val collections = collectionListAdapter.currentList
        collectionListAdapter = CollectionListAdapter(
            isCompactLayout = preferenceRepository.isCompactLayout(tabName),
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
                requireContext().getSpanCountOf(tabName, preferenceRepository.getViewMode(tabName))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
    }

    abstract fun collectionViewModel(): CollectionListViewModel

}
