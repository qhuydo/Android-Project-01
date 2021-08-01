package hcmus.android.gallery1.ui.collectionlist

import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.getCollections
import hcmus.android.gallery1.ui.base.CollectionListFragment
import hcmus.android.gallery1.helpers.TAB_ALBUM

class TabAlbumFragment : CollectionListFragment(tabName = TAB_ALBUM) {

    override fun getCollectionList(): List<Collection> {
        return requireContext().contentResolver.getCollections()
    }

}
