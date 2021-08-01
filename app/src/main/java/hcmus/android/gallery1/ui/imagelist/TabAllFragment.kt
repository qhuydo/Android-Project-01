package hcmus.android.gallery1.ui.imagelist

import hcmus.android.gallery1.data.getItems
import hcmus.android.gallery1.ui.base.ImageListFragment
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.TAB_ALL

class TabAllFragment: ImageListFragment(tabName = TAB_ALL) {

    override fun getItemList(): List<Item> {
        return requireContext().contentResolver.getItems(null)
    }

}
