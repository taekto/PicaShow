package io.b101.picashow.api.image

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("last_page_num")
    val lastPageNum: Int,
    @SerializedName("list")
    val list: List<ImageItem>
)

