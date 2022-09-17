package com.coderoids.radio.ui.radio.data


import com.coderoids.radio.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RadioData(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean,
    override val id: Long = 0
) : ListAdapterItem, Serializable