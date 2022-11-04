package com.coderoids.radio.ui.radioplayermanager.episodedata


import com.coderoids.radio.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("chaptersUrl")
    val chaptersUrl: Any,
    @SerializedName("dateCrawled")
    val dateCrawled: Int,
    @SerializedName("datePublished")
    val datePublished: Int,
    @SerializedName("datePublishedPretty")
    val datePublishedPretty: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("enclosureLength")
    val enclosureLength: Int,
    @SerializedName("enclosureType")
    val enclosureType: String,
    @SerializedName("enclosureUrl")
    val enclosureUrl: String,
    @SerializedName("episode")
    val episode: Any,
    @SerializedName("episodeType")
    val episodeType: String,
    @SerializedName("explicit")
    val explicit: Int,
    @SerializedName("feedDead")
    val feedDead: Int,
    @SerializedName("feedDuplicateOf")
    val feedDuplicateOf: Any,
    @SerializedName("feedId")
    val feedId: Int,
    @SerializedName("feedImage")
    val feedImage: String,
    @SerializedName("feedItunesId")
    val feedItunesId: Int,
    @SerializedName("feedLanguage")
    val feedLanguage: String,
    @SerializedName("guid")
    override val id: String,
    @SerializedName("id")
    val _id: Long,
    @SerializedName("image")
    val image: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("season")
    val season: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("transcriptUrl")
    val transcriptUrl: Any,
) : ListAdapterItem , java.io.Serializable