package hcmus.android.gallery1.ui.adapters.recyclerview

import hcmus.android.gallery1.data.Item
import kotlinx.coroutines.*

class SelectableItemListAdapter(
    isCompactLayout: Boolean = false,
    private val selectionTriggerAction: SelectionTriggerAction = SelectionTriggerAction.ON_LONG_PRESS,
    callback: Callback? = null,
) : ItemListAdapter(isCompactLayout, callback) {

    enum class SelectionTriggerAction {
        ON_LONG_PRESS,
        ON_CLICK
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default + Job())
    private val selectedCollections = mutableSetOf<Long>()
    private var isInSelectionState = false

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        if (selectionTriggerAction == SelectionTriggerAction.ON_CLICK && !isInSelectionState) {
            isInSelectionState = true
        }

        val item = getItem(position)
        holder.bind(item)
        setListeners(holder, item)
    }

    private fun setListeners(
        holder: ItemListViewHolder,
        item: Item
    ) {
        if (selectionTriggerAction == SelectionTriggerAction.ON_LONG_PRESS) {
            holder.itemView.setOnLongClickListener {
                if (!isInSelectionState) {
                    isInSelectionState = true
                    selectItem(holder, item)
                }
                true
            }
        }

        holder.itemView.setOnClickListener {
            if (isInSelectionState) {
                selectItem(holder, item)
            } else {
                callback?.onClick(item, holder.bindingAdapterPosition)
            }
        }
    }

    private fun selectItem(
        holder: ItemListViewHolder, item: Item
    ) = adapterScope.launch {
        val containedItem = selectedCollections.contains(item.id)
        if (containedItem) {
            selectedCollections.remove(item.id)
        } else {
            selectedCollections.add(item.id)
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
        onClickFn: (Item, Int) -> Unit,
        private val onSelectionFinished: (List<Long>) -> Unit
    ) : ItemListAdapter.Callback(onClickFn) {
        fun onSelectionFinished(items: List<Long>) = onSelectionFinished.invoke(items)
    }
}