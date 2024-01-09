package com.netcast.baidutv.ui.radio.data.temp

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.netcast.baidutv.interfaces.ListAdapterItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//@Entity(tableName = "RadioStations")
data class RadioLists(

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
) : ListAdapterItem, Serializable
