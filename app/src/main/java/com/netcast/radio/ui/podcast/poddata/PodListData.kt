package com.netcast.radio.ui.podcast.poddata

import com.netcast.radio.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName

data class PodListData(
    @SerializedName("artwork")
    val artwork: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("categories")
    val categories: List<String>,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    override val id: String,
    @SerializedName("id")
    val _id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("itunesId")
    val itunesId: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("trendScore")
    val trendScore: String,
    @SerializedName("url")
    val url: String
) : ListAdapterItem, java.io.Serializable
