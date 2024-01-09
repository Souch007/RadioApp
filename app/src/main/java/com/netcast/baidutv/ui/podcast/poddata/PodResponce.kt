package com.netcast.baidutv.ui.podcast.poddata


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PodResponce(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)