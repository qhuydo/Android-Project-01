package hcmus.android.gallery1.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hcmus.android.gallery1.ui.main.ActivityMain

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, ActivityMain::class.java))
        finish()
    }
}
