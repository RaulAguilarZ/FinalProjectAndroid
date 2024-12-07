package com.example.newfinalproiect.DataB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranslationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: TranslationEntity)

    @Query("DELETE FROM translations_table WHERE id = :translationId")
    suspend fun deleteTranslation(translationId: Int)

    @Query("SELECT * FROM translations_table")
    suspend fun getAllTranslations(): List<TranslationEntity>
}