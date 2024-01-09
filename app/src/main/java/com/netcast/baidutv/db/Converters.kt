package com.netcast.baidutv.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netcast.baidutv.ui.radio.data.temp.RadioLists
import java.lang.reflect.Type


class Converters {
    @TypeConverter
    fun fromRadioList(countryLang: List<RadioLists>?): String? {
        if (countryLang == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<RadioLists>>() {}.type
        return gson.toJson(countryLang, type)
    }


    @TypeConverter
    fun toRadioList(countryLangString: String?): List<RadioLists>? {
        if (countryLangString.isNullOrEmpty()) {
            return listOf()
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<RadioLists?>?>() {}.type
        return gson.fromJson<List<RadioLists>>(countryLangString, type)
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