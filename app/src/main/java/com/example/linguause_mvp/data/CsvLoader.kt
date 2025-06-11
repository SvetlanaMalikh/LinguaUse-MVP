package com.example.linguause_mvp.data

import android.content.Context
import com.example.linguause_mvp.model.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import com.example.linguause_mvp.model.WordDao


object CsvLoader {

    /**
     * Загружает слова из assets/words.csv в БД, если ещё не загружены
     */
    suspend fun loadIfNeeded(context: Context, dao: WordDao) {
        val count = dao.getWordCount()
        if (count > 0) {
            android.util.Log.d("DEBUG_FLOW", "📦 База уже заполнена, загрузка не требуется")
            return
        }

        val words = parseCsv(context)
        dao.insertWords(words)
        android.util.Log.d("DEBUG_FLOW", "✅ Загружено слов: ${words.size}")
    }

    /**
     * Чтение и разбор файла words.csv (6 полей)
     */
    private suspend fun parseCsv(context: Context): List<WordEntity> = withContext(Dispatchers.IO) {
        val wordList = mutableListOf<WordEntity>()
        val inputStream = context.assets.open("words.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.forEachLine { line ->
            val parts = line.split(",")

            if (parts.size >= 6) {
                val word = parts[0].trim()
                val translation = parts[1].trim()
                val transcription = parts[2].trim()
                val partOfSpeech = parts[3].trim()
                val level = parts[4].trim()
                val topic = parts[5].trim()

                wordList.add(
                    WordEntity(
                        word = word,
                        translation = translation,
                        transcription = transcription,
                        partOfSpeech = partOfSpeech,
                        level = level,
                        topic = topic,
                        isStudying = false,       // ещё не начал учить
                        isInVocabulary = true     // пока все активны
                    )
                )
            }
        }

        reader.close()
        return@withContext wordList
    }
}