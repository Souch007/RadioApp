package com.netcast.radio.ui.podcast.poddata


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Data(
    @SerializedName("Politics")
    val business: List<PodListData>,
    @SerializedName("Wrestling")
    val culture: List<PodListData>,
    @SerializedName("Shopping")
    val education: List<PodListData>,
    @SerializedName("Podcasting")
    val fitness: List<PodListData>,
    @SerializedName("Gadgets")
    val health: List<PodListData>,
    @SerializedName("Wilderness")
    val news: List<PodListData>,
    @SerializedName("Running")
    val religion: List<PodListData>,
    @SerializedName("Society")
    val society: List<PodListData>,
    @SerializedName("Spirituality")
    val spirituality: List<PodListData>,
    @SerializedName("Sports")
    val sports: List<PodListData>
)