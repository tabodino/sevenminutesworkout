package fr.wesy.sevenminutesworkout.domain.service

import android.os.CountDownTimer
import fr.wesy.sevenminutesworkout.util.Constants.COUNTDOWN_INTERVAL
import fr.wesy.sevenminutesworkout.util.Constants.REST_TIME
import javax.inject.Inject

class TimerManager @Inject constructor() {
    private var countDownTimer: CountDownTimer? = null
    private var timeRemaining: Long = 0L
    private var isPaused = false

    fun startTimer(exerciseDuration: Long,
                   onTick: (Long) -> Unit, onFinish: () -> Unit) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(exerciseDuration, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                onTick(millisUntilFinished)
            }
            override fun onFinish() {
                onFinish()
            }
        }.start()
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        isPaused = true
    }

    fun startRestTimer(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(REST_TIME, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                onTick(millisUntilFinished)
            }
            override fun onFinish() {
                onFinish()
            }
        }.start()
    }

    fun resumeRestTimer(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        if (isPaused && timeRemaining > 0L) {
            countDownTimer = object : CountDownTimer(timeRemaining, COUNTDOWN_INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemaining = millisUntilFinished
                    onTick(millisUntilFinished)
                }
                override fun onFinish() {
                    onFinish()
                }
            }.start()
            isPaused = false
        }
    }

    fun cancelTimer() {
        countDownTimer?.cancel()
    }
}