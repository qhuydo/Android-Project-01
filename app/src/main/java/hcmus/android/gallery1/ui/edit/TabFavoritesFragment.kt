package hcmus.android.gallery1.ui.edit

import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.data.getFavorites
import hcmus.android.gallery1.ui.base.ImageListFragment
import hcmus.android.gallery1.helpers.TAB_FAV

class TabFavoritesFragment : ImageListFragment(tabName = TAB_FAV) {

    override fun getItemList(): List<Item> {
        return requireContext().contentResolver.getFavorites()
    }

}
