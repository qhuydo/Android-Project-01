package hcmus.android.gallery1.ui.adapters.recyclerview

import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.ViewPropertyTransition
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.databinding.ListItemBinding
import hcmus.android.gallery1.databinding.ListItemCompactBinding
import hcmus.android.gallery1.helpers.ItemThumbnailTarget


sealed class ItemListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(
        item: Item,
        @ColorInt selectedTintColour: Int,
        @Px selectedCornerRadius: Int,
        selectedDrawable: Drawable
    )

    protected fun ImageView.bindItemDrawable(
        item: Item,
        @ColorInt selectedTintColour: Int,
        @Px selectedCornerRadius: Int,
        selectedDrawable: Drawable
    ) {

        val animationObject = ViewPropertyTransition.Animator { view ->
            view.alpha = 0f
            val fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            fadeAnim.duration = 250
            fadeAnim.start()
        }

        Glide.with(this)
            .asBitmap()
            .load(item.getUri())
            .error(R.drawable.placeholder_item)
            .transition(GenericTransitionOptions.with(animationObject))
            .into(
                ItemThumbnailTarget(
                    this,
                    selectedTintColour,
                    selectedCornerRadius,
                    selectedDrawable
                )
            )
    }

    class ViewHolderCompact(private val binding: ListItemCompactBinding) :
        ItemListViewHolder(binding.root) {

        override fun bind(
            item: Item,
            selectedTintColour: Int,
            selectedCornerRadius: Int,
            selectedDrawable: Drawable
        ) = binding.run {
            itemThumbnail.bindItemDrawable(
                item,
                selectedTintColour,
                selectedCornerRadius,
                selectedDrawable
            )
            this.item = item
            executePendingBindings()
        }
    }

    class ViewHolderGrid(private val binding: ListItemBinding) :
        ItemListViewHolder(binding.root) {

        override fun bind(
            item: Item,
            selectedTintColour: Int,
            selectedCornerRadius: Int,
            selectedDrawable: Drawable
        ) = binding.run {
            itemThumbnail.bindItemDrawable(
                item,
                selectedTintColour,
                selectedCornerRadius,
                selectedDrawable
            )
            this.item = item
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup, type: Int): ItemListViewHolder {

            val layoutInflater = LayoutInflater.from(parent.context)

            return when (type) {
                R.layout.list_item_compact -> {
                    val binding = ListItemCompactBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                    ViewHolderCompact(binding)
                }
                else -> {
                    val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                    ViewHolderGrid(binding)
                }
            }

        }
    }
}