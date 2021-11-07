package hcmus.android.gallery1.ui.adapters.viewpager2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.ui.collection.list.AlbumFragment
import hcmus.android.gallery1.ui.collection.list.DateCollectionFragment
import hcmus.android.gallery1.ui.image.list.AllPhotosFragment
import hcmus.android.gallery1.ui.image.list.FavouritesFragment

class TabFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val fragments: Map<Int, () -> Fragment> = mapOf(
        TAB.ALL.ordinal to { AllPhotosFragment() },
        TAB.ALBUM.ordinal to { AlbumFragment() },
        TAB.DATE.ordinal to { DateCollectionFragment() },
        TAB.FAV.ordinal to { FavouritesFragment() }
    )

    override fun createFragment(position: Int): Fragment {
        return fragments[position]?.invoke() ?:  throw IndexOutOfBoundsException()
    }

    override fun getItemCount() = fragments.size

}