package com.netcast.radio.ui.search.frequentsearch


import com.netcast.radio.interfaces.ListAdapterItem
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