package com.example.linguause_mvp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.linguause_mvp.databinding.FragmentWordGameBinding

class WordGameFragment : Fragment() {

    private var _binding: FragmentWordGameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordGameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.wordText.text = word.word
        }

        viewModel.options.observe(viewLifecycleOwner) { options ->
            if (options.size >= 4) {
                binding.option1.text = options[0]
                binding.option2.text = options[1]
                binding.option3.text = options[2]
                binding.option4.text = options[3]
            }
        }

        // 👇 наблюдаем за счетчиком
        viewModel.score.observe(viewLifecycleOwner) { score ->
            binding.scoreText.text = "Счёт: $score"
        }

        val buttons = listOf(binding.option1, binding.option2, binding.option3, binding.option4)
        buttons.forEach { button ->
            button.setOnClickListener { onOptionClicked(button) }
        }
    }

    private fun onOptionClicked(button: Button) {
        val selected = button.text.toString()
        viewModel.checkAnswer(selected)

        if (selected == viewModel.currentWord.value?.translation) {
            button.setBackgroundColor(Color.parseColor("#7ED321"))
        } else {
            button.setBackgroundColor(Color.parseColor("#E30A21"))
            // Сбрасываем цвет через 0.5 сек
            button.postDelayed({
                button.setBackgroundColor(Color.parseColor("#6200EE"))
            }, 500)
        }

        // Загружаем новую карточку через 1 сек
        button.postDelayed({
            resetButtons()
            viewModel.loadNewCard()
        }, 1000)
    }

    private fun resetButtons() {
        val buttons = listOf(binding.option1, binding.option2, binding.option3, binding.option4)
        buttons.forEach { button ->
            button.setBackgroundColor(Color.parseColor("#6200EE"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}