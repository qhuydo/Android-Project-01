package hcmus.android.gallery1.helpers.extensions

import android.widget.LinearLayout
import com.google.android.material.tabs.TabLayout

/**
 * remove the gap between tab icon & tab text of the first tab
 */
fun TabLayout.removeGapBetweenTextAndIcon() {
    for (i in 0 until tabCount) {
        val params = getTabAt(i)?.view?.getChildAt(0)?.layoutParams as LinearLayout.LayoutParams?
        params?.bottomMargin = 0
        getTabAt(i)?.view?.getChildAt(0)?.layoutParams = params
    }
}