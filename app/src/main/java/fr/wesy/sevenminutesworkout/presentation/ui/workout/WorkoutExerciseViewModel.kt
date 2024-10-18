package fr.wesy.sevenminutesworkout.presentation.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.wesy.sevenminutesworkout.domain.model.Exercise
import fr.wesy.sevenminutesworkout.domain.model.Workout
import fr.wesy.sevenminutesworkout.domain.service.TimerManager
import fr.wesy.sevenminutesworkout.util.Constants.MILLIS_TIME
import fr.wesy.sevenminutesworkout.util.Constants.REST_TIME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class WorkoutExerciseViewModel @Inject constructor(
    private val timerManager: TimerManager
) : ViewModel() {

    private val _timeRemaining = MutableLiveData<Long>()

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> = _timerText

    private val _isPaused = MutableLiveData<Boolean>()
    val isPaused: LiveData<Boolean> get() = _isPaused

    private val _isRestPhase = MutableLiveData<Boolean>()
    val isRestPhase: LiveData<Boolean> get() = _isRestPhase

    private val _isWorkoutCompleted = MutableLiveData<Boolean>()
    val isWorkoutCompleted: LiveData<Boolean> get() = _isWorkoutCompleted

    private val _currentTime = MutableLiveData<Long>()

    private val _currentExerciseIndex = MutableLiveData<Int>()
    val currentExerciseIndex: LiveData<Int> get() = _currentExerciseIndex

    private val _exerciseTitle = MutableLiveData<String>()
    val exerciseTitle: LiveData<String> get() = _exerciseTitle

    private val _exerciseImageUrl = MutableLiveData<String>()
    val exerciseImageUrl: LiveData<String> get() = _exerciseImageUrl

    private val _progressBarMax = MutableLiveData<Int>()
    val progressBarMax: LiveData<Int> get() = _progressBarMax

    private val _progressBarProgress = MutableLiveData<Int>()
    val progressBarProgress: MutableLiveData<Int> get() = _progressBarProgress

    private lateinit var workout: Workout
    private var exerciseDuration: Long = 0

    init {
        progressBarProgress.value = 0
    }

    fun startWorkout(initialWorkout: Workout) {
        workout = initialWorkout
        _currentExerciseIndex.value = 0
        startExerciseTimer(workout.exercises[0])
    }

    /**
     * We work with separate timer to avoid conflict with rest and exercise duration
     */
    private fun startExerciseTimer(exercise: Exercise) {
        _exerciseTitle.value = exercise.title

        exerciseDuration = exercise.duration * MILLIS_TIME

        _timeRemaining.value = exerciseDuration
        _currentTime.value = exercise.duration.toLong()
        _progressBarMax.value = exercise.duration
        _progressBarProgress.value = 0
        _exerciseImageUrl.value = exercise.imageUrl

        timerManager.cancelTimer()

        _isRestPhase.value = false
        _isPaused.value = false

        timerManager.startTimer(exerciseDuration, onTick = { millisUntilFinished ->
            val roundedMillis = roundUpToNextThousand(millisUntilFinished)
            _currentTime.postValue((millisUntilFinished / 1000))  // UI Timer
            _timerText.postValue(_currentTime.value.toString())
            _progressBarProgress.value = ((exerciseDuration - roundedMillis) / 1000).toInt()
        }, onFinish = {
            if (_currentExerciseIndex.value == workout.exercises.size - 1) {
                _isWorkoutCompleted.value = true
            } else {
                // To make the transition smoother
                CoroutineScope(Dispatchers.Main).launch {
                    resetDisplayTimer()
                    _progressBarMax.value = exercise.duration
                    delay(1000)
                    startRestTimer()
                }
            }
        })
    }

    fun togglePauseResumeTimer() {
        // Toggle the pause/resume state
        _isPaused.value = _isPaused.value != true
        // Pause the timer
        if (_isPaused.value == true) {
            _timeRemaining.value = _currentTime.value?.let {
                roundUpToNextThousand(it.times(MILLIS_TIME))
            }
            timerManager.pauseTimer()
        }
        // Resume the timer
        else {
            val remainingTime = _timeRemaining.value

            if (_isRestPhase.value == true) {
                // Resume rest phase
                timerManager.resumeRestTimer(onTick = { millisUntilFinished ->
                    val roundedMillis = roundUpToNextThousand(millisUntilFinished)
                    _timeRemaining.postValue(roundedMillis)
                    _timerText.postValue(((roundedMillis / 1000)).toString())
                    _progressBarProgress.postValue(((exerciseDuration - roundedMillis) / 1000).toInt())
                }, onFinish = {
                    goToNextExercise()
                })
            } else {
                // Resume exercise phase
                if (remainingTime != null) {
                    startExerciseTimerWithRemainingTime(remainingTime)
                }
            }
        }
        // Notify the UI about the new pause state
        _isPaused.postValue(_isPaused.value)
    }

    private fun startRestTimer() {
        _isRestPhase.value = true
        _progressBarMax.value = (REST_TIME / MILLIS_TIME).toInt()
        timerManager.startRestTimer(
            onTick = { millisUntilFinished ->
                val roundedMillis = roundUpToNextThousand(millisUntilFinished)
                _timeRemaining.value = roundedMillis
                _timerText.value = ((roundedMillis / 1000)).toString()
                _progressBarProgress.value = ((REST_TIME - roundedMillis) / 1000).toInt()
            },
            onFinish = {
                // To make the transition smoother
                CoroutineScope(Dispatchers.Main).launch {
                    resetDisplayTimer()
                    delay(1000)
                    goToNextExercise()
                }
            }
        )
    }

    private fun startExerciseTimerWithRemainingTime(remainingTime: Long) {
        timerManager.cancelTimer()
        timerManager.startTimer(remainingTime, onTick = { millisUntilFinished ->
            val roundedMillis = roundUpToNextThousand(millisUntilFinished)
            _currentTime.postValue((roundedMillis / 1000) - 1) // UI Timer
            _timerText.postValue(_currentTime.value.toString())
            _progressBarProgress.postValue(((exerciseDuration - millisUntilFinished) / 1000).toInt())
        }, onFinish = {
            startRestTimer()
        })
    }

    private fun resetDisplayTimer() {
        _timerText.value = "0"
        _progressBarProgress.postValue(0)
    }

    fun goToPreviousExercise() {
        val currentIndex = _currentExerciseIndex.value ?: 0
        if (currentExerciseIndex.value!! > 0) {
            _currentExerciseIndex.value = currentIndex - 1
            startExerciseTimer(workout.exercises[currentIndex - 1])
        }
    }

    fun goToNextExercise() {
        val currentIndex = _currentExerciseIndex.value ?: 0
        if (currentIndex < workout.exercises.size - 1) {
            _currentExerciseIndex.value = currentIndex + 1
            startExerciseTimer(workout.exercises[currentIndex + 1])
        } else {
            _isWorkoutCompleted.value = true
        }
    }

    private fun roundUpToNextThousand(timeInMillis: Long): Long {
        return ceil(timeInMillis / 1000.0).toLong() * 1000
    }

    override fun onCleared() {
        timerManager.cancelTimer()
        super.onCleared()
    }
}