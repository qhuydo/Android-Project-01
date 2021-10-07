package hcmus.android.gallery1.helpers.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import hcmus.android.gallery1.R
import hcmus.android.gallery1.data.Collection
import hcmus.android.gallery1.data.Item
import hcmus.android.gallery1.helpers.TAB
import hcmus.android.gallery1.ui.base.BaseViewImageFragment
import hcmus.android.gallery1.ui.base.image.ImageListViewModel
import hcmus.android.gallery1.ui.collection.view.ViewCollectionFragment
import hcmus.android.gallery1.ui.image.view.ViewImageFragment
import hcmus.android.gallery1.ui.main.MainActivity

fun MainActivity.navigateToViewImageFragment(
    fromTab: TAB,
    itemPosition: Int,
    imageListViewModel: ImageListViewModel
) {
    imageListViewModel.setCurrentDisplayingList(mainViewModel)
    mainViewModel.currentDisplayingItemPos = itemPosition
    mainViewModel.itemListFromTab = fromTab

    supportFragmentManager.findFragmentById(R.id.fragment_container)?.let {
        it.view?.alpha = 0f
    }

    pushScreen(ViewImageFragment::class.java,
        shouldHideExistingFragment = false,
        transition = FragmentTransaction.TRANSIT_FRAGMENT_FADE
    )
}

private fun MainActivity.pushScreen(
    fragmentClass: Class<out Fragment>,
    bundle: Bundle? = null,
    @IdRes fragmentContainerId: Int = R.id.fragment_container,
    transition: Int? = FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
    shouldHideExistingFragment: Boolean = true
) {
    val fm = supportFragmentManager
    val tag = fragmentClass.name
    val fragmentToBeHidden = fm.findFragmentById(fragmentContainerId)
    fm.commit {
        fragmentToBeHidden?.let { if (shouldHideExistingFragment) hide(it) }
        add(fragmentContainerId, fragmentClass, bundle, tag)
        addToBackStack(tag)
        transition?.let { setTransition(it) }
    }
}

fun MainActivity.navigateToViewImageFragmentNoPager(item: Item) {
    val bundle = Bundle().apply {
        putParcelable(BaseViewImageFragment.ARGS_ITEM, item)
    }
    pushScreen(ViewImageFragment::class.java, bundle)
}

fun MainActivity.navigateToViewCollectionFragment(collection: Collection) {

    val bundle = Bundle().apply {
        putParcelable(ViewCollectionFragment.ARGS_COLLECTION, collection)
    }
    pushScreen(ViewCollectionFragment::class.java, bundle)
}