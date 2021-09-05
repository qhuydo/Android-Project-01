package hcmus.android.gallery1.helpers.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireContext(), text, duration).show()


fun Fragment.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireContext(), resId, duration)
