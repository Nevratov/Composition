package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.useCases.GenerateQuestionUseCase
import com.example.composition.domain.useCases.GetGameSettingsUseCase

class GameViewModel(
    private val level: Level,
    private val application: Application,
) : ViewModel() {

    private val repository = GameRepositoryImpl

    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)

    private val gameSettings by lazy {
        getGameSettingsUseCase(level)
    }

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question


    private val _timerField = MutableLiveData<String>()
    val timerField: LiveData<String>
        get() = _timerField

    private lateinit var timer: CountDownTimer

    private val _rightAnswersField = MutableLiveData<String>()
    val rightAnswersField: LiveData<String>
        get() = _rightAnswersField


    private var countQuestions = 0
    private var countRightAnswers = 0


    init {
        generateNewQuestion()
        startTimer()
    }

    private fun generateNewQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }


    fun checkAnswer(answer: Int) {
        if (answer == question.value?.rightAnswer) {
            countRightAnswers++
        }
        countQuestions++
        _rightAnswersField.value =getRightAnswersField()
    }

    private fun getRightAnswersField(): String {
        return String.format(
            application.getString(R.string.right_answers),
            getPercentRightAnswers(),
            gameSettings.minPercentOfRightAnswers
            )
    }

    private fun getPercentRightAnswers() =
        (countRightAnswers / countQuestions.toDouble() * 100).toInt()


    private fun startTimer() {
        val timeGame = gameSettings.gameTimeInSeconds * MILLIS_IN_SEC
        timer = object : CountDownTimer(timeGame, MILLIS_IN_SEC) {
            override fun onTick(millisUntilFinished: Long) {
                _timerField.value = getTime(millisUntilFinished)
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }
        timer.start()
    }

    private fun getTime(millisUntilFinished: Long): String {
        val sec = millisUntilFinished * 1000

        val minutes = sec / 60
        val seconds = sec % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    companion object {
        private const val MILLIS_IN_SEC = 1000L
    }
}

