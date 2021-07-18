package hcmus.android.gallery1.fragments.collection

import hcmus.android.gallery1.adapters.ItemListAdapter
import hcmus.android.gallery1.data.getFavorites
import hcmus.android.gallery1.fragments.base.ImageListFragment
import hcmus.android.gallery1.globalPrefs
import hcmus.android.gallery1.helpers.TAB_FAV
import hcmus.android.gallery1.helpers.VIEW_LIST

class TabFavoritesFragment : ImageListFragment(tabName = TAB_FAV) {
    override fun getItemListAdapter() = ItemListAdapter(
        items = requireContext().contentResolver.getFavorites(),
        isCompactLayout = globalPrefs.getViewMode(TAB_FAV) == VIEW_LIST
    )
}
