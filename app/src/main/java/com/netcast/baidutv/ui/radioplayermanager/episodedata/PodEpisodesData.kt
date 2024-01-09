package com.netcast.baidutv.ui.radioplayermanager.episodedata


import com.google.gson.annotations.SerializedName

data class PodEpisodesData(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)