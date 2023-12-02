package com.example.composition.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.composition.R
import com.example.composition.domain.entity.GameResult

@BindingAdapter("displayNumberAsString")
fun bindDisplayNumber(tv: TextView, number: Int) {
    tv.text = number.toString()
}

@BindingAdapter("onOptionClickListener")
fun bindOnOptionClick(tv: TextView, onClickListener: OnOptionClickListener) {
    tv.setOnClickListener {
        onClickListener.onOptionClick(tv.text.toString().toInt())
    }
}

interface OnOptionClickListener {
    fun onOptionClick(answer: Int)
}

@BindingAdapter("colorProgress")
fun bindColorProgress(progressBar: ProgressBar, win: Boolean) {
    progressBar.progressTintList = ColorStateList.valueOf(getColor(win))
}

@BindingAdapter("colorTextByPerformance")
fun bindColorTextByPerformance(tv: TextView, win: Boolean) {
    tv.setTextColor(getColor(win))
}

private fun getColor(win: Boolean): Int {
    return if (win) {
        Color.GREEN
    } else {
        Color.RED
    }
}

@BindingAdapter("emojiResult")
fun bindEmojiResult(iv: ImageView, win: Boolean) {
    iv.setImageResource(getEmojiId(iv, win))
}

private fun getEmojiId(iv: ImageView, win: Boolean): Int {
    return if (win) {
        R.drawable.ic_smile
    } else {
        R.drawable.ic_sad
    }
}

@BindingAdapter("requiredCountCorrectAnswersText")
fun bindRequiredCountCorrectAnswersText(tv: TextView, requiredCount: Int) {
    tv.text = String.format(
        tv.context.getString(R.string.required_count_of_correct_answers),
        requiredCount
    )
}

@BindingAdapter("scoreText")
fun bindScoreText(tv: TextView, score: Int) {
    tv.text = String.format(
        tv.context.getString(R.string.your_score),
        score
    )
}

@BindingAdapter("requiredPercentCorrectAnswersText")
fun bindRequiredPercentCorrectAnswersText(tv: TextView, percent: Int) {
    tv.text = String.format(
        tv.context.getString(R.string.required_percent_correct_answers),
        percent
    )
}

@BindingAdapter("percentCorrectAnswersText")
fun bindPercentCorrectAnswersText(tv: TextView, gameResult: GameResult) {
    tv.text = String.format(
        tv.context.getString(R.string.percent_correct_answers),
        getPercentRightAnswers(gameResult)
    )
}

private fun getPercentRightAnswers(gameResult: GameResult): Int {
    return (gameResult.countOfRightAnswers / gameResult.countOfQuestions.toDouble() * 100).toInt()
}


