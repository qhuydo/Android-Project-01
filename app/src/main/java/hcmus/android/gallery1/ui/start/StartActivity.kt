package hcmus.android.gallery1.ui.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.DEFAULT_SETTINGS_REQ_CODE
import hcmus.android.gallery1.databinding.ActivityStartBinding
import hcmus.android.gallery1.helpers.extensions.goToAppSetting
import hcmus.android.gallery1.helpers.extensions.toMainActivity
import hcmus.android.gallery1.helpers.extensions.hasReadExternalPermission
import hcmus.android.gallery1.helpers.extensions.requestReadExternalPermission
import hcmus.android.gallery1.helpers.extensions.toast
import hcmus.android.gallery1.helpers.extensions.visible

class StartActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
        const val DEFAULT_SETTINGS_REQUEST_CODE = 0x0906
    }

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        supportActionBar?.hide()

        if (hasReadExternalPermission()) {
            toMainActivity()
            finish()
            return
        } else {
            // requestPermission
            requestReadExternalPermission()
        }

        binding = ActivityStartBinding.inflate(layoutInflater)
        binding.buttonGrantPermission.setOnClickListener {
            if (hasReadExternalPermission()) {
                toMainActivity()
            }
            else {
                goToAppSetting()
            }
        }
        setContentView(binding.root)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // SettingsDialog.Builder(this).build().show()
            binding.root.visible()
        } else {
            requestReadExternalPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        toast("Permission granted")
        toMainActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DEFAULT_SETTINGS_REQUEST_CODE,
            DEFAULT_SETTINGS_REQ_CODE -> {
                if (hasReadExternalPermission()) {
                    toMainActivity()
                    finish()
                    return
                }
            }
        }
    }

}