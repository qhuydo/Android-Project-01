package hcmus.android.gallery1.ui.collectionlist

import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.getCollectionsByDate
import hcmus.android.gallery1.ui.base.CollectionListFragment
import hcmus.android.gallery1.helpers.TAB_DATE

class TabDateFragment : CollectionListFragment(tabName = TAB_DATE) {

    override fun getCollectionList(): List<Collection> {
        return requireContext().getCollectionsByDate()
    }

}
