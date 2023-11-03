package com.netcast.radio.ui.radio.data.temp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netcast.radio.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//@Entity(tableName = "RadioStations")
data class RadioLists(
//    @SerializedName("bitrate")
//    val bitrate: String,
//    @SerializedName("changeuuid")
//    val changeuuid: String,
//    @SerializedName("clickcount")
//    val clickcount: String,
//    @SerializedName("clicktimestamp")
//    val clicktimestamp: String,
//    @SerializedName("clicktimestamp_iso8601")
//    val clicktimestampIso8601: String,
//    @SerializedName("clicktrend")
//    val clicktrend: String,
//    @SerializedName("codec")
//    val codec: String,

//    @SerializedName("countrycode")
//    val countrycode: String,



    @ColumnInfo(name = "country", defaultValue = "")
    @SerializedName("country")
    val country: String,

    @ColumnInfo(name = "favicon", defaultValue = "")
    @SerializedName("favicon")
    val favicon: String,

    @PrimaryKey
    @ColumnInfo(name = "_id", defaultValue = "")
    @SerializedName("_id")
    override val id: String,

    @ColumnInfo(name = "name", defaultValue = "")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "url", defaultValue = "")
    @SerializedName("url")
    val url: String,
//    @SerializedName("geo_lat")
//    val geoLat: String,
//    @SerializedName("geo_long")
//    val geoLong: String,
//    @SerializedName("has_extended_info")
//    val hasExtendedInfo: Boolean,
//    @SerializedName("hls")
//    val hls: String,
//    @SerializedName("homepage")
//    val homepage: String,

//    @SerializedName("language")
//    val language: List<String>,
//    @SerializedName("languagecodes")
//    val languagecodes: String,
//    @SerializedName("lastchangetime")
//    val lastchangetime: String,
//    @SerializedName("lastchangetime_iso8601")
//    val lastchangetimeIso8601: String,
//    @SerializedName("lastcheckok")
//    val lastcheckok: String,
//    @SerializedName("lastcheckoktime")
//    val lastcheckoktime: String,
//    @SerializedName("lastcheckoktime_iso8601")
//    val lastcheckoktimeIso8601: String,
//    @SerializedName("lastchecktime")
//    val lastchecktime: String,
//    @SerializedName("lastchecktime_iso8601")
//    val lastchecktimeIso8601: String,
//    @SerializedName("lastlocalchecktime")
//    val lastlocalchecktime: String,
//    @SerializedName("lastlocalchecktime_iso8601")
//    val lastlocalchecktimeIso8601: String,

//    @SerializedName("serveruuid")
//    val serveruuid: String,
//    @SerializedName("ssl_error")
//    val sslError: String,
//    @SerializedName("state")
//    val state: String,
//    @SerializedName("stationuuid")
//    val stationuuid: String,
//    @SerializedName("tags")
//    val tags: List<String>,

//    @SerializedName("url_resolved")
//    val urlResolved: String,
//    @SerializedName("votes")
//    val votes: String,
) : ListAdapterItem, Serializable
