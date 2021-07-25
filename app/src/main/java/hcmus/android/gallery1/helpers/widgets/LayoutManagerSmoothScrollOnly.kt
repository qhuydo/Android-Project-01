package hcmus.android.gallery1.helpers.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class LayoutManagerSmoothScrollOnly: LinearLayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    var isScrollable = false

    override fun canScrollVertically() = isScrollable
    override fun canScrollHorizontally() = isScrollable

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        isScrollable = true
        val linearSmoothScroller = SmoothScroller(recyclerView!!.context)
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    inner class SmoothScroller(context: Context?) : LinearSmoothScroller(context) {
        override fun onStop() {
            super.onStop()
            isScrollable = false
        }
    }
}