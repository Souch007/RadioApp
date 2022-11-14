package com.netcast.radio.ui.radioplayermanager.episodedata


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netcast.radio.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName
@Entity(tableName = "OfflineEpisodes")
data class Data(
    @ColumnInfo(name = "chaptersUrl" , defaultValue = "")
    @SerializedName("chaptersUrl")
    val chaptersUrl: String,

    @ColumnInfo(name = "dateCrawled", defaultValue = "")
    @SerializedName("dateCrawled")
    val dateCrawled: Int,

    @ColumnInfo(name = "datePublished", defaultValue = "")
    @SerializedName("datePublished")
    val datePublished: Int,

    @ColumnInfo(name = "datePublishedPretty", defaultValue = "")
    @SerializedName("datePublishedPretty")
    val datePublishedPretty: String,

    @ColumnInfo(name = "description", defaultValue = "")
    @SerializedName("description")
    val description: String,

    @ColumnInfo(name = "duration", defaultValue = "")
    @SerializedName("duration")
    val duration: Int,

    @ColumnInfo(name = "enclosureLength", defaultValue = "")
    @SerializedName("enclosureLength")
    val enclosureLength: Int,

    @ColumnInfo(name = "enclosureType", defaultValue = "")
    @SerializedName("enclosureType")
    val enclosureType: String,

    @ColumnInfo(name = "enclosureUrl", defaultValue = "")
    @SerializedName("enclosureUrl")
    val enclosureUrl: String,

    @ColumnInfo(name = "episode", defaultValue = "")
    @SerializedName("episode")
    val episode: String,

    @ColumnInfo(name = "episodeType", defaultValue = "")
    @SerializedName("episodeType")
    val episodeType: String,

    @ColumnInfo(name = "explicit", defaultValue = "")
    @SerializedName("explicit")
    val explicit: Int,

    @ColumnInfo(name = "feedDead", defaultValue = "")
    @SerializedName("feedDead")
    val feedDead: Int,

    @ColumnInfo(name = "feedDuplicateOf", defaultValue = "")
    @SerializedName("feedDuplicateOf")
    val feedDuplicateOf: String,

    @ColumnInfo(name = "feedId", defaultValue = "")
    @SerializedName("feedId")
    val feedId: Int,

    @ColumnInfo(name = "feedImage", defaultValue = "")
    @SerializedName("feedImage")
    val feedImage: String,

    @ColumnInfo(name = "feedItunesId",defaultValue = "")
    @SerializedName("feedItunesId")
    val feedItunesId: Int,

    @ColumnInfo(name = "feedLanguage",defaultValue = "")
    @SerializedName("feedLanguage")
    val feedLanguage: String,
    @ColumnInfo(name = "guid",defaultValue = "")
    @SerializedName("guid")
    override val id: String,

    @PrimaryKey
    @ColumnInfo(name = "id",defaultValue = "")
    @SerializedName("id")
    val _id: Long,

    @ColumnInfo(name = "image",defaultValue = "")
    @SerializedName("image")
    val image: String,

    @ColumnInfo(name = "link",defaultValue = "")
    @SerializedName("link")
    val link: String,

    @ColumnInfo(name = "season",defaultValue = "")
    @SerializedName("season")
    val season: Int,

    @ColumnInfo(name = "title",defaultValue = "")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "transcriptUrl",defaultValue = "")
    @SerializedName("transcriptUrl")
    val transcriptUrl: String,

    @ColumnInfo(name = "fileURI",defaultValue = "")
    var fileURI : String
) : ListAdapterItem , java.io.Serializable