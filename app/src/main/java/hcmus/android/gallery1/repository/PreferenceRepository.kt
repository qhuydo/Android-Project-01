package hcmus.android.gallery1.repository

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.*

class PreferenceRepository(applicationContext: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    companion object {
        // Sanity check
        val validTabs           = arrayOf(TAB_ALL, TAB_ALBUM, TAB_DATE, TAB_FACE, TAB_SECRET, TAB_FAV)
        val validViews          = arrayOf(VIEW_LIST, VIEW_ITEM_GRID_L, VIEW_ITEM_GRID_M, VIEW_ITEM_GRID_S)
        val validViewsLimited   = arrayOf(VIEW_LIST, VIEW_COLLECTION_GRID)
        val validThemes         = arrayOf(THEME_FOLLOW_SYSTEM, THEME_DAY, THEME_NIGHT)
        val validLanguages      = arrayOf(LANG_FOLLOW_SYSTEM, LANG_EN, LANG_VI, LANG_JA)

        @Volatile
        private var INSTANCE: PreferenceRepository? = null

        fun getInstance(applicationContext: Context): PreferenceRepository {
            return INSTANCE ?: synchronized(this) {
                PreferenceRepository(applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Theme
    var theme: String
        get() { return prefs.getString(KEY_THEME, THEME_FOLLOW_SYSTEM) as String }
        set(value) { prefs.edit(commit = true) { putString(KEY_THEME, value) } }

    // Language
    var language: String
        get() { return prefs.getString(KEY_LANGUAGE, LANG_EN) as String }
        set(value) { prefs.edit(commit = true) { putString(KEY_LANGUAGE, value) } }

    // Theme (fetch actual resource ID)
    val themeR: Int
        get() {
            return when (theme) {
                THEME_DAY -> R.style.Theme_GalleryOne
                THEME_NIGHT -> R.style.Theme_GalleryOne
                else -> R.style.Theme_GalleryOne // fallback
            }
        }

    var tabAllViewMode: String
        get() { return getViewMode(TAB_ALL) }
        set(value) {
            setViewMode(TAB_ALL, value)
        }

    var tabAlbumViewModel: String
        get() { return getViewMode(TAB_ALBUM) }
        set(value) {
            setViewMode(TAB_ALBUM, value)
        }

    var tabDateViewMode: String
        get() { return getViewMode(TAB_DATE) }
        set(value) {
            setViewMode(TAB_DATE, value)
        }

    fun isValidViewMode(tab: String, mode: String): Boolean {
        if (tab !in validTabs) return false
        return when (tab) {
            TAB_ALL, TAB_FAV, TAB_SECRET -> { mode in validViews }
            else -> { mode in validViewsLimited }
        }
    }

    // View mode per tab
    fun getViewMode(tab: String): String {
        if (tab in validTabs) {
            return prefs.getString("$VIEW_MODE_OF$tab", null) ?: NOT_EXIST
        }
        return NOT_EXIST
    }

    fun setViewMode(tab: String, newMode: String) {
        if (isValidViewMode(tab, newMode)) {
            prefs.edit(commit = true) { putString("$VIEW_MODE_OF$tab", newMode) }
        }
    }

    fun isCompactLayout(tabName: String) = getViewMode(tabName) == VIEW_LIST
}
