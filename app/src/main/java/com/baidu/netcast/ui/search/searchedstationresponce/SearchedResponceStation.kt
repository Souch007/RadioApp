package com.baidu.netcast.ui.search.searchedstationresponce


import com.google.gson.annotations.SerializedName

data class SearchedResponceStation(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)