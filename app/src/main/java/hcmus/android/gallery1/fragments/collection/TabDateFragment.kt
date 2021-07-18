package hcmus.android.gallery1.fragments.collection

import hcmus.android.gallery1.adapters.CollectionListAdapter
import hcmus.android.gallery1.data.getCollectionsByDate
import hcmus.android.gallery1.fragments.base.CollectionListFragment
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.TAB_DATE
import hcmus.android.gallery1.helpers.VIEW_LIST

class TabDateFragment : CollectionListFragment(tabName = TAB_DATE) {
    override fun getCollectionListAdapter() = CollectionListAdapter(
        items = requireContext().contentResolver.getCollectionsByDate(),
        isCompactLayout = globalPrefs.getViewMode(TAB_DATE) == VIEW_LIST
    )
}
