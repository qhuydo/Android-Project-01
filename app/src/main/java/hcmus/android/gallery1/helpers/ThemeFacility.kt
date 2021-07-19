package hcmus.android.gallery1.helpers

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

fun Context.configTheme(preferenceFacility: PreferenceFacility, uiMode: Int?) {

    when(preferenceFacility.theme) {

        THEME_DAY -> {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        THEME_NIGHT -> {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }

        else -> {
            configDefaultTheme(uiMode ?: resources.configuration.uiMode)
        }
    }
}

private fun configDefaultTheme(uiMode: Int) {
    when (uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_NO -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }
        Configuration.UI_MODE_NIGHT_YES -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
