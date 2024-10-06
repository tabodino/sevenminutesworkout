package fr.wesy.sevenminutesworkout.domain.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.wesy.sevenminutesworkout.domain.model.Workout
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
     @ApplicationContext private val context: Context
) : WorkoutRepository {

    override fun getWorkouts(): List<Workout> {
        val jsonString = loadJSONFromAsset(context, "workouts.json")
        val workoutsType = object : TypeToken<List<Workout>>() {}.type
        return Gson().fromJson(jsonString, workoutsType)
    }

    private fun loadJSONFromAsset(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}