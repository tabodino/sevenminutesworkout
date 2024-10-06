package fr.wesy.sevenminutesworkout.presentation.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.wesy.sevenminutesworkout.domain.model.Workout
import fr.wesy.sevenminutesworkout.domain.repository.WorkoutRepository
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    fun loadWorkouts() {
        _workouts.value = workoutRepository.getWorkouts()
    }
}