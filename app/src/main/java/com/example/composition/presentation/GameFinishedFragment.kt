package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entity.GameResult
import java.util.Locale

class GameFinishedFragment : Fragment() {

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")

    private lateinit var gameResult: GameResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonPlayAgain.setOnClickListener {
            retryGame()
        }
        onBackPressedClickListenner()

        with(binding) {
            with(gameResult) {

                tvRequiredCountCorrectAnswers.text = String.format(
                    Locale.getDefault(),
                    tvRequiredCountCorrectAnswers.text.toString(),
                    gameSettings.minCountOfRightAnswers.toString()
                )

                tvScore.text = String.format(
                    Locale.getDefault(),
                    tvScore.text.toString(),
                    countOfRightAnswers.toString()
                )

                tvRequiredPercentCorrectAnswers.text = String.format(
                    Locale.getDefault(),
                    tvRequiredPercentCorrectAnswers.text.toString(),
                    gameSettings.minPercentOfRightAnswers.toString()
                )

                tvPercentCorrectAnswers.text = String.format(
                    Locale.getDefault(),
                    tvPercentCorrectAnswers.text.toString(),
                    (countOfRightAnswers.toDouble() / countOfQuestions * 100).toString()
                )

                if (winner) {
                    ivEmojiResult.setImageResource(R.drawable.ic_smile)
                } else {
                    ivEmojiResult.setImageResource(R.drawable.ic_sad)
                }
            }
        }


    }

    private fun onBackPressedClickListenner() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                retryGame()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun retryGame() {
        requireActivity().supportFragmentManager.popBackStack(
            GameFragment.NAME,
            POP_BACK_STACK_INCLUSIVE
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs() {
        val args = requireArguments()
        args.getParcelable<GameResult>(KEY_GAME_RESULT)?.let {
            gameResult = it
        }
    }

    companion object {
        private const val KEY_GAME_RESULT = "game_result "

        fun newInstance(gameResult: GameResult): GameFinishedFragment {
            return GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_GAME_RESULT, gameResult)
                }
            }
        }
    }
}