package com.netcast.radio.ui.podcast.poddata


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import com.netcast.radio.db.PodConverters

@Keep
@Entity(tableName = "PodStations")
@TypeConverters(PodConverters::class)
data class Data(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "business", defaultValue = "")
    @SerializedName("Politics")
    val business: List<PodListData>,

    @ColumnInfo(name = "culture", defaultValue = "")
    @SerializedName("Wrestling")
    val culture: List<PodListData>,

    @ColumnInfo(name = "education", defaultValue = "")
    @SerializedName("Shopping")
    val education: List<PodListData>,

    @ColumnInfo(name = "fitness", defaultValue = "")
    @SerializedName("Podcasting")
    val fitness: List<PodListData>,

    @ColumnInfo(name = "health", defaultValue = "")
    @SerializedName("Gadgets")
    val health: List<PodListData>,

    @ColumnInfo(name = "news", defaultValue = "")
    @SerializedName("Wilderness")
    val news: List<PodListData>,

    @ColumnInfo(name = "religion", defaultValue = "")
    @SerializedName("Running")
    val religion: List<PodListData>,
)