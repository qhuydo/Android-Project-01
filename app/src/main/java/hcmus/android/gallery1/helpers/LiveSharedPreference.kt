package hcmus.android.gallery1.helpers

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

private class LiveSharedPreference<T>(
    private val sharedPreference: SharedPreferences,
    private val key: String,
    private val getPreferenceValue: () -> T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
            if (key == this.key || key == null) {
                updateIfChanged()
            }
        }

//    @Suppress("UNCHECKED_CAST")
//    fun SharedPreferences.getValue(): T {
//        return when (defaultValue) {
//            is Boolean -> getBoolean(key, defaultValue) as T
//            is Float -> getFloat(key, defaultValue) as T
//            is Int -> getInt(key, defaultValue) as T
//            is Long -> getLong(key, defaultValue) as T
//            is String -> getString(key, defaultValue) as T
//            is Set<*> -> getStringSet(key, defaultValue as Set<String>?) as T
//            is MutableSet<*> -> getStringSet(key, defaultValue as MutableSet<String>?) as T
//            else -> throw IllegalArgumentException()
//        }
//    }

    override fun onActive() {
        super.onActive()
        sharedPreference.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        updateIfChanged()
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreference.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    /** Update the live data value, but only if the value has changed. */
    private fun updateIfChanged() = with(getPreferenceValue()) { if (value != this) value = this }
}


fun SharedPreferences.asLiveData(key: String, default: Int): LiveData<Int> =
    LiveSharedPreference(this, key) { getInt(key, default) }

fun SharedPreferences.asLiveData(key: String, default: Long): LiveData<Long> =
    LiveSharedPreference(this, key) { getLong(key, default) }

fun SharedPreferences.asLiveData(key: String, default: Boolean): LiveData<Boolean> =
    LiveSharedPreference(this, key) { getBoolean(key, default) }

fun SharedPreferences.asLiveData(key: String, default: Float): LiveData<Float> =
    LiveSharedPreference(this, key) { getFloat(key, default) }

fun SharedPreferences.asLiveData(key: String, default: String?): LiveData<String?> =
    LiveSharedPreference(this, key) { getString(key, default) }

fun SharedPreferences.asLiveData(key: String, default: Set<String>?): LiveData<Set<String>?> =
    LiveSharedPreference(this, key) { getStringSet(key, default) }
