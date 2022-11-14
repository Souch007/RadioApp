package com.netcast.radio.ui.podcast.poddata


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Data(
    @SerializedName("Business")
    val business: List<PodListData>,
    @SerializedName("Culture")
    val culture: List<PodListData>,
    @SerializedName("Education")
    val education: List<PodListData>,
    @SerializedName("Fitness")
    val fitness: List<PodListData>,
    @SerializedName("Health")
    val health: List<PodListData>,
    @SerializedName("News")
    val news: List<PodListData>,
    @SerializedName("Religion")
    val religion: List<PodListData>,
    @SerializedName("Society")
    val society: List<PodListData>,
    @SerializedName("Spirituality")
    val spirituality: List<PodListData>,
    @SerializedName("Sports")
    val sports: List<PodListData>
)