package hcmus.android.gallery1.fragments.collection

import hcmus.android.gallery1.adapters.CollectionListAdapter
import hcmus.android.gallery1.data.getCollections
import hcmus.android.gallery1.fragments.base.CollectionListFragment
import hcmus.android.gallery1.globalContext
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.TAB_ALBUM
import hcmus.android.gallery1.helpers.VIEW_LIST

class TabAlbumFragment: CollectionListFragment(
    collectionListAdapter = CollectionListAdapter(
        items = globalContext.contentResolver.getCollections(),
        isCompactLayout = globalPrefs.getViewMode(TAB_ALBUM) == VIEW_LIST
    ),
    tabName = TAB_ALBUM
)
