/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hcmus.android.gallery1.helpers.widgets

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.toRectF
import androidx.core.graphics.withScale
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.abs

private const val ANIM_DURATION = 300L // ms
private const val SELECTED_DRAWABLE_SIZE_PERCENT = 0.4f

class ItemDrawable(
    @ColorInt private val selectedTintColour: Int,
    @Px private val selectedTopLeftCornerRadius: Int,
    private val selectedDrawable: Drawable
) : Drawable() {

    private val thumbnailPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = selectedTintColour
        style = Paint.Style.FILL
    }
    private val tintAlpha = Color.alpha(selectedTintColour)
    private val path = Path()

    private var progress = 0f
        set(value) {
            val clamped = value.coerceIn(0f, 1f)
            if (clamped != field) {
                field = clamped
                update()
            }
        }

    var bitmap: Bitmap? = null
        set(value) {
            if (value != null) {
                thumbnailPaint.shader = BitmapShader(value, CLAMP, CLAMP)
                progress = 0f
            }
        }

    private var progressAnim: ValueAnimator? = null
    private val interp = FastOutSlowInInterpolator()
    private var pivotX = 0f
    private var pivotY = 0f

    private fun update() {
        path.run {
            if (!bounds.isEmpty) {
                reset()
                val cornerRadius = selectedTopLeftCornerRadius * progress
                addRoundRect(
                    bounds.toRectF(),
                    FloatArray(size = 8) { cornerRadius },
                    Path.Direction.CW
                )
            }
        }
        tintPaint.alpha = (progress * tintAlpha).toInt()
        callback?.invalidateDrawable(this)
    }

    override fun onStateChange(state: IntArray?): Boolean {
        val initialProgress = progress
        val newProgress = if (state?.contains(android.R.attr.state_selected) == true) {
            1f
        } else {
            0f
        }
        progressAnim?.cancel()
        progressAnim = ValueAnimator.ofFloat(initialProgress, newProgress).apply {
            addUpdateListener {
                progress = animatedValue as Float
            }
            // scale the duration if was already running
            duration = (abs(newProgress - initialProgress) * ANIM_DURATION).toLong()
            interpolator = interp
        }
        progressAnim?.start()
        return newProgress == initialProgress
    }

    override fun isStateful() = true

    override fun onBoundsChange(bounds: Rect?) {
        if (bounds == null) return
        update()
        val selectedDrawableSize = (bounds.height() * SELECTED_DRAWABLE_SIZE_PERCENT).toInt()

        val dLeft = (bounds.right - selectedDrawableSize) / 2
        val dTop = (bounds.bottom - selectedDrawableSize) / 2
        selectedDrawable.setBounds(
            dLeft,
            dTop,
            dLeft + selectedDrawableSize,
            dTop + selectedDrawableSize
        )
        // scale about the 'corner' of the check mark
        pivotX = (bounds.width() / 2f) - (3f / 24f * selectedDrawableSize)
        pivotY = (bounds.height() / 2f) + (5f / 24f * selectedDrawableSize)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(path, thumbnailPaint)
        if (progress > 0f) {
            canvas.drawPath(path, tintPaint)
            canvas.withScale(progress, progress, pivotX, pivotY) {
                selectedDrawable.draw(canvas)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        thumbnailPaint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(filter: ColorFilter?) {
        thumbnailPaint.colorFilter = filter
    }

}