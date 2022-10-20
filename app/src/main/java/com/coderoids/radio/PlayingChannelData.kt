package com.coderoids.radio

import com.coderoids.radio.interfaces.ListAdapterItem
import java.io.Serializable

data class PlayingChannelData(
    val url: String,
    val favicon: String,
    val name: String,
    override val id : String,
    val country : String,
    val type: String,
    ) : ListAdapterItem, Serializable
