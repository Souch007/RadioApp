package com.netcast.baidutv.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netcast.baidutv.ui.podcast.poddata.PodListData
import java.lang.reflect.Type


class PodConverters {
    @TypeConverter
    fun fromPodList(countryLang: List<PodListData>?): String? {
        if (countryLang == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<PodListData>>() {}.type
        return gson.toJson(countryLang, type)
    }


    @TypeConverter
    fun toPodList(countryLangString: String?): List<PodListData>? {
        if (countryLangString.isNullOrEmpty()) {
            return listOf()
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<PodListData?>?>() {}.type
        return gson.fromJson<List<PodListData>>(countryLangString, type)
    }



//    @TypeConverter
//    fun fromList(list: List<RadioLists>?): String {
//        return gson.toJson(list)
//    }
//
//    @TypeConverter
//    fun toList(json: String?): List<RadioLists> {
//        if (json == null) {
//            return emptyList()
//        }
//        Log.d("MyDatabase", "toList: $json")
//        val listType = object : TypeToken<List<RadioLists>>() {}.type
//        return gson.fromJson(json, listType)
//    }


}