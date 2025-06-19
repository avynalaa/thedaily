package com.example.thedaily.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.thedaily.utils.DatabaseMigrations

@Database(
    entities = [CharacterProfile::class, ChatMessage::class], 
    version = 5, // Updated to include null collection fix
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun characterProfileDao(): CharacterProfileDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "the_daily_database"
                )
                    .addMigrations(*DatabaseMigrations.getAllMigrations())
                    .fallbackToDestructiveMigration(true) // Allow destructive migration as fallback
                    .enableMultiInstanceInvalidation() // Sync across multiple instances
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        fun closeDatabase() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
        
        // Helper method to clear all data
        suspend fun clearAllData(context: Context) {
            val db = getDatabase(context)
            db.clearAllTables()
        }
    }
}
