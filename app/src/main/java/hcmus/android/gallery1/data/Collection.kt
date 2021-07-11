package hcmus.android.gallery1.data

data class Collection (
    val id: Long,
    val name: String,
    val type: String,
    val thumbnailUri: String,
    var itemCount: Int,

    // Lazy-load fields
    var items: List<Item> = emptyList()
)
