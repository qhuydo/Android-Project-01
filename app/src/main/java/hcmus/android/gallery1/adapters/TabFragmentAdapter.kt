package hcmus.android.gallery1.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hcmus.android.gallery1.R
import hcmus.android.gallery1.fragments.collection.TabAlbumFragment
import hcmus.android.gallery1.fragments.collection.TabAllFragment
import hcmus.android.gallery1.fragments.collection.TabDateFragment
import hcmus.android.gallery1.fragments.collection.TabFavoritesFragment
import hcmus.android.gallery1.helpers.TAB

class TabFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val fragments: Map<Int, () -> Fragment> = mapOf(
        TAB.ALL.ordinal to { TabAllFragment() },
        TAB.ALBUM.ordinal to { TabAlbumFragment() },
        TAB.DATE.ordinal to { TabDateFragment() },
        TAB.FAV.ordinal to { TabFavoritesFragment() }
    )

    override fun createFragment(position: Int): Fragment {
        return fragments[position]?.invoke() ?:  throw IndexOutOfBoundsException()
    }

    override fun getItemCount() = fragments.size
}