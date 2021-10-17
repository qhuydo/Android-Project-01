package hcmus.android.gallery1.ui.adapters.recyclerview

import hcmus.android.gallery1.data.Collection
import kotlinx.coroutines.*

class SelectableCollectionListAdapter(
    isCompactLayout: Boolean = false,
    private val selectionTriggerAction: SelectionTriggerAction = SelectionTriggerAction.ON_LONG_PRESS,
    callback: Callback? = null,
) : CollectionListAdapter(isCompactLayout, callback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default + Job())
    private val selectedCollections = mutableSetOf<Long>()
    private var isInSelectionState = false

    enum class SelectionTriggerAction {
        ON_LONG_PRESS,
        ON_CLICK
    }

    override fun onBindViewHolder(holder: CollectionListViewHolder, position: Int) {
        if (selectionTriggerAction == SelectionTriggerAction.ON_CLICK && !isInSelectionState) {
            isInSelectionState = true
        }

        val item = getItem(position)
        holder.bind(item)
        setListeners(holder, item)
    }

    private fun setListeners(
        holder: CollectionListViewHolder,
        item: Collection
    ) {
        if (selectionTriggerAction == SelectionTriggerAction.ON_LONG_PRESS) {
            holder.itemView.setOnLongClickListener {
                if (!isInSelectionState) {
                    isInSelectionState = true
                    selectCollection(holder, item)
                }
                true
            }
        }

        holder.itemView.setOnClickListener {
            if (isInSelectionState) {
                selectCollection(holder, item)
            } else {
                callback?.onClick(item)
            }
        }
    }

    private fun selectCollection(
        holder: CollectionListViewHolder, collection: Collection
    ) = adapterScope.launch {
        val containedItem = selectedCollections.contains(collection.id)
        if (containedItem) {
            selectedCollections.remove(collection.id)
        } else {
            selectedCollections.add(collection.id)
        }
        withContext(Dispatchers.Main) {
            holder.itemView.isSelected = !containedItem
        }
    }

    fun finishSelection() {
        adapterScope.launch {
            val collections = selectedCollections.toList()
            isInSelectionState = false
            selectedCollections.clear()

            withContext(Dispatchers.Main) {
                (callback as? Callback)?.onSelectionFinished(collections)
            }
        }
    }
    class Callback(
        onClickFn: (Collection) -> Unit,
        private val onSelectionFinished: (List<Long>) -> Unit
    ) : CollectionListAdapter.Callback(onClickFn) {
        fun onSelectionFinished(items: List<Long>) = onSelectionFinished.invoke(items)
    }
}