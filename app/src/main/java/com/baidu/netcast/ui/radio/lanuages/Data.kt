package com.baidu.netcast.ui.radio.lanuages


import com.baidu.netcast.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(
    @SerializedName("_id")
    override val id: String,
    @SerializedName("iso_639")
    val iso639: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("stationcount")
    val stationcount: Int,
    @SerializedName("type")
    val type: String
) : ListAdapterItem, Serializable