package hcmus.android.gallery1.ui.base

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hcmus.android.gallery1.ui.main.MainActivity
import hcmus.android.gallery1.ui.main.MainViewModel

abstract class BaseDialogFragment<B : ViewDataBinding>(@LayoutRes private val resId: Int) :
    DialogFragment() {

    protected val mainActivity by lazy { requireActivity() as MainActivity }
    protected lateinit var binding: B

    protected val sharedViewModel by activityViewModels<MainViewModel> {
        MainViewModel.Factory(
            requireActivity().application,
            mainActivity.customAlbumRepository
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(
            requireActivity().layoutInflater,
            resId,
            null,
            false
        )

        binding.apply {
            bindData()
            lifecycleOwner = this@BaseDialogFragment
            binding.executePendingBindings()
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    abstract fun bindData()
}