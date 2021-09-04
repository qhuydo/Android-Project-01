package hcmus.android.gallery1.utilities

import hcmus.android.gallery1.data.CustomAlbum
import hcmus.android.gallery1.data.CustomAlbumInfo
import hcmus.android.gallery1.data.CustomAlbumItem
import hcmus.android.gallery1.data.Favourite

val testFavouriteItem = Favourite(id = 0L)

val testCustomAlbum1 = CustomAlbum(
    CustomAlbumInfo(name = "a", id = 1L),
    listOf(
        CustomAlbumItem(1L),
        CustomAlbumItem(2L),
        CustomAlbumItem(3L),
    )
)

val testCustomAlbum2 = CustomAlbum(
    CustomAlbumInfo(name = "b", id = 2L),
    listOf(
        CustomAlbumItem(4L),
        CustomAlbumItem(5L),
        CustomAlbumItem(6L)
    )
)


val testCustomAlbums = listOf(
    testCustomAlbum1,
    testCustomAlbum2
)