package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.useCases.GenerateQuestionUseCase
import com.example.composition.domain.useCases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level,
) : ViewModel() {

    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private lateinit var gameSettings: GameSettings

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private val _timerField = MutableLiveData<String>()
    val timerField: LiveData<String>
        get() = _timerField

    private var timer: CountDownTimer? = null

    private val _rightAnswersField = MutableLiveData<String>()
    val rightAnswersField: LiveData<String>
        get() = _rightAnswersField

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _winByCount = MutableLiveData<Boolean>()
    val winByCount: LiveData<Boolean>
        get() = _winByCount

    private val _winByPercent = MutableLiveData<Boolean>()
    val winByPercent: LiveData<Boolean>
        get() = _winByPercent

    private var countTotalAnswers = 0
    private var countOfRightAnswers = 0

    init {
        startGame()
    }

    private fun startGame() {
        getGameSettings(level)
        generateNewQuestion()
        updateRightAnswersField()
        startTimer()
    }

    private fun getGameSettings(level: Level) {
        gameSettings = getGameSettingsUseCase(level)
    }

    private fun generateNewQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    fun checkAnswer(answer: Int) {
        if (answer == question.value?.rightAnswer) {
            countOfRightAnswers++
        }
        countTotalAnswers++
        updateRightAnswersField()
        calculatePercentOfRightAnswers()
        calculateWinByCount()
        calculateWinByPercent()
        generateNewQuestion()
    }

    private fun updateRightAnswersField() {
        val field = application.getString(R.string.right_answers)
        _rightAnswersField.value =
            String.format(field, countOfRightAnswers, gameSettings.minCountOfRightAnswers)
    }

    private fun startTimer() {
        val gameTime = gameSettings.gameTimeInSeconds * MILLIS_IN_SEC
        timer = object : CountDownTimer(gameTime, MILLIS_IN_SEC) {
            override fun onTick(millisUntilFinished: Long) {
                _timerField.value = formatTimeField(millisUntilFinished)
            }

            override fun onFinish() {
                _gameResult.value = finishGame()
            }
        }
        timer?.start()
    }

    private fun calculatePercentOfRightAnswers() {
        _percentOfRightAnswers.value =
            (countOfRightAnswers / countTotalAnswers.toDouble() * 100).toInt()
    }

    private fun calculateWinByPercent() {
        percentOfRightAnswers.value?.let {
            _winByPercent.value = it >= gameSettings.minPercentOfRightAnswers
        }
    }

    private fun calculateWinByCount() {
        _winByCount.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
    }

    private fun finishGame(): GameResult {
        return GameResult(
            winByCount.value == true && winByPercent.value == true,
            countOfRightAnswers,
            countTotalAnswers,
            gameSettings
        )
    }

    private fun formatTimeField(millisUntilFinished: Long): String {
        val sec = millisUntilFinished / 1000
        val minutes = sec / 60
        val seconds = sec % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLIS_IN_SEC = 1000L
    }
}