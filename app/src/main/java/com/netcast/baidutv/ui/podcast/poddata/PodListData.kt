package com.netcast.baidutv.ui.podcast.poddata

import androidx.room.ColumnInfo
import com.netcast.baidutv.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName

data class PodListData(

    @ColumnInfo(name = "_id", defaultValue = "")
    @SerializedName("_id")
    override  val id: String,

    @ColumnInfo(name = "id", defaultValue = "")
    @SerializedName("id")
    val _id: String,

    @ColumnInfo(name = "image", defaultValue = "")
    @SerializedName("image")
    val image: String,

    @ColumnInfo(name = "listennotes_url", defaultValue = "")
    @SerializedName("listennotes_url")
    val listennotesUrl: String,

    @ColumnInfo(name = "title", defaultValue = "")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "publisher", defaultValue = "")
    @SerializedName("publisher")
    val publisher: String,

    @ColumnInfo(name = "website", defaultValue = "")
    @SerializedName("website")
    val website: String
) : ListAdapterItem, java.io.Serializable
