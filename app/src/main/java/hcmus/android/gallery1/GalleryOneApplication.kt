package hcmus.android.gallery1

import android.app.Application
import android.content.res.Configuration
import android.view.Surface
import com.bumptech.glide.Glide
import hcmus.android.gallery1.helpers.configLanguage
import hcmus.android.gallery1.helpers.configTheme
import hcmus.android.gallery1.repository.PreferenceRepository
import timber.log.Timber

class GalleryOneApplication : Application() {

    var lastRotation: Int = Surface.ROTATION_0

    val preferenceRepository by lazy { PreferenceRepository.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        configTimber()
        configTheme(preferenceRepository.theme)
        configLanguage(preferenceRepository.locale)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configTheme(preferenceRepository.theme)
        configLanguage(preferenceRepository.locale)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }

    private fun configTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}