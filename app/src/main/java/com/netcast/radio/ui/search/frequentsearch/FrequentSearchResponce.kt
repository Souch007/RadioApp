package com.netcast.radio.ui.search.frequentsearch


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
@Keep
data class FrequentSearchResponce(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)