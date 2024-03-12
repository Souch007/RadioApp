package com.netcast.radio.ui.radio.data.temp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netcast.radio.interfaces.ListAdapterItem
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

    @ColumnInfo(name = "secondaryUrl", defaultValue = "")
    @SerializedName("secondaryUrl")
    val secondaryUrl: String,

    @SerializedName("isBlocked")
    val isBlocked: Boolean,

    @SerializedName("description")
    val description: String,

    ) : ListAdapterItem, Serializable
