package com.example.linguause_mvp.logic

import com.example.linguause_mvp.data.UserVocabulary
import com.example.linguause_mvp.model.GrammarSlot
import com.example.linguause_mvp.model.GrammarSlotType
import com.example.linguause_mvp.model.GrammarStructure
import com.example.linguause_mvp.model.WordEntity

class SentenceGenerator(
    private val grammarStructure: GrammarStructure,
    private val availableWords: List<WordEntity>
) {

    data class GeneratedSentence(
        val correctSequence: List<String>,       // Правильный порядок слов
        val options: List<String>,               // Все слова для выбора (включая ловушки)
        val correctAnswer: String                // Строка готового предложения
    )

    fun generate(): GeneratedSentence {
        val correctWords = mutableListOf<String>()
        val requiredTypes = mutableListOf<String>()

        // 1. Проходим по каждому элементу шаблона и подбираем слово
        for (slot in grammarStructure.slots) {
            when {
                // Если есть фиксированное значение (например, don't) — добавляем его как есть
                slot.fixedValue != null -> {
                    correctWords.add(slot.fixedValue)
                }

                slot.type == GrammarSlotType.SUBJECT -> {
                    val subject = pickSubject() ?: error("Нет подходящего SUBJECT")
                    correctWords.add(subject)
                }

                slot.type == GrammarSlotType.VERB -> {
                    val verb = pickWordByPart("verb") ?: error("Нет подходящего VERB")
                    correctWords.add(verb)
                }

                slot.type == GrammarSlotType.OBJECT -> {
                    val obj = pickWordByPart("noun") ?: error("Нет подходящего OBJECT")
                    correctWords.add(obj)
                }

                else -> {
                    // Поддержка других типов слотов при расширении
                }
            }
        }

        // 2. Генерируем ловушки
        val trapWords = generateTraps(correctWords)

        // 3. Объединяем и перемешиваем
        val allOptions = (correctWords + trapWords).shuffled()

        return GeneratedSentence(
            correctSequence = correctWords,
            options = allOptions,
            correctAnswer = correctWords.joinToString(" ")
        )
    }

    // Выбор субъекта: местоимение или существительное
    private fun pickSubject(): String? {
        val candidates = availableWords.filter {
            it.partOfSpeech == "noun" || it.partOfSpeech == "pronoun"
        }

        return candidates.randomOrNull()?.word
    }

    // Выбор слова по части речи
    private fun pickWordByPart(part: String): String? {
        return availableWords.firstOrNull { it.partOfSpeech == part }?.word
    }

    // Генерация ловушек: изменённые формы слов и похожие по типу
    private fun generateTraps(correctWords: List<String>): List<String> {
        val traps = mutableSetOf<String>()

        for (word in correctWords) {
            if (word.endsWith("e")) traps.add(word + "d")
            if (word.endsWith("y")) traps.add(word.dropLast(1) + "ies")
            traps.add(word + "s")
            traps.add(word + "ing")
        }

        // Также добавим случайные слова из словаря пользователя
        val randomExtras = availableWords.map { it.word }
            .filterNot { it in correctWords }
            .shuffled()
            .take(3)

        traps.addAll(randomExtras)

        return traps.shuffled().take(4) // не больше 4 ловушек
    }
}