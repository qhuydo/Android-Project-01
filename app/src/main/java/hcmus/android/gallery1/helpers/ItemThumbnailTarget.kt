package hcmus.android.gallery1.helpers

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import hcmus.android.gallery1.helpers.widgets.ItemDrawable

class ItemThumbnailTarget(
    imageView: ImageView,
    @ColorInt private val selectedTintColour: Int,
    @Px private val selectedCornerRadius: Int,
    private val selectedDrawable: Drawable
) : ImageViewTarget<Bitmap>(imageView) {

    override fun setResource(resource: Bitmap?) {
        val drawable = (currentDrawable as? ItemDrawable) ?: ItemDrawable(
            selectedTintColour,
            selectedCornerRadius,
            selectedDrawable
        )
        drawable.bitmap = resource
        setDrawable(drawable)
    }
}