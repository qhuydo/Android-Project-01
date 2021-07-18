package hcmus.android.gallery1.fragments.collection

import hcmus.android.gallery1.data.getItems
import hcmus.android.gallery1.fragments.base.ImageListFragment
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.globalContext
import hcmus.android.gallery1.helpers.TAB_ALL
import hcmus.android.gallery1.helpers.VIEW_LIST

class TabAllFragment: ImageListFragment(
    itemListAdapter = ItemListAdapter(
        items = globalContext.contentResolver.getItems(null),
        isCompactLayout = globalPrefs.getViewMode(TAB_ALL) == VIEW_LIST
    ),
    tabName = TAB_ALL
)
