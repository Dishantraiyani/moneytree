package com.moneytree.app.database

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.moneytree.app.repository.network.responses.NSUserResponse

object NSTypeConverter {
    @TypeConverter
    fun fromStringUser(value: String?): NSUserResponse {
        val listType = object : TypeToken<NSUserResponse?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromUserList(list: NSUserResponse?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}