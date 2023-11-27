package com.example.composition.data

import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.repository.GameRepository
import kotlin.math.max
import kotlin.random.Random

object GameRepositoryImpl : GameRepository {

    private const val SUM_MIN_VALUE = 3
    private const val VISIBLE_NUMBER_MIN_VALUE = 1
    private const val RANGE_BETWEEN_ANSWER = 3

    override fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question {
        val sum = Random.nextInt(SUM_MIN_VALUE, maxSumValue + 1)
        val visibleNumber = Random.nextInt(VISIBLE_NUMBER_MIN_VALUE, sum)
        val rightAnswer = sum - visibleNumber
        val options = hashSetOf<Int>()
        options.add(rightAnswer)
        while (options.size < countOfOptions) {
            val option = Random.nextInt(
                VISIBLE_NUMBER_MIN_VALUE,
                 maxSumValue
            )
            options.add(option)
        }
        return Question(sum, visibleNumber, options.toList())
    }

    override fun getGameSettings(level: Level): GameSettings {
        return when (level) {
            Level.TEST -> {
                GameSettings(
                    10,
                    3,
                    50,
                    8
                )
            }

            Level.EASY -> {
                GameSettings(
                    10,
                    3,
                    70,
                    60
                )
            }

            Level.NORMAL -> {
                GameSettings(
                    20,
                    20,
                    80,
                    40
                )
            }

            Level.HARD -> {
                GameSettings(
                    30,
                    30,
                    90,
                    30
                )
            }
        }
    }
}