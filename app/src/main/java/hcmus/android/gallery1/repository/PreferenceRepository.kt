package hcmus.android.gallery1.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import hcmus.android.gallery1.R
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.helpers.extensions.configTheme
import java.util.*

class PreferenceRepository private constructor(applicationContext: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    companion object {
        // Sanity check
        val validTabs = arrayOf(TAB_ALL, TAB_ALBUM, TAB_DATE, TAB_FACE, TAB_SECRET, TAB_FAV)
        val validItemListTabs = arrayOf(TAB_ALL, TAB_FACE, TAB_SECRET, TAB_FAV)
        val validViews = arrayOf(VIEW_LIST, VIEW_ITEM_GRID_L, VIEW_ITEM_GRID_M, VIEW_ITEM_GRID_S)
        val validViewsLimited = arrayOf(VIEW_LIST, VIEW_COLLECTION_GRID)
        val validThemes = arrayOf(THEME_FOLLOW_SYSTEM, THEME_DAY, THEME_NIGHT)
        val validLanguages = arrayOf(LANG_FOLLOW_SYSTEM, LANG_EN, LANG_VI, LANG_JA)

        @Volatile
        private var INSTANCE: PreferenceRepository? = null

        fun getInstance(applicationContext: Context): PreferenceRepository {
            return INSTANCE ?: synchronized(this) {
                PreferenceRepository(applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                KEY_THEME -> configTheme(theme)
            }
        }

    init {
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    // Theme
    var theme: String
        get() {
            return prefs.getString(KEY_THEME, THEME_FOLLOW_SYSTEM) as String
        }
        set(theme) {
            if (theme in validThemes) {
                prefs.edit(commit = true) { putString(KEY_THEME, theme) }
            }
        }

    // Language
    var language: String
        get() {
            return prefs.getString(KEY_LANGUAGE, LANG_EN) as String
        }
        set(lang) {
            if (lang in validLanguages) {
                prefs.edit(commit = true) { putString(KEY_LANGUAGE, lang) }
            }
        }

    val locale: Locale?
        get() = when (language) {
            LANG_FOLLOW_SYSTEM -> null
            else -> Locale(language.lowercase())
        }

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
        get() {
            return getViewMode(TAB_ALL)
        }
        set(value) {
            setViewMode(TAB_ALL, value)
        }

    var tabAlbumViewModel: String
        get() {
            return getViewMode(TAB_ALBUM)
        }
        set(value) {
            setViewMode(TAB_ALBUM, value)
        }

    var tabDateViewMode: String
        get() {
            return getViewMode(TAB_DATE)
        }
        set(value) {
            setViewMode(TAB_DATE, value)
        }

    fun isValidViewMode(tab: String, mode: String): Boolean {
        if (tab !in validTabs) return false
        return when (tab) {
            TAB_ALL, TAB_FAV, TAB_SECRET -> {
                mode in validViews
            }
            else -> {
                mode in validViewsLimited
            }
        }
    }

    // View mode per tab
    fun getViewMode(tab: String): String {
        if (tab in validTabs) {
            val fallback = if (tab in validItemListTabs) {
                VIEW_TAB_ITEM_FALLBACK
            } else VIEW_TAB_COLLECTION_FALLBACK
            return prefs.getString("$VIEW_MODE_OF$tab", fallback) ?: NOT_EXIST
        }
        return NOT_EXIST
    }

    fun getViewMode(tab: TAB): LiveData<String?> {
        val fallback = if (tab.key in validItemListTabs) {
            VIEW_TAB_ITEM_FALLBACK
        } else VIEW_TAB_COLLECTION_FALLBACK
        return prefs.asLiveData("$VIEW_MODE_OF${tab.key}", fallback)
    }

    fun setViewMode(tab: TAB, newMode: String) = setViewMode(tab.key, newMode)

    fun setViewMode(tab: String, newMode: String) {
        if (isValidViewMode(tab, newMode)) {
            prefs.edit(commit = true) { putString("$VIEW_MODE_OF$tab", newMode) }
        }
    }

    fun isCompactLayout(tab: TAB) = getViewMode(tab.key) == VIEW_LIST
}
