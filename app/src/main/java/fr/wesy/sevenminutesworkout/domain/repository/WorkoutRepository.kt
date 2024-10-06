package fr.wesy.sevenminutesworkout.domain.repository

import fr.wesy.sevenminutesworkout.domain.model.Workout

interface WorkoutRepository {
    fun getWorkouts(): List<Workout>
}