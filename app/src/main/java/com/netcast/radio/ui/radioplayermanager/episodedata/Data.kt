package com.netcast.radio.ui.radioplayermanager.episodedata


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netcast.radio.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName
@Entity(tableName = "OfflineEpisodes")
data class Data(
    @ColumnInfo(name = "enclosureUrl", defaultValue = "")
    @SerializedName("audio")
    val audio: String,

    @ColumnInfo(name = "duration", defaultValue = "")
    @SerializedName("audio_length_sec")
    val audioLengthSec: Int,

    @ColumnInfo(name = "description", defaultValue = "")
    @SerializedName("description")
    val description: String,
    @SerializedName("explicit_content")
    val explicitContent: Boolean,

    @ColumnInfo(name = "guid", defaultValue = "")
    @SerializedName("guid_from_rss")
    val guidFromRss: String,

    @PrimaryKey
    @ColumnInfo(name = "id", defaultValue = "")
    @SerializedName("id")
    override val id: String,

    @ColumnInfo(name = "feedImage", defaultValue = "")
    @SerializedName("image")
    val feedImage: String,
    @ColumnInfo(name = "link", defaultValue = "")
    @SerializedName("link")
    val link: String,
    @SerializedName("listennotes_edit_url")
    val listennotesEditUrl: String,
    @SerializedName("listennotes_url")
    val listennotesUrl: String,
    @SerializedName("maybe_audio_invalid")
    val maybeAudioInvalid: Boolean,
    @SerializedName("pub_date_ms")
    val pubDateMs: Long,
    @SerializedName("thumbnail")
    val thumbnail: String,

    @ColumnInfo(name = "title", defaultValue = "")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "fileURI", defaultValue = "")
    var fileURI: String,

 ) : ListAdapterItem, java.io.Serializable