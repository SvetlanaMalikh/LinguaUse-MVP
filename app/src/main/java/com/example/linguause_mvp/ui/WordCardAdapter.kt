package com.example.lingua_use_mvp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lingua_use_mvp.R
import com.example.lingua_use_mvp.model.WordCard

class WordCardAdapter(
    private val items: List<WordCard>
) : RecyclerView.Adapter<WordCardAdapter.WordCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word_card, parent, false)
        return WordCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordCardViewHolder, position: Int) {
        val wordCard = items[position]
        holder.bind(wordCard)
    }

    override fun getItemCount(): Int = items.size

    inner class WordCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordText: TextView = itemView.findViewById(R.id.word_text)

        fun bind(card: WordCard) {
            wordText.text = card.word
            // можно добавить отображение вариантов перевода
        }
    }
}