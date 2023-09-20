package com.netcast.radio

import com.netcast.radio.interfaces.ListAdapterItem
import java.io.Serializable

data class PlayingChannelData(
    val url: String?,
    val favicon: String?,
    val name: String?,
    override val id : String,
    val idPodcast : String?,
    val country : String?,
    val type: String?,
    var isSelected:Boolean=false
    ) : ListAdapterItem, Serializable
