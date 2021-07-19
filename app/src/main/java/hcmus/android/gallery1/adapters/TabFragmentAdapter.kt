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

    private val fragments = HashMap<Int, Fragment>().apply {
        put(TAB.ALL.ordinal, TabAllFragment())
        put(TAB.ALBUM.ordinal, TabAlbumFragment())
        put(TAB.DATE.ordinal, TabDateFragment())
        put(TAB.FAV.ordinal, TabFavoritesFragment())
    }

    private val itemIds = listOf(
        R.id.tab_all,
        R.id.tab_album,
        R.id.tab_date,
        R.id.tab_favorites
    ).map { it.toLong() }

    fun fragmentAt(position: Int) : Fragment? {
        return fragments.get(position)
    }

    override fun createFragment(position: Int): Fragment {
        return fragments.getOrDefault(position, fragments[TAB.ALL.ordinal]!!)
//        return when(position) {
//            TAB.ALBUM.ordinal -> TabAlbumFragment()
//            TAB.DATE.ordinal -> TabDateFragment()
//            TAB.FAV.ordinal -> TabFavoritesFragment()
//            else -> TabAllFragment()
//        }
    }

    override fun getItemCount() = fragments.size

    override fun getItemId(position: Int): Long {
        return itemIds[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemIds.contains(itemId)
    }

}