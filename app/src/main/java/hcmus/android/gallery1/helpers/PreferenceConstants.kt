package hcmus.android.gallery1.helpers

import android.content.res.Resources
import com.google.android.material.tabs.TabLayout
import hcmus.android.gallery1.R

const val TAB_ALL = "all"
const val TAB_FAV = "fav"
const val TAB_ALBUM = "album"
const val TAB_DATE = "date"
const val TAB_FACE = "face"
const val TAB_SECRET = "secret"

const val KEY_THEME = "theme"
const val KEY_LANGUAGE = "language"
const val KEY_FAVORITES = "favorites"
const val KEY_MUTE_AUDIO = "mute_audio"

const val VIEW_MODE_OF = "view_mode_"

const val VIEW_LIST = "list"
const val VIEW_COLLECTION_GRID = "grid_2"
const val VIEW_ITEM_GRID_L = "grid_3"
const val VIEW_ITEM_GRID_M = "grid_4"
const val VIEW_ITEM_GRID_S = "grid_5"

const val BTN_TAB_ITEM_FALLBACK = R.id.btn_viewmode_item_grid_3
const val BTN_TAB_COLLECTION_FALLBACK = R.id.btn_viewmode_collection_grid_2

const val VIEW_TAB_ITEM_FALLBACK = VIEW_ITEM_GRID_L
const val VIEW_TAB_COLLECTION_FALLBACK = VIEW_COLLECTION_GRID

const val THEME_FOLLOW_SYSTEM = "follow_system"
const val THEME_DAY = "day"
const val THEME_NIGHT = "night"

const val LANG_FOLLOW_SYSTEM = "follow_system"
const val LANG_EN = "en"
const val LANG_VI = "vi"
const val LANG_JA = "ja"

const val NOT_EXIST = ""

const val DURATION_BOTTOM_SHEET_ANIMATION = 240L // ms
const val ALPHA_INVISIBLE = 0f
const val ALPHA_VISIBLE = 1f

enum class TAB(val key: String) {
    ALL(TAB_ALL),
    ALBUM(TAB_ALBUM),
    DATE(TAB_DATE),
    FAV(TAB_FAV),
    // unused
    FACE(TAB_FACE),
    SECRET(TAB_SECRET);

    companion object {
        val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    tab.text = textResource(tab.position, tab.view.context.resources)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.text = null
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        }

        fun fromPosition(position: Int) = when (position) {
            ALL.ordinal -> ALL
            ALBUM.ordinal -> ALBUM
            DATE.ordinal -> DATE
            FAV.ordinal -> FAV
            else -> ALL
        }

        fun iconRes(position: Int) = when (position) {
            ALBUM.ordinal -> R.drawable.ic_tab_album
            DATE.ordinal -> R.drawable.ic_tab_date
            FAV.ordinal -> R.drawable.ic_tab_favorite
            else -> R.drawable.ic_tab_all
        }

        fun textResource(position: Int, resource: Resources): CharSequence {
            val resId = when (position) {
                ALBUM.ordinal -> R.string.tab_album
                DATE.ordinal -> R.string.tab_date
                FAV.ordinal -> R.string.tab_favorites
                else -> R.string.tab_all
            }
            return resource.getText(resId)
        }

        fun validTabs(): List<TAB> {
            return listOf(ALL, ALBUM, DATE, FAV)
        }

    }
}