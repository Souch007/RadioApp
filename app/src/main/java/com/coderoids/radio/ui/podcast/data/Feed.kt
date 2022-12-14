package com.coderoids.radio.ui.podcast.data

import com.coderoids.radio.interfaces.ListAdapterItem

data class Feed(
    val artwork: String,
    val author: String,
    val categories: Categories,
    val contentType: String,
    val crawlErrors: Int,
    val dead: Int,
    val description: String,
    val generator: String,
    override val id: Long,
    val image: String,
    val imageUrlHash: Long,
    val itunesId: Int,
    val language: String,
    val lastCrawlTime: Int,
    val lastGoodHttpStatusTime: Int,
    val lastHttpStatus: Int,
    val lastParseTime: Int,
    val lastUpdateTime: Int,
    val link: String,
    val locked: Int,
    val originalUrl: String,
    val ownerName: String,
    val parseErrors: Int,
    val title: String,
    val type: Int,
    val url: String,
) : ListAdapterItem , java.io.Serializable