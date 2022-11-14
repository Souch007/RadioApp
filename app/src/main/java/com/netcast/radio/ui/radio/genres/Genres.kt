package com.netcast.radio.ui.radio.genres


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Genres(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)