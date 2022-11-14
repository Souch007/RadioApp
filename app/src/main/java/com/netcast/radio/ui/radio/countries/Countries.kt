package com.netcast.radio.ui.radio.countries


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Countries(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)