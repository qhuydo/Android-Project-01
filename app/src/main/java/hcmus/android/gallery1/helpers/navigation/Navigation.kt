package hcmus.android.gallery1.helpers.navigation

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.ScreenConstant
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.helpers.extensions.getCurrentFragment
import hcmus.android.gallery1.ui.base.BaseFragment.Companion.ARGS_TRANSITION_NAME
import hcmus.android.gallery1.ui.base.BaseViewImageFragment
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.collection.view.ViewCollectionFragment
import hcmus.android.gallery1.ui.collection.view.ViewCustomAlbumFragment
import hcmus.android.gallery1.ui.image.view.ViewImageFragment
import hcmus.android.gallery1.ui.image.view.ViewImageFromCustomAlbumFragment
import hcmus.android.gallery1.ui.main.MainActivity

fun MainActivity.navigateToViewImageFragment(
    fromTab: TAB,
    fromScreen: ScreenConstant,
    itemPosition: Int,
    imageListViewModel: ImageListViewModel
) {
    imageListViewModel.setCurrentDisplayingList(mainViewModel)
    mainViewModel.apply {
        currentDisplayingItemPos = itemPosition
        itemListFromTab = fromTab
        itemListScreenConstant = fromScreen
    }

    supportFragmentManager.findFragmentById(R.id.fragment_container)?.let {
        it.view?.alpha = 0f
    }

    val fragmentClass = if (fromScreen == ScreenConstant.COLLECTION_VIEW_CUSTOM_ALBUM) {
        ViewImageFromCustomAlbumFragment::class.java
    } else {
        ViewImageFragment::class.java
    }

    pushScreenInternal(
        fragmentClass,
        shouldHideExistingFragment = false,
        transition = FragmentTransaction.TRANSIT_FRAGMENT_FADE
    )
}

private fun MainActivity.pushScreenInternal(
    fragmentClass: Class<out Fragment>,
    bundle: Bundle? = null,
    @IdRes fragmentContainerId: Int = R.id.fragment_container,
    transition: Int? = FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
    shouldHideExistingFragment: Boolean = true,
    sharedElement: View? = null,
    sharedElementName: String? = null
) {
    val fm = supportFragmentManager
    val tag = fragmentClass.name
    val fragmentToBeHidden = fm.findFragmentById(fragmentContainerId)

    fm.commit {

        val targetFragment = if (sharedElement != null && sharedElementName != null) {
            setReorderingAllowed(true)
            val transform = MaterialContainerTransform(
                this@pushScreenInternal, true
            ).apply {
                containerColor = MaterialColors.getColor(
                    sharedElement, android.R.attr.colorBackground
                )
                fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            }
            val fragment = fragmentClass.classLoader?.let { classLoader ->
                FragmentFactory().instantiate(classLoader, fragmentClass.name)
            }?.apply {
                sharedElementEnterTransition = transform
                arguments = bundle?.also {
                    it.putString(ARGS_TRANSITION_NAME, sharedElementName)
                }
            }

            addSharedElement(sharedElement, sharedElementName)
            getCurrentFragment()?.let {
                val hold = Hold().apply {
                    addTarget(it.requireView())
                    duration = transform.duration
                }
                it.exitTransition = hold
            }

            fragment
        } else {
            null
        }

        fragmentToBeHidden?.let { if (shouldHideExistingFragment) hide(it) }

        targetFragment?.let {
            add(fragmentContainerId, it, tag)
        } ?: run {
            add(fragmentContainerId, fragmentClass, bundle, tag)
            transition?.let { setTransition(it) }
        }

        addToBackStack(tag)
    }
}

fun MainActivity.navigateToViewImageFragmentNoPager(item: Item) {
    val bundle = Bundle().apply {
        putParcelable(BaseViewImageFragment.ARGS_ITEM, item)
    }
    pushScreenInternal(ViewImageFragment::class.java, bundle)
}

fun MainActivity.navigateToViewCollectionFragment(
    collection: Collection,
    sharedElementView: View?,
    sharedElementName: String?
) {

    val bundle = Bundle().apply {
        putParcelable(ViewCollectionFragment.ARGS_COLLECTION, collection)
    }
    pushScreenInternal(
        ViewCollectionFragment::class.java,
        bundle,
        sharedElement = sharedElementView,
        sharedElementName = sharedElementName
    )
}

fun MainActivity.navigateToViewCustomAlbumFragment(
    collectionId: Long,
    itemView: View?,
    sharedElementName: String?
) {

    val bundle = Bundle().apply {
        putLong(ViewCustomAlbumFragment.ARGS_COLLECTION_ID, collectionId)
    }
    pushScreenInternal(
        ViewCustomAlbumFragment::class.java,
        bundle,
        sharedElement = itemView,
        sharedElementName = sharedElementName
    )
}