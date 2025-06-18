package com.example.thedaily.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(characterProfile: CharacterProfile)

    @Update
    suspend fun update(characterProfile: CharacterProfile)

    @Delete
    suspend fun delete(characterProfile: CharacterProfile)

    @Query("SELECT * FROM character_profiles WHERE id = :id")
    suspend fun getCharacterById(id: Long): CharacterProfile?

    @Query("SELECT * FROM character_profiles ORDER BY name ASC")
    fun getAllCharacters(): Flow<List<CharacterProfile>>
}
