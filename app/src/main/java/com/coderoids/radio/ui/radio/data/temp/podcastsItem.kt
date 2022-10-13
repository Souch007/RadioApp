package com.coderoids.radio.ui.radio.data.temp


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class podcastsItem(
    @SerializedName("artwork")
    val artwork: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("categories")
    val categories: Array<String>,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val _id: String,
    @SerializedName("id")
    val id: Int,
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
)