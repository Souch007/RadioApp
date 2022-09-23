package com.coderoids.radio.ui.podcast.data

import com.coderoids.radio.interfaces.ListAdapterItem

data class PodcastData(
    val count: Int,
    val description: String,
    val feeds: List<Feed>,
    val query: String,
    val status: String,
    override val id: String
)  : ListAdapterItem, java.io.Serializable