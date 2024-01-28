package io.b101.picashow.api.image

import com.google.gson.annotations.SerializedName

data class DownloadItem(
    @SerializedName("url")
    val url: String,

    @SerializedName("phone_number")
    val phone_number: String
)
