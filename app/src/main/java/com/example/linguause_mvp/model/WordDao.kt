package com.example.linguause_mvp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.*


@Dao
interface WordDao {

    // 📊 Количество всех слов в базе (для проверки, загружен ли словарь)
    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    // 📥 Вставка списка слов (из CSV)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    // 📄 Получить все слова (если нужно отладить)
    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<WordEntity>

    // 🔍 Поиск по части речи (например: "verb")
    @Query("SELECT * FROM words WHERE partOfSpeech = :type")
    suspend fun getWordsByType(type: String): List<WordEntity>

    // 🔍 Поиск по слову или переводу
    @Query("SELECT * FROM words WHERE word LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%'")
    suspend fun searchWords(query: String): List<WordEntity>

    // ✅ Получить только изучаемые слова (isStudying = true)
    @Query("SELECT * FROM words WHERE isStudying = 1")
    suspend fun getStudyingWords(): List<WordEntity>

    // 🧠 Получить слова из активного словаря
    @Query("SELECT * FROM words WHERE isInVocabulary = 1")
    suspend fun getActiveVocabulary(): List<WordEntity>

    // 🧩 Слова по уровню (например "A2")
    @Query("SELECT * FROM words WHERE level = :level")
    suspend fun getWordsByLevel(level: String): List<WordEntity>

    // 🎯 По теме (например: "travel")
    @Query("SELECT * FROM words WHERE topic = :topic")
    suspend fun getWordsByTopic(topic: String): List<WordEntity>

    // ✏️ Обновить слово (используется для изменения флагов)
    @Update
    suspend fun updateWord(word: WordEntity)
}