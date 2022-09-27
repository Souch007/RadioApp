package com.coderoids.radio.ui.radio.lanuages


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("_id")
    val id: String,
    @SerializedName("iso_639")
    val iso639: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("stationcount")
    val stationcount: Int,
    @SerializedName("type")
    val type: String
)