package com.baidu.netcast.ui.search.frequentsearch


import com.baidu.netcast.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(
    @SerializedName("count")
    val count: Int,
    @SerializedName("_id")
    override val id: String,
    @SerializedName("q")
    val q: String,
    @SerializedName("type")
    val type: String
)  : ListAdapterItem, Serializable