package hcmus.android.gallery1.ui.adapters.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeCollectionBinding
import hcmus.android.gallery1.databinding.ButtonGroupViewmodeItemBinding
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.viewIdToViewMode
import hcmus.android.gallery1.ui.main.MainActivity
import timber.log.Timber

abstract class ViewModeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    protected val globalPrefs by lazy { (itemView.context as MainActivity).preferenceRepository }

    abstract fun bind(tab: TAB, viewModeSelectedCallback: OnViewModeSelectedCallback)

    fun setOnButtonCheckedListener(
        tab: TAB,
        viewModeSelectedCallback: OnViewModeSelectedCallback
    ) {
        (itemView as? MaterialButtonToggleGroup)
            ?.apply { clearOnButtonCheckedListeners() }
            ?.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    val viewMode = checkedId.viewIdToViewMode()
                    Timber.d("OnButtonCheckedListener - viewMode = $viewMode, tab = ${tab.key}")
                    viewModeSelectedCallback.onViewModeSelected(tab, viewMode)
                }
            }
    }
}

class ViewModeCollectionViewHolder(private val binding: ButtonGroupViewmodeCollectionBinding) :
    ViewModeViewHolder(binding.root) {

    override fun bind(tab: TAB, viewModeSelectedCallback: OnViewModeSelectedCallback) {
        val tabKey = tab.key
        binding.viewMode = globalPrefs.getViewMode(tabKey)
        setOnButtonCheckedListener(tab, viewModeSelectedCallback)
    }
}

class ViewModeItemViewHolder(private val binding: ButtonGroupViewmodeItemBinding) :
    ViewModeViewHolder(binding.root) {

    override fun bind(tab: TAB, viewModeSelectedCallback: OnViewModeSelectedCallback) {
        val tabKey = tab.key
        binding.viewMode = globalPrefs.getViewMode(tabKey)
        setOnButtonCheckedListener(tab, viewModeSelectedCallback)
    }
}