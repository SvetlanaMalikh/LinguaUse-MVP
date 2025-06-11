package com.example.linguause_mvp.data

import com.example.linguause_mvp.model.GrammarSlot
import com.example.linguause_mvp.model.GrammarSlotType
import com.example.linguause_mvp.model.GrammarStructure

// Список доступных грамматических шаблонов
object GrammarTemplates {

    // Пример: Present Simple Negative
    val presentSimpleNegative = GrammarStructure(
        id = "present_simple_negative",
        level = "A",                         // Внутренний уровень
        displayLevel = "A1–A2",              // Отображаемый для клиента
        name = "Present Simple Negative",
        slots = listOf(
            GrammarSlot(GrammarSlotType.SUBJECT),
            GrammarSlot(GrammarSlotType.NEGATION, fixedValue = "don't"),
            GrammarSlot(GrammarSlotType.VERB),
            GrammarSlot(GrammarSlotType.OBJECT)
        )
    )

    // Добавим другие шаблоны позже
    val allTemplates = listOf(
        presentSimpleNegative
    )
}
