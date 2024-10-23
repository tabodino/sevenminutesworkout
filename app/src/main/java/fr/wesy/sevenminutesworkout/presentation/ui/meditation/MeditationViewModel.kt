package fr.wesy.sevenminutesworkout.presentation.ui.meditation

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.wesy.sevenminutesworkout.R
import fr.wesy.sevenminutesworkout.domain.service.TimerManager
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MeditationViewModel @Inject constructor(
    private val timerManager: TimerManager
): ViewModel() {

    private val _isSoundMuted = MutableLiveData<Boolean>()

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> = _timerText

    private val _isPaused = MutableLiveData<Boolean>()
    val isPaused: LiveData<Boolean> = _isPaused

    private val _isMeditationComplete = MutableLiveData<Boolean>()
    val isMeditationComplete: LiveData<Boolean> = _isMeditationComplete

    private var timeRemaining: Long = 0

    private var mediaPlayer: MediaPlayer? = null

    private var currentMusicPosition = 0

    fun startMeditation(duration: Long, playMusic: Boolean, context: Context) {
        _isMeditationComplete.value = false
        _isSoundMuted.value = false
        timeRemaining = duration * 60 * 1000
        _timerText.postValue(formatTime(timeRemaining / 1000))

        if (playMusic) {
            startMusic(context)
        }
        startMeditationTimer()
    }

    private fun startMeditationTimer() {
        timerManager.cancelTimer()
        timerManager.startTimer(timeRemaining, onTick = { millisUntilFinished ->
            timeRemaining = millisUntilFinished
            _timerText.postValue(formatTime(millisUntilFinished / 1000))
        }, onFinish = {
            _isMeditationComplete.postValue(true)
            stopMusic()
        })
    }
    // We pause also music if it played
    fun togglePauseResumeMeditation() {
        // Toggle the pause/resume state
        _isPaused.value = _isPaused.value != true
        if (_isPaused.value == true) {
            timerManager.pauseTimer()
            if (_isSoundMuted.value == false) {
                currentMusicPosition = mediaPlayer?.currentPosition ?: 0
                mediaPlayer?.pause()
            }
        } else {
            if (_isSoundMuted.value == false) {
                mediaPlayer?.seekTo(currentMusicPosition)
                mediaPlayer?.start()
            }
            startMeditationTimer()
        }
    }

    private fun startMusic(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.meditation_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun toggleSoundMute() {
        _isSoundMuted.value = _isSoundMuted.value != true
        // We keep music in memory
        if (_isSoundMuted.value == true) {
            currentMusicPosition = mediaPlayer?.currentPosition ?: 0
            mediaPlayer?.pause()
        } else {
            if (currentMusicPosition > 0) {
                mediaPlayer?.seekTo(currentMusicPosition)
                mediaPlayer?.start()
            }
        }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        timerManager.cancelTimer()
        stopMusic()
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }

}