package com.netcast.radio.ui.radio.data.temp


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Data(
    @SerializedName("Dance")
    val classical: List<RadioLists>,
    @SerializedName("estación")
    val estación: List<RadioLists>,
    @SerializedName("Other")
    val music: List<RadioLists>,
    @SerializedName("méxico")
    val méxico: List<RadioLists>,
    @SerializedName("Talk")
    val news: List<RadioLists>,
    @SerializedName("Pop")
    val pop: List<RadioLists>,
    @SerializedName("Community")
    val publicRadio: List<RadioLists>,
    @SerializedName("radio")
    val radio: List<RadioLists>,
    @SerializedName("Rock")
    val rock: List<RadioLists>,
    @SerializedName("podcasts")
    val podcasts: List<podcastsItem>,
    @SerializedName("Decades")
    val talk: List<RadioLists>
)