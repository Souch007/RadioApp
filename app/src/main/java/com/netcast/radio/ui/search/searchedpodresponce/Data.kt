package com.netcast.radio.ui.search.searchedpodresponce


import com.netcast.radio.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName

data class Data(
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
    val itunesId: Int,
    @SerializedName("language")
    val language: String,
    @SerializedName("newestItemPublishTime")
    val newestItemPublishTime: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("trendScore")
    val trendScore: Int,
    @SerializedName("url")
    val url: String
) :ListAdapterItem , java.io.Serializable