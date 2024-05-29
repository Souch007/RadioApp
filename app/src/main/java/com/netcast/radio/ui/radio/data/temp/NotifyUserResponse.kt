package com.netcast.radio.ui.radio.data.temp

import com.google.gson.annotations.SerializedName

data class NotifyUserResponse(
    val success: Boolean,
    val message: String,
    val response: Response,
)

data class Response(
    val success: Long,
    val user: User,
)

data class User(
    @SerializedName("_id")
    val id: String,
    @SerializedName("app_in_time")
    val appInTime: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("country_name")
    val countryName: String,
    @SerializedName("app_out_time")
    val appOutTime: String,
    @SerializedName("activity_logs")
    val activityLogs: Any?,
    @SerializedName("city_name")
    val cityName: Any?,
)

