package hcmus.android.gallery1.data

data class Item(
    val id: Long,
    var isPopulated: Boolean = false,

    // Lazy-load fields
    private var uri: String = "",
    var dateModified: Long = 0,
    var fileName: String = "",
    var fileSize: Long = 0,
    var filePath: String = "",
    var width: Int = 0,
    var height: Int = 0
) {
    // Fetch URI
    fun getUri(): String {
        if (uri.isEmpty()) {
            uri = "$DEFAULT_CONTENT_URI/$id"
        }
        return uri
    }

    // Populate all other fields in this Item itself
    fun populate() {
        if (!isPopulated) {
            isPopulated = true
        }
    }
}
