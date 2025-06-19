package com.example.thedaily.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterProfileDao {
    @Query("SELECT * FROM character_profiles ORDER BY name ASC")
    fun getAllCharacters(): Flow<List<CharacterProfile>>

    @Query("SELECT * FROM character_profiles WHERE id = :id")
    suspend fun getCharacterById(id: Long): CharacterProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(characterProfile: CharacterProfile): Long

    @Update
    suspend fun update(characterProfile: CharacterProfile)

    @Delete
    suspend fun delete(characterProfile: CharacterProfile)

    @Query("DELETE FROM character_profiles WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM character_profiles")
    suspend fun deleteAll()

    @Query("SELECT * FROM character_profiles WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentCharacter(): CharacterProfile?

    @Query("UPDATE character_profiles SET isCurrent = 0")
    suspend fun clearCurrentCharacter()

    @Query("UPDATE character_profiles SET isCurrent = (id = :characterId)")
    suspend fun setCurrentCharacter(characterId: Long)

    @Query("UPDATE character_profiles SET affectionLevel = :affection, mood = :mood, relationshipContext = :context, relationshipHistory = :history WHERE id = :id")
    suspend fun updateAffectionAndContext(
        id: Long,
        affection: Double,
        mood: String,
        context: String,
        history: String
    )
}
