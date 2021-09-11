package hcmus.android.gallery1.helpers.extensions

import android.view.Surface

fun Int.isHorizontalRotation() = this == Surface.ROTATION_90 || this == Surface.ROTATION_270
