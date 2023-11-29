package com.example.composition.presentation

import android.content.res.ColorStateList
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
import com.example.composition.domain.entity.Level

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    private val gameViewModelFactory by lazy {
        GameViewModelFactory(requireActivity().application, level)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, gameViewModelFactory)[GameViewModel::class.java]
    }

    private lateinit var level: Level

    private val options = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater,  container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initParams()
        optionsOnClickListeners()
        observeViewModel()
    }

    private fun optionsOnClickListeners() {
        for (option in options) {
            option.setOnClickListener {
                val answer = option.text.toString().toInt()
                viewModel.checkAnswer(answer)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.question.observe(viewLifecycleOwner) {
            binding.tvSum.text = it.sum.toString()
            binding.tvLeftNumber.text = it.visibleNumber.toString()

            for (i in options.indices) {
                options[i].text = it.options[i].toString()
            }
        }

        viewModel.timerField.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }

        viewModel.rightAnswersField.observe(viewLifecycleOwner) {
            binding.tvRightAnswers.text = it
        }

        viewModel.percentOfRightAnswers.observe(viewLifecycleOwner) {
            binding.progressBarAnswers.setProgress(it, true)
        }

        viewModel.winByCount.observe(viewLifecycleOwner) {
            binding.tvRightAnswers.setTextColor(getColor(it))
        }

        viewModel.winByPercent.observe(viewLifecycleOwner) {
            binding.progressBarAnswers.progressTintList = ColorStateList.valueOf(getColor(it))
        }

        viewModel.gameResult.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(it)
        }
    }

    private fun getColor(value :Boolean): Int {
        val colorId = if (value) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return requireActivity().getColor(colorId)
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container_fragment, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }


    private fun initParams() {
        with(binding) {
            options.add(tvOption1)
            options.add(tvOption2)
            options.add(tvOption3)
            options.add(tvOption4)
            options.add(tvOption5)
            options.add(tvOption6)
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