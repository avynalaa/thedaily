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
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            val listType = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
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
    
    @TypeConverter
    fun fromRelationshipContext(context: RelationshipContext): String {
        return context.name
    }

    @TypeConverter
    fun toRelationshipContext(context: String): RelationshipContext {
        return try {
            RelationshipContext.valueOf(context)
        } catch (e: IllegalArgumentException) {
            RelationshipContext.STRANGERS // Default fallback
        }
    }
    
    @TypeConverter
    fun fromDeleteType(deleteType: DeleteType): String {
        return deleteType.name
    }

    @TypeConverter
    fun toDeleteType(deleteType: String): DeleteType {
        return try {
            DeleteType.valueOf(deleteType)
        } catch (e: IllegalArgumentException) {
            DeleteType.NONE // Default fallback
        }
    }
}
