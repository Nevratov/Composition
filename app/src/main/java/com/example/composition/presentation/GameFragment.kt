package com.example.composition.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.presentation.viewmodel.GameFragmentViewModel

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    private lateinit var level: Level
    private lateinit var viewModel: GameFragmentViewModel

    private val options = ArrayList<TextView>()
    private var constProgress = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[GameFragmentViewModel::class.java]
        viewModel.getGameSettings(level)
        initParams()
        observable()
        optionsOnClickListener()

        viewModel.generateQuestion()
        viewModel.startTimer()

    }

    private fun optionsOnClickListener() {
        for (option in options) {
            option.setOnClickListener {
                val answer = (it as TextView).text.toString()
                viewModel.checkAnswer(answer)
                viewModel.generateQuestion()
            }
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container_fragment, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    private fun changeProgressBar() {
        val progress = viewModel.getPercentRightAnswers()
        val progressBar = binding.progressBarAnswers
        progressBar.progress = progress
        progressBar.progressTintList = if (progress > constProgress) {
            ColorStateList.valueOf(Color.GREEN)
        } else {
            ColorStateList.valueOf(Color.RED)
        }
    }

    private fun initParams() {
        with(binding) {
            with(options) {
                add(tvOption1)
                add(tvOption2)
                add(tvOption3)
                add(tvOption4)
                add(tvOption5)
                add(tvOption6)
            }
        }
        constProgress = viewModel.gameSettings.value?.minPercentOfRightAnswers ?: 0
        binding.progressBarAnswers.secondaryProgress = constProgress
    }


    private fun observable() {
        viewModel.question.observe(viewLifecycleOwner) {
            binding.tvSum.text = it.sum.toString()
            binding.tvLeftNumber.text = it.visibleNumber.toString()

            for ((index, option) in it.options.withIndex()) {
                options[index].text = option.toString()
            }

            binding.tvRightAnswers.text = viewModel.setupRightAnswers(requireContext())
            changeProgressBar()
        }

        viewModel.gameResult.observe(viewLifecycleOwner) {
            viewModel.gameResult.value?.let { gameResult ->
                launchGameFinishedFragment(gameResult)
            }
        }

        viewModel.timer.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }

    }

    private fun parseArgs() {
        val args = requireArguments()
        args.getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }

    companion object {
        const val NAME = "GameFragment"
        private const val KEY_LEVEL = "level"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }
}