package com.netcast.baidutv.ui.radio.lanuages


import com.google.gson.annotations.SerializedName

data class Lanuages(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)