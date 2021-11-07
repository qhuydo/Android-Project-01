package hcmus.android.gallery1.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs
import kotlin.math.min

const val DEFAULT_THRESHOLD = 4_000 // pixels per second

class FlingLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    var onPositionChanged: ((dragPercent: Float) -> Unit)? = null
    var onViewDismissed: (() -> Unit)? = null

    private var childX: Int = 0
    private var childY: Int = 0
    private var dragPercent: Float? = null

    private val dragHelper by lazy {

        val dragHelperCallback = object : ViewDragHelper.Callback() {

            override fun tryCaptureView(child: View, pointerId: Int) = true

            override fun getViewVerticalDragRange(child: View) = 1

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int) = top

            override fun onViewPositionChanged(
                changedView: View,
                left: Int,
                top: Int,
                dx: Int,
                dy: Int
            ) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
                onViewPositionChanged(top)
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)
                onViewReleased(releasedChild, yvel)
            }
        }

        ViewDragHelper.create(this, 1f, dragHelperCallback)
    }

    private fun onViewReleased(target: View, yVelocity: Float) {
        dragPercent = null

        val x = childX
        val y = childY

        if (abs(yVelocity) < DEFAULT_THRESHOLD) {
            dragHelper.settleCapturedViewAt(x, y)
            invalidate()
            return
        }

        val targetY = if (yVelocity > 0) {
            measuredHeight
        } else {
            -target.measuredHeight
        }

        dragHelper.smoothSlideViewTo(target, x, targetY)
        invalidate()

        onViewDismissed?.invoke()
    }

    private fun onViewPositionChanged(top: Int) {
        val defaultChildY = childY
        val rangeY = (measuredHeight / 2)
        val distance = abs(top - defaultChildY)

        dragPercent = min(distance.toFloat() / rangeY, 1f)
        onPositionChanged?.invoke(dragPercent ?: 0f)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent) = dragHelper.shouldInterceptTouchEvent(ev)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (childCount == 0) {
            return
        }
        else if (childCount > 1) {
            throw IllegalStateException()
        }

        childX = getChildAt(0)?.left ?: 0
        childY = getChildAt(0)?.top ?: 0
    }

    override fun computeScroll() {
        super.computeScroll()
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }
}