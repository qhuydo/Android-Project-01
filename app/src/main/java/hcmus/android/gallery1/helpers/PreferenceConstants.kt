package hcmus.android.gallery1.helpers

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

const val VIEW_MODE_OF = "view_mode_"

const val VIEW_LIST = "list"
const val VIEW_COLLECTION_GRID = "grid_2"
const val VIEW_ITEM_GRID_L = "grid_3"
const val VIEW_ITEM_GRID_M = "grid_4"
const val VIEW_ITEM_GRID_S = "grid_5"

const val BTN_TAB_ALL_FALLBACK = R.id.btn_viewmode_all_grid_3
const val BTN_TAB_FAV_FALLBACK = R.id.btn_viewmode_fav_grid_3
const val BTN_TAB_ALBUM_FALLBACK = R.id.btn_viewmode_album_grid_2
const val BTN_TAB_DATE_FALLBACK = R.id.btn_viewmode_date_grid_2

const val VIEW_TAB_ALL_FALLBACK = VIEW_ITEM_GRID_L
const val VIEW_TAB_FAV_FALLBACK = VIEW_ITEM_GRID_L
const val VIEW_TAB_ALBUM_FALLBACK = VIEW_COLLECTION_GRID
const val VIEW_TAB_DATE_FALLBACK = VIEW_COLLECTION_GRID

const val THEME_FOLLOW_SYSTEM = "follow_system"
const val THEME_DAY = "day"
const val THEME_NIGHT = "night"

const val LANG_FOLLOW_SYSTEM = "follow_system"
const val LANG_EN = "en"
const val LANG_VI = "vi"
const val LANG_JA = "ja"

const val NOT_EXIST = ""

enum class TAB {
    ALL,
    ALBUM,
    DATE,
    FAV,
}