package hcmus.android.gallery1.ui.adapters.binding

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.BindingAdapter

@BindingAdapter(
    value = [
        "paddingLeftSystemWindowInsets",
        "paddingTopSystemWindowInsets",
        "paddingRightSystemWindowInsets",
        "paddingBottomSystemWindowInsets",
    ],
    requireAll = false
)
fun View.applySystemWindowInsetsPadding(
    previousApplyLeft: Boolean,
    previousApplyTop: Boolean,
    previousApplyRight: Boolean,
    previousApplyBottom: Boolean,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {

    if (previousApplyLeft == applyLeft &&
        previousApplyTop == applyTop &&
        previousApplyRight == applyRight &&
        previousApplyBottom == applyBottom
    ) {
        return
    }

    doOnApplyWindowInsets { view, windowInsets, padding, _, _ ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        val left = if (applyLeft == true) insets.left else 0
        val top = if (applyTop == true) insets.top else 0
        val right = if (applyRight == true) insets.right else 0
        val bottom = if (applyBottom == true) insets.bottom else 0

        view.setPadding(
            padding.left + left,
            padding.top + top,
            padding.right + right,
            padding.bottom + bottom
        )
    }
}

fun View.doOnApplyWindowInsets(
    block: (View, WindowInsetsCompat, InitialPadding, InitialMargin, Int) -> Unit
) {
    // Create a snapshot of the view's padding & margin states
    val initialPadding = recordInitialPaddingForView(this)
    val initialMargin = recordInitialMarginForView(this)
    val initialHeight = recordInitialHeightForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding & margin states
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding, initialMargin, initialHeight)
        insets
    }

    // request some insets
    requestApplyInsetsWhenAttached()
}


class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialMarginForView(view: View): InitialMargin {
    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        ?: return InitialMargin(0, 0, 0, 0)
    return InitialMargin(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
}

private fun recordInitialHeightForView(view: View): Int {
    return view.layoutParams.height
}


fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}