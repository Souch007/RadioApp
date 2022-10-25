package com.coderoids.radio.ui.podcast.poddata


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.coderoids.radio.interfaces.ListAdapterItem

@Keep
data class PodResponce(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)