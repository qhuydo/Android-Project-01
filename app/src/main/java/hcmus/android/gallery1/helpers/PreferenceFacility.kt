package hcmus.android.gallery1.helpers

import android.content.SharedPreferences
import androidx.core.content.edit
import hcmus.android.gallery1.R

class PreferenceFacility(private val prefs: SharedPreferences) {
    // Sanity check
    val validTabs           = arrayOf(TAB_ALL, TAB_ALBUM, TAB_DATE, TAB_FACE, TAB_SECRET, TAB_FAV)
    val validViews          = arrayOf(VIEW_LIST, VIEW_GRID_3, VIEW_GRID_4, VIEW_GRID_5)
    val validViewsLimited   = arrayOf(VIEW_LIST, VIEW_GRID_2)
    val validThemes         = arrayOf(THEME_FOLLOW_SYSTEM, THEME_DAY, THEME_NIGHT)
    val validLanguages      = arrayOf(LANG_FOLLOW_SYSTEM, LANG_EN, LANG_VI, LANG_JA)

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

    // Theme
    var theme: String
        get() { return prefs.getString(KEY_THEME, THEME_FOLLOW_SYSTEM) as String }
        set(value) { prefs.edit(commit = true) { putString(KEY_THEME, value) } }

    // Language
    var language: String
        get() {
            return prefs.getString(KEY_LANGUAGE, LANG_EN) as String }
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

    fun getFavorites(): List<Long> {
        val rawSet = prefs.getStringSet(KEY_FAVORITES, setOf())
        val returnList = mutableListOf<Long>()
        if (rawSet != null) {
            for (each in rawSet) {
                returnList += each.toLong()
            }
        }
        return returnList
    }

    fun isInFavorite(imageId: Long) : Boolean {
        val rawSet = prefs.getStringSet(KEY_FAVORITES, setOf())
        if (rawSet != null) {
            return (imageId.toString() in rawSet)
        }
        return false
    }

    fun addFavorite(imageId: Long) {
        val currentSet = prefs.getStringSet(KEY_FAVORITES, setOf())?.toMutableSet()
        currentSet?.add(imageId.toString())
        prefs.edit(commit = true) {
            putStringSet(KEY_FAVORITES, currentSet)
        }
    }

    fun removeFavorite(imageId: Long) {
        val currentSet = prefs.getStringSet(KEY_FAVORITES, setOf())?.toMutableSet()
        if (currentSet != null) {
            if (imageId.toString() in currentSet) {
                currentSet.remove(imageId.toString())
                prefs.edit(commit = true) {
                    putStringSet(KEY_FAVORITES, currentSet)
                }
            }
        }
    }
}
