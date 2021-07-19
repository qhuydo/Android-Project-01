package hcmus.android.gallery1.fragments.collection

import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.data.getFavorites
import hcmus.android.gallery1.fragments.base.ImageListFragment
import hcmus.android.gallery1.helpers.TAB_FAV

class TabFavoritesFragment : ImageListFragment(tabName = TAB_FAV) {

    override fun getItemList(): List<Item> {
        return requireContext().contentResolver.getFavorites()
    }

}
