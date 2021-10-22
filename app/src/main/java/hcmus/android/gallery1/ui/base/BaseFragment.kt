package hcmus.android.gallery1.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.extensions.animateFadeUp
import hcmus.android.gallery1.ui.main.MainActivity
import hcmus.android.gallery1.ui.main.MainViewModel
import java.lang.ref.WeakReference

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    protected val screenConstant: ScreenConstant
) : Fragment() {

    protected val mainActivity by lazy { requireActivity() as? MainActivity }
    protected val preferenceRepository by lazy { mainActivity!!.preferenceRepository }
    protected lateinit var binding: B

    protected val sharedViewModel by activityViewModels<MainViewModel> {
        MainViewModel.Factory(
            requireActivity().application,
            mainActivity!!.customAlbumRepository
        )
    }

    protected var dialogToDismiss = WeakReference<Any>(null)

    open fun onBackPressed(): Boolean = false

    /**
     * Set the properties of the generated binding class
     * (class holds all the bindings from the layout properties to the layout's views)
     */
    abstract fun bindData()

    /**
     * Observe liveData given from view model
     */
    abstract fun subscribeUi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindData()
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        subscribeUi()
    }

    override fun onPause() {
        super.onPause()
        dismissCurrentlyDisplayedDialog()
    }

    private fun dismissCurrentlyDisplayedDialog() {
        when (val dialog = dialogToDismiss.get()) {
            is Dialog -> dialog.dismiss()
            // is DialogFragment -> dialog.dismissAllowingStateLoss()
        }
        dialogToDismiss.clear()
    }

    fun animateFadeUp() = view?.animateFadeUp()

}