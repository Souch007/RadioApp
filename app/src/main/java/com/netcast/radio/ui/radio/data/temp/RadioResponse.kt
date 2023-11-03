package com.netcast.radio.ui.radio.data.temp


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity

@Keep

data class RadioResponse(
    @ColumnInfo(name = "data", defaultValue ="")
    @SerializedName("data")
    val `data`: Data,
    @ColumnInfo(name = "success", defaultValue ="")
    @SerializedName("success")
    val success: Boolean
)