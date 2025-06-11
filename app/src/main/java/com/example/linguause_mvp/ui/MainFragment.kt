package com.example.linguause_mvp.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.linguause_mvp.R

class MainFragment : Fragment(R.layout.fragment_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_words).setOnClickListener {
            findNavController().navigate(R.id.wordGameFragment)
        }

        view.findViewById<Button>(R.id.btn_sentences).setOnClickListener {
            findNavController().navigate(R.id.sentenceGameFragment)
        }

        view.findViewById<Button>(R.id.btn_dictionary).setOnClickListener {
            findNavController().navigate(R.id.dictionaryFragment)
        }
    }
}