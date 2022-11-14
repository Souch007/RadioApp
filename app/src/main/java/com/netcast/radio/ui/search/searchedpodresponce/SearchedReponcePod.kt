package com.netcast.radio.ui.search.searchedpodresponce


import com.google.gson.annotations.SerializedName

data class SearchedReponcePod(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)