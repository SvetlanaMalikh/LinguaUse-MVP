package com.example.linguause_mvp.model

// Типы грамматических элементов в шаблоне
enum class GrammarSlotType {
    SUBJECT, VERB, NEGATION, OBJECT, AUXILIARY
}

// Один элемент шаблона (например, "Subject" или "don't")
data class GrammarSlot(
    val type: GrammarSlotType,
    val fixedValue: String? = null // если фиксированное слово, например "don't"
)

// Описание грамматической структуры
data class GrammarStructure(
    val id: String,
    val level: String,           // "A", "B", "C" — для фильтрации
    val displayLevel: String,    // "A1–A2", "B1–B2", "C1–C2" — для отображения в UI
    val name: String,
    val slots: List<GrammarSlot>
)
