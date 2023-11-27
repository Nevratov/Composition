package com.example.composition.presentation.viewmodel

import android.content.Context
import android.os.CountDownTimer
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
import java.util.Locale

class GameFragmentViewModel : ViewModel() {

    private val repository = GameRepositoryImpl
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)

    private var _gameSettings = MutableLiveData<GameSettings>()
    val gameSettings: LiveData<GameSettings> // НАДО ЛИ?
        get() = _gameSettings

    private var _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private var _countOfRightAnswers = MutableLiveData(0)
    val countOfRightAnswers: LiveData<Int>
        get() = _countOfRightAnswers

    private var totalQuestions: Int = 0

    private var _timer = MutableLiveData<String>()
    val timer: LiveData<String>
        get() = _timer

    private var _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    fun getGameSettings(level: Level) {
        _gameSettings.value = getGameSettingsUseCase(level)
    }

    fun generateQuestion() {
        val maxSumValue =
            _gameSettings.value?.maxSumValue ?: throw RuntimeException("maxSumValue == null")
        _question.value = generateQuestionUseCase(maxSumValue)
        totalQuestions++
    }

    fun checkAnswer(answer: String) {
        if (isRightAnswer(answer)) {
            _countOfRightAnswers.value?.let {
                _countOfRightAnswers.value = it + 1
            }
        }
    }

    fun setupRightAnswers(context: Context): String {
        val pattern = context.getString(R.string.right_answers)
        return String.format(
            Locale.getDefault(),
            pattern,
            _countOfRightAnswers.value,
            _gameSettings.value?.minCountOfRightAnswers
        )
    }

    private fun getRightAnswer(): Int {
        return _question.value?.let {
            it.sum - it.visibleNumber
        } ?: throw RuntimeException("rightAnswer == null")
    }

    private fun isRightAnswer(answer: String): Boolean {
        val rightAnswer = getRightAnswer()
        return try {
            answer.toInt() == rightAnswer
        } catch (e: NumberFormatException) {
            throw NumberFormatException("answer - $answer is not digit")
        }
    }

    fun getPercentRightAnswers(): Int {
        val right = _countOfRightAnswers.value
            ?: throw RuntimeException("_countOfRightAnswers == null")

        return ((right.toDouble() / totalQuestions) * 100).toInt()
    }

    fun startTimer() {
        val gameTime: Long = _gameSettings.value?.let {
            it.gameTimeInSeconds.toLong() * 1000
        } ?: throw RuntimeException("gameTime == null")

        val timer = object : CountDownTimer(gameTime, INTERVAL_1_SEC) {

            override fun onTick(millisUntilFinished: Long) {
                val min = millisUntilFinished / 1000 / 60
                val sec = millisUntilFinished / 1000 % 60
                _timer.value = "%02d:%02d".format(Locale.getDefault(), min, sec)
            }

            override fun onFinish() {
                _gameResult.value = GameResult(
                    isWinner(),
                    _countOfRightAnswers.value ?: UNDEFINED_COUNT_OF_RIGHT_ANSWER,
                    totalQuestions,
                    _gameSettings.value ?: UNDEFINED_GAME_SETTINGS
                )
            }
        }
        timer.start()
    }

    private fun isWinner(): Boolean {
        return getPercentRightAnswers() >= (gameSettings.value?.minPercentOfRightAnswers
            ?: throw RuntimeException("gameSettings.value?.minPercentOfRightAnswers == null"))
    }

    companion object {
        private const val INTERVAL_1_SEC: Long = 1000
        private const val UNDEFINED_COUNT_OF_RIGHT_ANSWER = -1
        private val UNDEFINED_GAME_SETTINGS = GameSettings(
            -1,
            -1,
            -1,
            -1)
    }

}