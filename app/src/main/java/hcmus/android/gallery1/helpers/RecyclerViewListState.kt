package hcmus.android.gallery1.helpers

// this might be a stupid idea
sealed class RecyclerViewListState {

    class ItemInserted(val position: Int) : RecyclerViewListState()
    class ItemRemoved(val position: Int) : RecyclerViewListState()
    object DataSetChanged : RecyclerViewListState()

}