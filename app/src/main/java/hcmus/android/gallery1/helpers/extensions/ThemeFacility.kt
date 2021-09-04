package hcmus.android.gallery1.helpers

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import hcmus.android.gallery1.repository.PreferenceRepository

fun Context.configTheme(preferenceRepository: PreferenceRepository, uiMode: Int?) {

    when(preferenceRepository.theme) {

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
