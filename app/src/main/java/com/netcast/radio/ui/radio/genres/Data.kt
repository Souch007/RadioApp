package com.netcast.radio.ui.radio.genres


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.netcast.radio.interfaces.ListAdapterItem

@Keep
data class Data(
    @SerializedName("_id")
    override val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("stationcount")
    val stationcount: Int,
    @SerializedName("type")
    val type: String
) : ListAdapterItem , java.io.Serializable