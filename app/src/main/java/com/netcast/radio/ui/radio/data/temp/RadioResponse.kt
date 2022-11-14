package com.netcast.radio.ui.radio.data.temp


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class RadioResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("success")
    val success: Boolean
)