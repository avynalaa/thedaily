package com.example.thedaily.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
    
    @TypeConverter
    fun fromReplyConfig(replyConfig: ReplyConfig?): String? {
        return Gson().toJson(replyConfig)
    }

    @TypeConverter
    fun toReplyConfig(replyConfigString: String?): ReplyConfig? {
        return replyConfigString?.let {
            Gson().fromJson(it, ReplyConfig::class.java)
        }
    }
    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String {
        return status.name
    }

    @TypeConverter
    fun toMessageStatus(status: String): MessageStatus {
        return MessageStatus.valueOf(status)
    }
}
