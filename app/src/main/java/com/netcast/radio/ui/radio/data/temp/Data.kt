package com.netcast.radio.ui.radio.data.temp


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Data(
    @SerializedName("classical")
    val classical: List<RadioLists>,
    @SerializedName("estación")
    val estación: List<RadioLists>,
    @SerializedName("music")
    val music: List<RadioLists>,
    @SerializedName("méxico")
    val méxico: List<RadioLists>,
    @SerializedName("news")
    val news: List<RadioLists>,
    @SerializedName("pop")
    val pop: List<RadioLists>,
    @SerializedName("public radio")
    val publicRadio: List<RadioLists>,
    @SerializedName("radio")
    val radio: List<RadioLists>,
    @SerializedName("rock")
    val rock: List<RadioLists>,
    @SerializedName("podcasts")
    val podcasts: List<podcastsItem>,
    @SerializedName("talk")
    val talk: List<RadioLists>
)