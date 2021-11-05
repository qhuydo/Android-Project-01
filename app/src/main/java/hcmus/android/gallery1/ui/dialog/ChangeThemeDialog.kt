package hcmus.android.gallery1.ui.dialog

import androidx.appcompat.app.AppCompatActivity
import hcmus.android.gallery1.R
import hcmus.android.gallery1.databinding.DialogChangeThemeBinding
import hcmus.android.gallery1.helpers.*
import hcmus.android.gallery1.ui.base.BaseDialogFragment

class ChangeThemeDialog : BaseDialogFragment<DialogChangeThemeBinding>(
    R.layout.dialog_change_theme,
    ScreenConstant.DIALOG_NEW_ALBUM
) {
    private val preferenceRepository by lazy { mainActivity.preferenceRepository }

    override fun bindData() = with(binding) {
        dialogChangeThemeDaynight.check(
            when (preferenceRepository.theme) {
                THEME_DAY -> R.id.button_day_mode
                THEME_NIGHT -> R.id.button_night_mode
                else -> R.id.button_default_mode
            }
        )

        dialogChangeThemeMaterial.check(
            when (preferenceRepository.materialVersion) {
                MATERIAL_2 -> R.id.button_material2
                else -> R.id.button_material3
            }
        )

        buttonSaveTheme.setOnClickListener { saveAndChangeTheme() }
        buttonCancel.setOnClickListener { dismiss() }
    }

    private fun saveAndChangeTheme() {
        val theme = when (binding.dialogChangeThemeDaynight.checkedButtonId) {
            R.id.button_day_mode -> THEME_DAY
            R.id.button_night_mode -> THEME_NIGHT
            else -> THEME_FOLLOW_SYSTEM
        }

        val materialVersion = when (binding.dialogChangeThemeMaterial.checkedButtonId) {
            R.id.button_material2 -> MATERIAL_2
            else -> MATERIAL_3
        }

        mainActivity.changeTheme(theme, materialVersion)
        dismiss()
    }

    companion object {
        fun AppCompatActivity.showChangeThemeDialog() = ChangeThemeDialog().also {
            it.show(supportFragmentManager, it::class.java.name)
        }
    }

}