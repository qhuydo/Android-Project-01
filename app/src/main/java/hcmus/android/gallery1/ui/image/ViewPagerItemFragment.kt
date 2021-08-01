package hcmus.android.gallery1.ui.image

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import hcmus.android.gallery1.R

/* A fragment containing a single item */
class ViewPagerItemFragment(private val uri: String): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.view_pager_item, container, false)
        view.findViewById<SubsamplingScaleImageView>(R.id.view_pager_image_container)
            .setImage(
                ImageSource.uri(Uri.parse(uri))
            )
        return view
    }
}
