package hcmus.android.gallery1.helpers.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.customview.widget.ViewDragHelper.INVALID_POINTER
import hcmus.android.gallery1.helpers.extensions.dpToPixel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class PullToRefreshLayout: ViewGroup {

    constructor(context: Context?) : super(context) { init() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    companion object {
        const val FACTOR = 2f
        const val MAX_DRAG_DISTANCE = 40
        const val DRAG_RATE = 0.5f
        const val MAX_OFFSET_ANIMATION_DURATION = 700L
    }

    val totalDragDistance by lazy { context.dpToPixel(MAX_DRAG_DISTANCE) }

    private var targetView: View? = null
    private val decelerateInterpolator by lazy { DecelerateInterpolator(FACTOR) }
    private val scaledTouchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop }
    private val refreshView by lazy { TextView(context).apply { text = "Refresh me!" } }

    private var targetPaddingTop = 0
    private var targetPaddingLeft = 0
    private var targetPaddingRight = 0
    private var targetPaddingBottom = 0

    private var currentOffsetTop = 0
    private var currentDragPercent = 0f
    var isRefreshing = false
        private set

    private var activePointerId = INVALID_POINTER
    private var isBeingDragged = false
    private var initialMotionY = 0f
    private var notify = false
    private var from = 0
    private var fromDragPercent = 0f

    var listener: Listener? = null

    private val onAnimationListener = object: Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
            // No need to implement
        }

        override fun onAnimationEnd(p0: Animation?) {
            // baseRefreshView.stop()
            currentOffsetTop = targetView?.top ?: 0
        }

        override fun onAnimationRepeat(p0: Animation?) {
            // No need to implement
        }

    }

    private val animateToStartPosition: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    private val animateToCorrectPosition: Animation = object : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val endTarget = totalDragDistance
            val targetTop = (from + ((endTarget - from) * interpolatedTime)).toInt()

            val offset = targetTop - (targetView?.top ?: 0)
            currentDragPercent = fromDragPercent - (fromDragPercent - 1.0f) * interpolatedTime
            // mBaseRefreshView.setPercent(mCurrentDragPercent, false)
            setTargetOffsetTop(offset)
        }
    }


    private val toStartListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                // mBaseRefreshView.stop()
                currentOffsetTop = targetView?.top ?: 0
            }
        }

    private fun init() {
        refreshView.updatePadding(
            top = context.dpToPixel(20),
            bottom = context.dpToPixel(20)
        )

        addView(refreshView)
        setWillNotDraw(true)
        isChildrenDrawingOrderEnabled = true
    }

    private fun setUpTargetView() {
        if (targetView != null) return

        if (childCount > 0) {

            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                if (childView != refreshView) {
                    childView.apply {
                        targetView = this
                        targetPaddingTop = paddingTop
                        targetPaddingLeft = paddingLeft
                        targetPaddingRight = paddingRight
                        targetPaddingBottom = paddingBottom
                    }
                    break
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setUpTargetView()
        if (targetView == null) return

        val newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredWidth - paddingRight - paddingLeft,
            MeasureSpec.EXACTLY
        )
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredHeight - paddingTop - paddingBottom,
            MeasureSpec.EXACTLY
        )

        targetView?.measure(newWidthMeasureSpec, newHeightMeasureSpec)
        refreshView.measure(newWidthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        setUpTargetView()
        if (targetView == null) return

        val height = measuredHeight
        val width = measuredWidth
        val left = paddingLeft
        val top = paddingTop
        val right = paddingRight
        val bottom = paddingBottom

        targetView?.layout(
            left,
            top + currentOffsetTop,
            left + width - right,
            top + height - bottom + currentOffsetTop
        )

        refreshView.layout(left, top, left + width - right, top + height - bottom)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        if (!isEnabled || canChildScrollUp() || isRefreshing) return false

        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                setTargetOffsetTop(0)
                activePointerId = ev.getPointerId(0)
                isBeingDragged = false

                val newInitialMotionY = getMotionEventY(ev, activePointerId)
                if (newInitialMotionY < 0) return false
                initialMotionY = newInitialMotionY
            }

            MotionEvent.ACTION_MOVE -> {
                if (activePointerId == INVALID_POINTER) {
                    return false
                }
                val y = getMotionEventY(ev, activePointerId)
                if (y < 0) return false

                val yDiff: Float = y - initialMotionY
                if (yDiff > scaledTouchSlop && !isBeingDragged) {
                    isBeingDragged = true
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                isBeingDragged = false
                activePointerId = INVALID_POINTER
            }

            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(ev)
            }
        }

        return isBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {

        if (!isBeingDragged) return super.onTouchEvent(ev)

        when (ev?.action) {
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(activePointerId)
                if (pointerIndex < 0) return false

                val y = ev.getY(pointerIndex)
                val yDiff: Float = y - initialMotionY
                val scrollTop: Float = yDiff * DRAG_RATE
                currentDragPercent = scrollTop / totalDragDistance
                if (currentDragPercent < 0) {
                    return false
                }
                val boundedDragPercent = min(1f, abs(currentDragPercent))
                val extraOS = abs(scrollTop) - totalDragDistance
                val slingshotDist = totalDragDistance.toFloat()
                val tensionSlingshotPercent = max(
                    0f,
                    Math.min(extraOS, slingshotDist * 2) / slingshotDist
                )

                val tensionPercent = (tensionSlingshotPercent / 4 - (tensionSlingshotPercent / 4).toDouble()
                    .pow(2.0)).toFloat() * 2f

                val extraMove = slingshotDist * tensionPercent / 2
                val targetY = (slingshotDist * boundedDragPercent + extraMove).toInt()

                // mBaseRefreshView.setPercent(mCurrentDragPercent, true)
                setTargetOffsetTop(targetY - currentOffsetTop)
            }

            MotionEvent.ACTION_POINTER_DOWN -> activePointerId = ev.getPointerId(ev.actionIndex)

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (activePointerId == INVALID_POINTER) return false

                val pointerIndex = ev.findPointerIndex(activePointerId)
                val y = ev.getY(pointerIndex)

                val overScrollTop: Float = (y - initialMotionY) * DRAG_RATE
                isBeingDragged = false

                if (overScrollTop > totalDragDistance) {
                    setRefreshing(true, true)
                } else {
                    isRefreshing = false
                    animateOffsetToStartPosition()
                }
                activePointerId = INVALID_POINTER

            }
        }

        return true
    }

    private fun canChildScrollUp() = targetView?.canScrollVertically( -1) == true

    private fun setTargetOffsetTop(offset: Int) {
        targetView?.offsetTopAndBottom(offset)
        // mBaseRefreshView.offsetTopAndBottom(offset)
        currentOffsetTop = targetView?.top ?: 0
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = ev.findPointerIndex(activePointerId)
        return if (index < 0) {
            -1f
        } else ev.getY(index)
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == activePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            activePointerId = ev.getPointerId(newPointerIndex)
        }
    }


    fun setRefreshing(refreshing: Boolean) {
        if (isRefreshing != refreshing) {
            setRefreshing(refreshing, false)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (isRefreshing != refreshing) {
            this.notify = notify
            setUpTargetView()
            isRefreshing = refreshing
            if (isRefreshing) {
                // mBaseRefreshView.setPercent(1f, true)
                animateOffsetToCorrectPosition()
            } else {
                animateOffsetToStartPosition()
            }
        }
    }

    private fun animateOffsetToCorrectPosition() {
        from = currentOffsetTop
        fromDragPercent = currentDragPercent

        animateToCorrectPosition.reset()
        animateToCorrectPosition.duration = MAX_OFFSET_ANIMATION_DURATION
        animateToCorrectPosition.interpolator = decelerateInterpolator

        refreshView.clearAnimation()
        refreshView.startAnimation(animateToCorrectPosition)

        if (isRefreshing) {
            // mBaseRefreshView.start()
            if (notify) {
                listener?.onRefresh()
            }
        } else {
            // mBaseRefreshView.stop()
            animateOffsetToStartPosition()
        }

        currentOffsetTop = targetView?.top ?: 0
        targetView?.setPadding(
            targetPaddingLeft,
            targetPaddingTop,
            targetPaddingRight,
            totalDragDistance
        )
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = from - (from * interpolatedTime).toInt()
        val targetPercent = fromDragPercent * (1.0f - interpolatedTime)
        val offset = targetTop - (targetView?.top ?: 0)

        currentDragPercent = targetPercent
        // mBaseRefreshView.setPercent(mCurrentDragPercent, true)
        targetView?.setPadding(
            targetPaddingLeft,
            targetPaddingTop,
            targetPaddingRight,
            paddingBottom + targetTop
        )
        setTargetOffsetTop(offset)
    }

    private fun animateOffsetToStartPosition() {
        from = currentOffsetTop
        fromDragPercent = currentDragPercent
        val animationDuration = abs((MAX_OFFSET_ANIMATION_DURATION * fromDragPercent).toLong())

        animateToStartPosition.apply {
            reset()
            duration = animationDuration
            interpolator = decelerateInterpolator
            setAnimationListener(toStartListener)
        }

        refreshView.apply {
            clearAnimation()
            startAnimation(animateToStartPosition)
        }

    }


    class Listener(private val onRefreshFn: () -> Unit) {
        fun onRefresh() = onRefreshFn.invoke()
    }
}