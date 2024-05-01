package com.netcast.radio.ui.radio.data.temp


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.netcast.radio.db.Converters

@Keep
@Entity(tableName = "RadioStations")
@TypeConverters(Converters::class)
data class Data(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "classical", defaultValue = "")
    @SerializedName("Rock")
    val classical: List<RadioLists>,
    @ColumnInfo(name = "music", defaultValue = "")
    @SerializedName("Hits")
    val music: List<RadioLists>,
    @ColumnInfo(name = "news", defaultValue = "")
    @SerializedName("News-Talk")
    val news: List<RadioLists>,
    @ColumnInfo(name = "pop", defaultValue = "")
    @SerializedName("Pop")
    val pop: List<RadioLists>,
    @ColumnInfo(name = "publicRadio", defaultValue = "")
    @SerializedName("localStations")
    val publicRadio: List<RadioLists>,

)