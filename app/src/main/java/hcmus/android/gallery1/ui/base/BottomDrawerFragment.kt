package hcmus.android.gallery1.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BottomDrawerFragment(private val layoutR: Int): Fragment() {
    private lateinit var bDrawerBehavior: BottomSheetBehavior<BottomNavigationView>
    private lateinit var bDrawerBtnExpand: ImageButton
    private lateinit var bDrawerDim: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutR, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findElements()
        postViewCreated()
    }

    abstract fun findElements()
    abstract fun postViewCreated()
}
